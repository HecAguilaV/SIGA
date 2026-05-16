package com.siga.agent

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class SigaAgentApplicationTests {

    @Test
    fun contextLoads() {
        // Verify application context loads without errors
    }
}
