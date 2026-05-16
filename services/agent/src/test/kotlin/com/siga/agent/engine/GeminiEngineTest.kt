package com.siga.agent.engine

import com.siga.agent.model.CreateSurface
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GeminiEngineTest {

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
