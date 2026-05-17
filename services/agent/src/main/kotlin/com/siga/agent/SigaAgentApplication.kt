package com.siga.agent

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment

@SpringBootApplication
@EnableDiscoveryClient
class SigaAgentApplication(
    private val environment: Environment
) {
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        SigaAgentApplication.validateApiKey(environment)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SigaAgentApplication::class.java)

        /**
         * Validates that GEMINI_API_KEY is configured.
         * Throws [IllegalStateException] if missing or blank — fails startup.
         */
        fun validateApiKey(environment: Environment) {
            val apiKey = environment.getProperty("gemini.api-key")
            if (apiKey.isNullOrBlank()) {
                logger.error("GEMINI_API_KEY is not configured. Set it in environment or application.yml")
                throw IllegalStateException("GEMINI_API_KEY is required. Set it in environment or application.yml")
            }
            logger.info("GEMINI_API_KEY is configured")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<SigaAgentApplication>(*args)
}
