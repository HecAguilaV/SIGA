package com.siga.agent.service

import com.siga.agent.engine.FallbackEngine
import com.siga.agent.engine.GeminiEngine
import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.A2UILayout
import com.siga.agent.model.A2UIv0Request
import com.siga.agent.model.CreateSurface
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.Duration

class A2UIServiceTest {

    private lateinit var geminiEngine: GeminiEngine
    private lateinit var fallbackEngine: FallbackEngine
    private lateinit var service: A2UIService

    private val testSurface = CreateSurface(
        surfaceId = "surf-test",
        components = listOf(
            A2UIComponent(type = "stat-card", props = mapOf("label" to "Test"))
        ),
        layout = A2UILayout(layout = "grid", columns = 2)
    )

    @BeforeEach
    fun setUp() {
        geminiEngine = mockk()
        fallbackEngine = mockk()
        service = A2UIService(geminiEngine, fallbackEngine)
    }

    @Test
    fun `Tier 1 Gemini succeeds returns gemini provenance`() {
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.just(testSurface)

        val request = A2UIv0Request(prompt = "show sales", mode = "analyst")
        val result = service.generateSurface(request).block(Duration.ofSeconds(5))

        assertNotNull(result)
        assertEquals("surf-test", result!!.surfaceId)
        assertEquals("gemini", result!!.provenance)
        verify(exactly = 1) { geminiEngine.generateSurface(any(), any()) }
    }

    @Test
    fun `Tier 1 fails Tier 2 fallback succeeds returns fallback-engine provenance`() {
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.error(RuntimeException("Gemini down"))
        every { fallbackEngine.generateSurface(any(), any()) } returns testSurface

        val request = A2UIv0Request(prompt = "stock de leche")
        val result = service.generateSurface(request).block(Duration.ofSeconds(5))

        assertNotNull(result)
        assertEquals("fallback-engine", result!!.provenance)
        verify(exactly = 1) { geminiEngine.generateSurface(any(), any()) }
        verify(exactly = 1) { fallbackEngine.generateSurface(any(), any()) }
    }

    @Test
    fun `Tier 1 and 2 fail returns catalog provenance`() {
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.error(RuntimeException("Gemini down"))
        every { fallbackEngine.generateSurface(any(), any()) } throws RuntimeException("Fallback error")

        val request = A2UIv0Request(prompt = "unknown query")
        val result = service.generateSurface(request).block(Duration.ofSeconds(5))

        assertNotNull(result)
        assertEquals("catalog", result!!.provenance)
        assertTrue(result!!.surfaceId.startsWith("surf-"))
    }

    @Test
    fun `dedup returns same result for identical message within 2s`() {
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.just(testSurface)

        val request = A2UIv0Request(prompt = "show sales")
        val first = service.generateSurface(request).block(Duration.ofSeconds(5))
        val second = service.generateSurface(request).block(Duration.ofSeconds(5))

        assertNotNull(first)
        assertNotNull(second)
        assertEquals(first!!.surfaceId, second!!.surfaceId)
        assertEquals(first!!.provenance, second!!.provenance)
        // Gemini should only be called once (dedup serves the second from cache)
        verify(exactly = 1) { geminiEngine.generateSurface(any(), any()) }
    }

    @Test
    fun `dedup does not apply to different messages`() {
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.just(testSurface)

        val request1 = A2UIv0Request(prompt = "show sales")
        val request2 = A2UIv0Request(prompt = "stock de leche")

        service.generateSurface(request1).block(Duration.ofSeconds(5))
        service.generateSurface(request2).block(Duration.ofSeconds(5))

        verify(exactly = 2) { geminiEngine.generateSurface(any(), any()) }
    }

    @Test
    fun `Tier 1 timeout triggers fallback to Tier 2`() {
        // Simulate Gemini timeout by returning a slow Mono
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.just(testSurface)
            .delayElement(Duration.ofSeconds(35)) // exceeds 30s tier timeout
        every { fallbackEngine.generateSurface(any(), any()) } returns testSurface

        val request = A2UIv0Request(prompt = "stock de leche")
        val result = service.generateSurface(request).block(Duration.ofSeconds(60))

        assertNotNull(result)
        assertEquals("fallback-engine", result!!.provenance)
    }

    @Test
    fun `catalog fallback generates suggestion components`() {
        every { geminiEngine.generateSurface(any(), any()) } returns Mono.error(RuntimeException("error"))
        every { fallbackEngine.generateSurface(any(), any()) } throws RuntimeException("Fallback error")

        val request = A2UIv0Request(prompt = "xyz")
        val result = service.generateSurface(request).block(Duration.ofSeconds(5))

        assertNotNull(result)
        assertEquals("catalog", result!!.provenance)
        assertTrue(result!!.surface.components.isNotEmpty())
    }
}
