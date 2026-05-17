package com.siga.agent.controller

import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.A2UILayout
import com.siga.agent.service.A2UIEnvelopeResponse
import com.siga.agent.service.A2UIService
import com.siga.agent.service.SurfaceEnvelope
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class A2UIControllerTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var webTestClient: WebTestClient

    @TestConfiguration
    class MockA2UIServiceConfig {
        @Bean
        @Primary
        fun mockA2UIService(): A2UIService {
            val mock = mockk<A2UIService>()
            every {
                mock.generateSurface(any())
            } returns Mono.just(
                A2UIEnvelopeResponse(
                    surfaceId = "surf-test-001",
                    surface = SurfaceEnvelope(
                        type = "createSurface",
                        surfaceId = "surf-test-001",
                        components = listOf(
                            A2UIComponent(
                                type = "stat-card",
                                props = mapOf("label" to "Ventas", "value" to "100"),
                                nodeId = "node-1"
                            )
                        ),
                        layout = A2UILayout(layout = "grid", columns = 2)
                    ),
                    provenance = "gemini"
                )
            )
            return mock
        }
    }

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `POST a2ui with valid message returns 200 with createSurface`() {
        webTestClient.post()
            .uri("/api/agent/a2ui")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message": "show me sales", "mode": "analyst"}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.surfaceId").isNotEmpty
            .jsonPath("$.surface.type").isEqualTo("createSurface")
            .jsonPath("$.surface.components").isArray
            .jsonPath("$.surface.components[0].type").isEqualTo("stat-card")
            .jsonPath("$.provenance").isEqualTo("gemini")
    }

    @Test
    fun `POST a2ui with empty message returns 400`() {
        webTestClient.post()
            .uri("/api/agent/a2ui")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message": ""}""")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.code").isEqualTo("INVALID_MESSAGE")
    }

    @Test
    fun `POST a2ui with invalid JSON returns 400`() {
        webTestClient.post()
            .uri("/api/agent/a2ui")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("not valid json")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `POST a2ui with context returns 200`() {
        webTestClient.post()
            .uri("/api/agent/a2ui")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message": "show sales", "context": {"tenant": "acme"}}""")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.surfaceId").isNotEmpty
            .jsonPath("$.provenance").isEqualTo("gemini")
    }
}
