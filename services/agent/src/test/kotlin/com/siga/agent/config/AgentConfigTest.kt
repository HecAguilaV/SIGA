package com.siga.agent.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
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
}
