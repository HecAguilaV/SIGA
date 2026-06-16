package com.siga.sales.client

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.siga.sales.BaseSalesIntegrationTest
import com.siga.sales.KafkaTestContainer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.util.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["eureka.client.enabled=false"]
)
class InventoryClientIntegrationTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var inventoryClient: InventoryClient

    companion object {
        private lateinit var wireMockServer: WireMockServer

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            wireMockServer = WireMockServer(wireMockConfig().dynamicPort())
            wireMockServer.start()
            configureFor("localhost", wireMockServer.port())
            registry.add("siga-inventory.url") { "http://localhost:${wireMockServer.port()}" }
        }
    }

    init {
        describe("InventoryClient Integration with manual WireMock") {

            it("should return true when stock is valid (200 OK true)") {
                val tenantId = UUID.randomUUID().toString()
                val sku = "PROD-001"
                val quantity = 5

                stubFor(
                    get(urlPathEqualTo("/api/products/validate-stock"))
                        .withHeader("X-Tenant-Id", equalTo(tenantId))
                        .withQueryParam("sku", equalTo(sku))
                        .withQueryParam("quantity", equalTo(quantity.toString()))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("true")
                        )
                )

                val result = inventoryClient.validateStock(tenantId, sku, quantity)
                result shouldBe true
            }

            it("should return false when stock is insufficient (200 OK false)") {
                val tenantId = UUID.randomUUID().toString()
                val sku = "PROD-002"
                val quantity = 100

                stubFor(
                    get(urlPathEqualTo("/api/products/validate-stock"))
                        .withHeader("X-Tenant-Id", equalTo(tenantId))
                        .withQueryParam("sku", equalTo(sku))
                        .withQueryParam("quantity", equalTo(quantity.toString()))
                        .willReturn(
                            aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("false")
                        )
                )

                val result = inventoryClient.validateStock(tenantId, sku, quantity)
                result shouldBe false
            }
        }
    }
}
