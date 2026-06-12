package com.siga.agent.engine

import com.siga.agent.config.GeminiProperties
import com.siga.agent.model.CreateSurface
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GeminiEngineTest {

    private val engine = GeminiEngine(GeminiProperties())

    @Test
    fun `extractTextFromGeminiResponse extracts text correctly`() {
        val json = """
        {
            "candidates": [
                {
                    "content": {
                        "parts": [
                            {"text": "Hello world"}
                        ]
                    }
                }
            ]
        }
        """.trimIndent()
        
        val text = engine.extractTextFromGeminiResponse(json)
        assertEquals("Hello world", text)
    }
    
    @Test
    fun `extractTextFromGeminiResponse throws on missing candidates`() {
        val json = """{"other": "data"}"""
        val ex = assertThrows<RuntimeException> {
            engine.extractTextFromGeminiResponse(json)
        }
        assertTrue(ex.message!!.contains("Unexpected Gemini response format"))
    }
    
    @Test
    fun `extractTextFromGeminiResponse throws on empty candidates`() {
        val json = """{"candidates": []}"""
        assertThrows<RuntimeException> {
            engine.extractTextFromGeminiResponse(json)
        }
    }
    
    @Test
    fun `extractTextFromGeminiResponse throws on missing content`() {
        val json = """{"candidates": [{"other": 1}]}"""
        assertThrows<RuntimeException> {
            engine.extractTextFromGeminiResponse(json)
        }
    }
    
    @Test
    fun `extractTextFromGeminiResponse throws on missing parts`() {
        val json = """{"candidates": [{"content": {"other": 1}}]}"""
        assertThrows<RuntimeException> {
            engine.extractTextFromGeminiResponse(json)
        }
    }
    
    @Test
    fun `extractTextFromGeminiResponse throws on empty parts`() {
        val json = """{"candidates": [{"content": {"parts": []}}]}"""
        assertThrows<RuntimeException> {
            engine.extractTextFromGeminiResponse(json)
        }
    }
    
    @Test
    fun `buildUserContent builds string without context`() {
        val content = engine.buildUserContent("hello", null)
        assertEquals("hello", content)
        
        val content2 = engine.buildUserContent("hello", emptyMap())
        assertEquals("hello", content2)
    }
    
    @Test
    fun `buildUserContent builds string with context`() {
        val content = engine.buildUserContent("hello", mapOf("k" to "v"))
        assertTrue(content.startsWith("hello"))
        assertTrue(content.contains("Contexto:"))
        assertTrue(content.contains("\"k\":\"v\""))
    }
    
    @Test
    fun `buildSystemPrompt returns non empty string`() {
        val prompt = engine.buildSystemPrompt()
        assertTrue(prompt.isNotBlank())
        assertTrue(prompt.contains("siga-agent"))
    }

    @Test
    fun `generateSurface throws error when api key is blank`() {
        val emptyConfigEngine = GeminiEngine(GeminiProperties(apiKey = ""))
        val result = emptyConfigEngine.generateSurface("test").onErrorResume { e ->
            reactor.core.publisher.Mono.just(CreateSurface("error", emptyList(), narrative = e.message))
        }.block()
        
        assertNotNull(result)
        assertEquals("GEMINI_API_KEY is not configured", result?.narrative)
    }

    @Test
    fun `parseResponse returns CreateSurface with components and layout`() {
        val json = """
        {
            "surfaceId": "surf-test",
            "components": [
                {
                    "type": "stat-card",
                    "props": {"label": "Ventas", "value": "100"},
                    "nodeId": "node-ventas"
                },
                {
                    "type": "data-table",
                    "props": {
                        "columns": [{"key": "prod", "label": "Producto"}],
                        "rows": [{"prod": "Laptop"}]
                    }
                }
            ],
            "layout": {"layout": "grid", "columns": 2, "gap": 16}
        }
        """.trimIndent()

        val surface = GeminiEngine.parseResponse(json, "surf-test")

        assertNotNull(surface)
        assertEquals("surf-test", surface.surfaceId)
        assertEquals(2, surface.components.size)
        assertEquals("stat-card", surface.components[0].type)
        assertEquals("data-table", surface.components[1].type)
        assertEquals("Ventas", surface.components[0].props?.get("label"))
        assertNotNull(surface.layout)
        assertEquals("grid", surface.layout?.layout)
        assertEquals(2, surface.layout?.columns)
        assertEquals(16, surface.layout?.gap)
    }

    @Test
    fun `parseResponse handles missing layout gracefully`() {
        val json = """
        {
            "surfaceId": "surf-no-layout",
            "components": [
                {"type": "stat-card", "props": {"label": "Test", "value": "1"}}
            ]
        }
        """.trimIndent()

        val surface = GeminiEngine.parseResponse(json, "surf-no-layout")

        assertEquals("surf-no-layout", surface.surfaceId)
        assertEquals(1, surface.components.size)
    }

    @Test
    fun `parseResponse handles empty components array`() {
        val json = """
        {
            "surfaceId": "surf-empty",
            "components": []
        }
        """.trimIndent()

        val surface = GeminiEngine.parseResponse(json, "surf-empty")

        assertTrue(surface.components.isEmpty())
    }

    @Test
    fun `parseResponse generates surfaceId when not in response`() {
        val json = """
        {
            "components": [
                {"type": "trend-badge", "props": {"label": "KPI", "value": "+5%"}}
            ]
        }
        """.trimIndent()

        val surface = GeminiEngine.parseResponse(json, "auto-gen-id")

        assertEquals("auto-gen-id", surface.surfaceId)
        assertEquals(1, surface.components.size)
    }

    @Test
    fun `parseResponse throws on malformed JSON`() {
        val json = "not valid json"

        try {
            GeminiEngine.parseResponse(json, "surf-err")
            assertTrue(false) // Should not reach here
        } catch (e: RuntimeException) {
            assertTrue(e.message?.contains("Failed to parse") == true)
        }
    }

    @Test
    fun `parseResponse handles A2UIComponent with children`() {
        val json = """
        {
            "surfaceId": "surf-nested",
            "components": [
                {
                    "type": "card",
                    "props": {"title": "Parent"},
                    "children": [
                        {"type": "stat-card", "props": {"label": "Child", "value": "42"}}
                    ]
                }
            ]
        }
        """.trimIndent()

        val surface = GeminiEngine.parseResponse(json, "surf-nested")

        assertEquals(1, surface.components.size)
        assertEquals("card", surface.components[0].type)
        assertEquals(1, surface.components[0].children?.size)
        assertEquals("stat-card", surface.components[0].children?.get(0)?.type)
    }
}
