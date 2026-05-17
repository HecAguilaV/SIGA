package com.siga.agent.controller

import com.siga.agent.model.A2UIv0Request
import com.siga.agent.service.A2UIService
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
    private val a2uiService: A2UIService
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

        val a2uiV0Request = A2UIv0Request(
            prompt = request.message,
            context = request.context,
            history = request.history,
            mode = request.mode
        )

        return a2uiService.generateSurface(a2uiV0Request)
            .map<ResponseEntity<Any>> { response ->
                val resp = A2UIResponse(
                    surfaceId = response.surfaceId,
                    surface = SurfaceEnvelope(
                        type = "createSurface",
                        surfaceId = response.surfaceId,
                        components = response.surface.components,
                        layout = response.surface.layout
                    ),
                    provenance = response.provenance
                )
                ResponseEntity.ok(resp as Any)
            }
            .onErrorResume { error ->
                logger.error("A2UI service error: ${error.message}")
                Mono.just(
                    ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(mapOf("code" to "SERVICE_ERROR", "message" to error.message) as Any)
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
