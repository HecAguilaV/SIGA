package com.siga.agent

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(properties = ["GEMINI_API_KEY=test-key"])
@ActiveProfiles("test")
class SigaAgentApplicationTests {

    @Test
    fun contextLoads() {
        // Verify application context loads without errors
    }

    @Test
    fun `startup check rejects missing GEMINI_API_KEY`() {
        val env = mockk<Environment>()
        every { env.getProperty("gemini.api-key") } returns ""

        val exception = assertThrows(IllegalStateException::class.java) {
            SigaAgentApplication.validateApiKey(env)
        }
        assertEquals("GEMINI_API_KEY is required. Set it in environment or application.yml", exception.message)
    }

    @Test
    fun `startup check rejects blank GEMINI_API_KEY`() {
        val env = mockk<Environment>()
        every { env.getProperty("gemini.api-key") } returns "   "

        val exception = assertThrows(IllegalStateException::class.java) {
            SigaAgentApplication.validateApiKey(env)
        }
        assertEquals("GEMINI_API_KEY is required. Set it in environment or application.yml", exception.message)
    }

    @Test
    fun `startup check accepts present GEMINI_API_KEY`() {
        val env = mockk<Environment>()
        every { env.getProperty("gemini.api-key") } returns "test-key-123"

        assertDoesNotThrow {
            SigaAgentApplication.validateApiKey(env)
        }
    }
}
