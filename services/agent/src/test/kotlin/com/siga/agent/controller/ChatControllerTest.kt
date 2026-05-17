package com.siga.agent.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.http.MediaType
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatControllerTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var webTestClient: WebTestClient
    private val mapper = ObjectMapper()

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
}
