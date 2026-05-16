package com.siga.agent.controller

import com.siga.agent.config.GeminiProperties
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HealthController(
    private val geminiProperties: GeminiProperties
) {

    @GetMapping("/health", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun health(): Mono<Map<String, Any>> {
        return Mono.just(
            mapOf(
                "status" to "UP",
                "model" to geminiProperties.modelId,
                "version" to "1.0.0"
            )
        )
    }
}
