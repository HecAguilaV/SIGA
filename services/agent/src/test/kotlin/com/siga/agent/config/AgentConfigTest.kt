package com.siga.agent.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(properties = ["gemini.api-key=test-key", "gemini.model-id=gemini-2.0-flash-001"])
@ActiveProfiles("test")
class AgentConfigTest @Autowired constructor(
    private val geminiProperties: GeminiProperties
) {

    @Test
    fun `gemini api key property is bound from config`() {
        assertNotNull(geminiProperties.apiKey)
    }

    @Test
    fun `gemini model id property is bound from config`() {
        assertEquals("gemini-2.0-flash-001", geminiProperties.modelId)
    }

    @Test
    fun `GeminiProperties data class methods coverage`() {
        val props1 = GeminiProperties("key1", "model1")
        val props2 = GeminiProperties("key1", "model1")
        val props3 = GeminiProperties("key2", "model2")

        // equals
        assertTrue(props1 == props1)
        assertTrue(props1 == props2)
        assertFalse(props1 == props3)
        assertFalse(props1.equals(null))
        assertFalse(props1.equals("Not a property"))

        // hashCode
        assertEquals(props1.hashCode(), props2.hashCode())
        assertNotEquals(props1.hashCode(), props3.hashCode())

        // toString
        assertTrue(props1.toString().contains("GeminiProperties"))

        // copy
        val propsCopied = props1.copy(apiKey = "keyCopied")
        assertEquals("keyCopied", propsCopied.apiKey)

        // getters and setters (implicit)
        val props4 = GeminiProperties()
        props4.apiKey = "key4"
        props4.modelId = "model4"
        assertEquals("key4", props4.apiKey)
        assertEquals("model4", props4.modelId)
    }
}
