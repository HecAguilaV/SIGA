package com.siga.agent.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class A2UIMessageTest {

    private val mapper = jacksonObjectMapper()

    @Test
    fun `A2UIv0Request serializes to expected JSON`() {
        val request = A2UIv0Request(
            prompt = "show me sales",
            context = mapOf("tenant" to "acme"),
            history = listOf(mapOf("role" to "user", "content" to "hi")),
            mode = "analyst"
        )

        val json = mapper.writeValueAsString(request)
        val parsed = mapper.readTree(json)

        assertEquals("show me sales", parsed["prompt"].asText())
        assertEquals("acme", parsed["context"]["tenant"].asText())
        assertEquals("user", parsed["history"][0]["role"].asText())
        assertEquals("analyst", parsed["mode"].asText())
    }

    @Test
    fun `A2UIv0Request with only prompt has null optionals`() {
        val request = A2UIv0Request(prompt = "hello")

        val json = mapper.writeValueAsString(request)
        val parsed = mapper.readTree(json)

        assertEquals("hello", parsed["prompt"].asText())
        assertNull(parsed["context"])
        assertNull(parsed["history"])
        assertNull(parsed["mode"])
    }

    @Test
    fun `CreateSurface serializes with all fields`() {
        val component = A2UIComponent(
            type = "stat-card",
            props = mapOf("label" to "Ventas", "value" to "100"),
            nodeId = "node-1"
        )
        val surface = CreateSurface(
            surfaceId = "surf-1",
            components = listOf(component),
            layout = A2UILayout(layout = "grid", columns = 2, gap = 16)
        )

        val json = mapper.writeValueAsString(surface)
        val parsed = mapper.readTree(json)

        assertEquals("surf-1", parsed["surfaceId"].asText())
        assertEquals("stat-card", parsed["components"][0]["type"].asText())
        assertEquals("Ventas", parsed["components"][0]["props"]["label"].asText())
        assertEquals("grid", parsed["layout"]["layout"].asText())
        assertEquals(2, parsed["layout"]["columns"].asInt())
    }

    @Test
    fun `CreateSurface without layout omits it`() {
        val component = A2UIComponent(type = "data-table")
        val surface = CreateSurface(surfaceId = "surf-2", components = listOf(component))

        val json = mapper.writeValueAsString(surface)
        val parsed = mapper.readTree(json)

        assertEquals("surf-2", parsed["surfaceId"].asText())
        assertNull(parsed["layout"])
    }

    @Test
    fun `UpdateComponents serializes with mode`() {
        val update = UpdateComponents(
            surfaceId = "surf-1",
            components = listOf(A2UIComponent(type = "trend-badge")),
            mode = UpdateMode.APPEND
        )

        val json = mapper.writeValueAsString(update)
        val parsed = mapper.readTree(json)

        assertEquals("surf-1", parsed["surfaceId"].asText())
        assertEquals("APPEND", parsed["mode"].asText())
    }

    @Test
    fun `UpdateComponents defaults to REPLACE mode`() {
        val update = UpdateComponents(
            surfaceId = "surf-1",
            components = listOf(A2UIComponent(type = "card"))
        )

        val json = mapper.writeValueAsString(update)
        val parsed = mapper.readTree(json)

        assertEquals("REPLACE", parsed["mode"].asText())
    }

    @Test
    fun `UpdateDataModel serializes data map`() {
        val update = UpdateDataModel(
            surfaceId = "surf-1",
            data = mapOf("sales" to 1000, "trend" to "up")
        )

        val json = mapper.writeValueAsString(update)
        val parsed = mapper.readTree(json)

        assertEquals(1000, parsed["data"]["sales"].asInt())
        assertEquals("up", parsed["data"]["trend"].asText())
    }

    @Test
    fun `A2UIComponent with children serializes nested`() {
        val child = A2UIComponent(type = "stat-card", props = mapOf("label" to "Child"))
        val parent = A2UIComponent(
            type = "card",
            props = mapOf("title" to "Parent"),
            children = listOf(child)
        )

        val json = mapper.writeValueAsString(parent)
        val parsed = mapper.readTree(json)

        assertEquals("card", parsed["type"].asText())
        assertEquals("Child", parsed["children"][0]["props"]["label"].asText())
    }

    @Test
    fun `A2UIComponent deserializes from JSON`() {
        val json = """{"type":"stat-card","props":{"label":"Ventas","value":"42"},"nodeId":"n1"}"""
        val component: A2UIComponent = mapper.readValue(json)

        assertEquals("stat-card", component.type)
        assertEquals("Ventas", component.props?.get("label"))
        assertEquals("42", component.props?.get("value"))
        assertEquals("n1", component.nodeId)
    }

    @Test
    fun `CreateSurface deserializes from JSON`() {
        val json = """
        {
            "surfaceId": "s1",
            "components": [
                {"type": "stat-card", "props": {"label": "Ventas", "value": "42"}}
            ]
        }
        """.trimIndent()

        val surface: CreateSurface = mapper.readValue(json)
        assertEquals("s1", surface.surfaceId)
        assertEquals(1, surface.components.size)
        assertEquals("stat-card", surface.components[0].type)
    }

    @Test
    fun `UpdateMode enum has all three values`() {
        assertEquals(3, UpdateMode.entries.size)
        assertEquals(UpdateMode.REPLACE, UpdateMode.valueOf("REPLACE"))
        assertEquals(UpdateMode.APPEND, UpdateMode.valueOf("APPEND"))
        assertEquals(UpdateMode.PATCH, UpdateMode.valueOf("PATCH"))
    }
}
