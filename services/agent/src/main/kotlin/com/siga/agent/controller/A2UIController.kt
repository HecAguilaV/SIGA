package com.siga.agent.controller

import com.siga.agent.engine.GeminiEngine
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class A2UIController(
    private val geminiEngine: GeminiEngine
) {
    private val logger = LoggerFactory.getLogger(A2UIController::class.java)

    @PostMapping("/api/agent/a2ui", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun generateA2ui(@RequestBody request: A2UIRequest): Mono<ResponseEntity<Any>> {
        if (request.message.isBlank()) {
            return Mono.just(
                ResponseEntity
                    .badRequest()
                    .body(mapOf("code" to "INVALID_MESSAGE", "message" to "message is required") as Any)
            )
        }

        logger.info("A2UI generate | message=%.80s".format(request.message))

        return geminiEngine.generateSurface(request.message, request.context)
            .map<ResponseEntity<Any>> { surface ->
                val resp = A2UIResponse(
                    surfaceId = surface.surfaceId,
                    surface = SurfaceEnvelope(
                        type = "createSurface",
                        surfaceId = surface.surfaceId,
                        components = surface.components,
                        layout = surface.layout
                    ),
                    provenance = "gemini"
                )
                ResponseEntity.ok(resp as Any)
            }
            .onErrorResume { error ->
                logger.error("Gemini error: ${error.message}")
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(mapOf("code" to "GEMINI_ERROR", "message" to error.message) as Any)
                )
            }
    }
}

data class A2UIRequest(
    val message: String,
    val context: Map<String, Any>? = null,
    val history: List<Map<String, String>>? = null,
    val mode: String? = null
)

data class A2UIResponse(
    val surfaceId: String,
    val surface: SurfaceEnvelope,
    val provenance: String
)

data class SurfaceEnvelope(
    val type: String,
    val surfaceId: String,
    val components: List<com.siga.agent.model.A2UIComponent>,
    val layout: com.siga.agent.model.A2UILayout? = null
)
