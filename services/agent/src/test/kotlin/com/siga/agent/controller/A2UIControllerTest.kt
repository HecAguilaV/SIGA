package com.siga.agent.controller

import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.A2UILayout
import com.siga.agent.service.A2UIEnvelopeResponse
import com.siga.agent.service.A2UIService
import com.siga.agent.controller.SurfaceEnvelope
import com.siga.agent.controller.A2UIRequest
import com.siga.agent.controller.A2UIResponse
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
                mock.generateSurface(match { it.prompt == "error" })
            } returns Mono.error(RuntimeException("Mocked service error"))
            
            every {
                mock.generateSurface(match { it.prompt != "error" })
            } returns Mono.just(
                A2UIEnvelopeResponse(
                    surfaceId = "surf-test-001",
                    surface = com.siga.agent.service.SurfaceEnvelope(
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

    @Test
    fun `POST a2ui error from service returns 502 BAD GATEWAY`() {
        webTestClient.post()
            .uri("/api/agent/a2ui")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""{"message": "error"}""")
            .exchange()
            .expectStatus().isEqualTo(502)
            .expectBody()
            .jsonPath("$.code").isEqualTo("SERVICE_ERROR")
            .jsonPath("$.message").isEqualTo("Mocked service error")
    }

    @Test
    fun `A2UIController Data Classes cover equals hashCode toString and copy`() {
        val req1 = A2UIRequest("msg", mapOf("k" to "v"), emptyList(), "mode")
        val req2 = req1.copy()
        val req3 = req1.copy(message = "m2")
        org.junit.jupiter.api.Assertions.assertTrue(req1 == req2)
        org.junit.jupiter.api.Assertions.assertFalse(req1 == req3)
        org.junit.jupiter.api.Assertions.assertEquals(req1.hashCode(), req2.hashCode())
        org.junit.jupiter.api.Assertions.assertTrue(req1.toString().contains("A2UIRequest"))

        val env1 = SurfaceEnvelope("type", "sid", emptyList(), null)
        val env2 = env1.copy()
        org.junit.jupiter.api.Assertions.assertTrue(env1 == env2)
        org.junit.jupiter.api.Assertions.assertEquals(env1.hashCode(), env2.hashCode())
        org.junit.jupiter.api.Assertions.assertTrue(env1.toString().contains("SurfaceEnvelope"))

        val res1 = A2UIResponse("sid", env1, "prov")
        val res2 = res1.copy()
        org.junit.jupiter.api.Assertions.assertTrue(res1 == res2)
        org.junit.jupiter.api.Assertions.assertEquals(res1.hashCode(), res2.hashCode())
        org.junit.jupiter.api.Assertions.assertTrue(res1.toString().contains("A2UIResponse"))
    }
}
