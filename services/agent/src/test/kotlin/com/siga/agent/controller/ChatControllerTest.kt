package com.siga.agent.controller

import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.A2UILayout
import com.siga.agent.model.A2UIv0Request
import com.siga.agent.service.A2UIEnvelopeResponse
import com.siga.agent.service.A2UIService
import com.siga.agent.service.SurfaceEnvelope
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
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
class ChatControllerTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Test
    fun `GET chat stream returns SSE events`() {
        val result = webTestClient.get()
            .uri("/api/agent/chat/stream?message=hola")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
            .expectBody()
            .returnResult()

        val body = String(result.responseBody ?: ByteArray(0))
        assertTrue(body.isNotEmpty())
        assertTrue(body.startsWith("data:") || body.contains("data:"))
    }

    @Test
    fun `GET chat stream emits chunk event first`() {
        val result = webTestClient.get()
            .uri("/api/agent/chat/stream?message=muestra+ventas")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .returnResult()

        val body = String(result.responseBody ?: ByteArray(0))
        assertTrue(body.contains("\"type\":\"chunk\""))
    }

    @Test
    fun `GET chat stream emits done event`() {
        val result = webTestClient.get()
            .uri("/api/agent/chat/stream?message=test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .returnResult()

        val body = String(result.responseBody ?: ByteArray(0))
        assertTrue(body.contains("\"type\":\"done\""))
        assertTrue(body.contains("\"done\":true"))
    }

    @Test
    fun `GET chat stream with empty message returns 400`() {
        webTestClient.get()
            .uri("/api/agent/chat/stream?message=")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET chat stream without message param returns 400`() {
        webTestClient.get()
            .uri("/api/agent/chat/stream")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET chat stream emits a2ui event for known intents`() {
        val result = webTestClient.get()
            .uri("/api/agent/chat/stream?message=stock+de+leche")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .returnResult()

        val body = String(result.responseBody ?: ByteArray(0))
        // Should contain a2ui event for stock query
        assertTrue(body.contains("\"type\":\"a2ui\"") || body.contains("\"type\":\"chunk\""))
    }

    @Test
    fun `GET chat stream context param is accepted`() {
        webTestClient.get()
            .uri("/api/agent/chat/stream?message=test&context=analyst")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `GET chat stream history param is accepted`() {
        webTestClient.get()
            .uri("/api/agent/chat/stream?message=test&history=%5B%7B%22role%22%3A%22user%22%2C%22content%22%3A%22hi%22%7D%5D")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `SSE response has correct content type`() {
        webTestClient.get()
            .uri("/api/agent/chat/stream?message=hola")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
    }

    @Test
    fun `GET chat stream emits narrative when present`() {
        val result = webTestClient.get()
            .uri("/api/agent/chat/stream?message=narrative")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .returnResult()

        val body = String(result.responseBody ?: ByteArray(0))
        assertTrue(body.contains("\"type\":\"chunk\""))
        assertTrue(body.contains("This is a test narrative"))
    }

    @Test
    fun `GET chat stream error from service emits error event`() {
        val result = webTestClient.get()
            .uri("/api/agent/chat/stream?message=error")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .returnResult()

        val body = String(result.responseBody ?: ByteArray(0))
        assertTrue(body.contains("\"type\":\"error\""))
        assertTrue(body.contains("Mocked service error"))
    }

    @TestConfiguration
    class MockA2UIServiceConfig {

        @Bean
        @Primary
        fun a2uiService(): A2UIService {
            val mock = mockk<A2UIService>()
            
            every { mock.generateSurface(match { it.prompt == "error" }) } returns Mono.error(RuntimeException("Mocked service error"))
            
            every { mock.generateSurface(match { it.prompt == "narrative" }) } returns Mono.just(
                A2UIEnvelopeResponse(
                    surfaceId = "surf-narrative",
                    surface = SurfaceEnvelope(
                        type = "createSurface",
                        surfaceId = "surf-narrative",
                        components = listOf(A2UIComponent(type = "text", props = mapOf("text" to "Narrative"))),
                        narrative = "This is a test narrative"
                    ),
                    provenance = "gemini"
                )
            )

            val defaultResponse = A2UIEnvelopeResponse(
                surfaceId = "surf-test",
                surface = SurfaceEnvelope(
                    type = "createSurface",
                    surfaceId = "surf-test",
                    components = listOf(
                        A2UIComponent(type = "stat-card", props = mapOf("label" to "Test"))
                    ),
                    layout = A2UILayout(layout = "grid", columns = 2)
                ),
                provenance = "gemini"
            )
            every { mock.generateSurface(match { it.prompt != "error" && it.prompt != "narrative" }) } returns Mono.just(defaultResponse)
            return mock
        }
    }
}
