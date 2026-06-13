package com.siga.gateway

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=true",
        "spring.cloud.simple.discovery.enabled=true",
        "spring.cloud.discovery.client.simple.instances.siga-agent[0].uri=http://localhost:\${wiremock.server.port:0}",
        "spring.cloud.discovery.client.simple.instances.siga-auth[0].uri=http://localhost:\${wiremock.server.port:0}",
        "spring.cloud.discovery.client.simple.instances.siga-inventory[0].uri=http://localhost:\${wiremock.server.port:0}",
        "spring.cloud.discovery.client.simple.instances.siga-sales[0].uri=http://localhost:\${wiremock.server.port:0}",
        "spring.cloud.discovery.client.simple.instances.siga-billing[0].uri=http://localhost:\${wiremock.server.port:0}"
    ]
)
class RoutingIntegrationTests {

    @LocalServerPort
    var port: Int = 0

    lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setup() {
        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    companion object {
        private lateinit var wireMockServer: WireMockServer

        @BeforeAll
        @JvmStatic
        fun startWireMock() {
            wireMockServer = WireMockServer(wireMockConfig().dynamicPort())
            wireMockServer.start()
            configureFor("localhost", wireMockServer.port())
            System.setProperty("wiremock.server.port", wireMockServer.port().toString())
        }

        @AfterAll
        @JvmStatic
        fun stopWireMock() {
            wireMockServer.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("wiremock.server.port") { wireMockServer.port() }
        }
    }

    @Test
    fun `context loads`() {
    }

    @Test
    fun `should route to siga-agent`() {
        stubFor(
            get(urlEqualTo("/api/agent/ping"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\": \"pong\"}")
                )
        )

        webTestClient.get()
            .uri("/api/agent/ping")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("pong")
    }

    @Test
    fun `should route and rewrite chat to siga-agent`() {
        stubFor(
            get(urlEqualTo("/api/agent/chat/message"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"reply\": \"hello\"}")
                )
        )

        webTestClient.get()
            .uri("/api/chat/message")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.reply").isEqualTo("hello")
    }

    @Test
    fun `should route and rewrite for siga-auth`() {
        stubFor(
            post(urlEqualTo("/api/v1/auth/login"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"token\": \"fake-token\"}")
                )
        )

        webTestClient.post()
            .uri("/api/auth/login")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("fake-token")
    }

    @Test
    fun `should route and rewrite for siga-inventory`() {
        // Products
        stubFor(
            get(urlEqualTo("/api/v1/products/P123"))
                .willReturn(aResponse().withStatus(200).withBody("product-data"))
        )
        webTestClient.get().uri("/api/products/P123").exchange().expectStatus().isOk.expectBody(String::class.java).isEqualTo("product-data")

        // Stores
        stubFor(
            get(urlEqualTo("/api/v1/stores/S001"))
                .willReturn(aResponse().withStatus(200).withBody("store-data"))
        )
        webTestClient.get().uri("/api/stores/S001").exchange().expectStatus().isOk.expectBody(String::class.java).isEqualTo("store-data")

        // Inventory direct
        stubFor(
            get(urlEqualTo("/api/v1/inventory/stock"))
                .willReturn(aResponse().withStatus(200).withBody("stock-data"))
        )
        webTestClient.get().uri("/api/inventory/stock").exchange().expectStatus().isOk.expectBody(String::class.java).isEqualTo("stock-data")
    }

    @Test
    fun `should route and rewrite for siga-sales`() {
        // Sales
        stubFor(
            get(urlEqualTo("/api/v1/sales/history"))
                .willReturn(aResponse().withStatus(200).withBody("sales-history"))
        )
        webTestClient.get().uri("/api/sales/history").exchange().expectStatus().isOk.expectBody(String::class.java).isEqualTo("sales-history")

        // Cash shifts
        stubFor(
            post(urlEqualTo("/api/v1/cash-shifts/open"))
                .willReturn(aResponse().withStatus(201))
        )
        webTestClient.post().uri("/api/cash-shifts/open").exchange().expectStatus().isCreated
    }

    @Test
    fun `should route and rewrite for siga-billing and comercial`() {
        // Billing
        stubFor(
            get(urlEqualTo("/api/v1/billing/invoice/123"))
                .willReturn(aResponse().withStatus(200).withBody("invoice-123"))
        )
        webTestClient.get().uri("/api/billing/invoice/123").exchange().expectStatus().isOk.expectBody(String::class.java).isEqualTo("invoice-123")

        // Comercial (rewrites to billing)
        stubFor(
            get(urlEqualTo("/api/v1/billing/data"))
                .willReturn(aResponse().withStatus(200).withBody("comercial-data"))
        )
        webTestClient.get().uri("/api/comercial/data").exchange().expectStatus().isOk.expectBody(String::class.java).isEqualTo("comercial-data")
    }
}
