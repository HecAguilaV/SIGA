package com.siga.agent.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.siga.agent.service.A2UIService
import com.siga.agent.model.A2UIv0Request
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger
import java.util.UUID

/**
 * SSE streaming controller for BFF legacy compatibility.
 * Exposes GET /api/agent/chat/stream with same path + event format as the Python endpoint.
 *
 * Features:
 * - SSE events: chunk, done, error, tool, a2ui
 * - Max 50 concurrent connections
 * - TTFB < 2s
 * - 60s stream idle timeout
 */
@RestController
class ChatController(
    private val a2uiService: A2UIService
) {
    private val logger = LoggerFactory.getLogger(ChatController::class.java)
    private val mapper: ObjectMapper = jacksonObjectMapper()

    /**
     * Tracks concurrent SSE connections. Hard cap at 50.
     */
    private val activeConnections = AtomicInteger(0)

    @GetMapping("/api/agent/chat/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(
        @RequestParam message: String?,
        @RequestParam context: String? = null,
        @RequestParam history: String? = null
    ): Flux<ServerSentEvent<String>> {
        // Validate required param — throw to get proper HTTP 400
        if (message.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "message parameter is required")
        }

        // Check connection limit
        val current = activeConnections.incrementAndGet()
        if (current > 50) {
            activeConnections.decrementAndGet()
            logger.warn("Connection limit reached: {} concurrent", current - 1)
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many concurrent connections")
        }

        logger.info("SSE connect | active={} | message=%.60s".format(activeConnections.get(), message))

        val requestId = UUID.randomUUID().toString().take(8)
        val a2uiRequest = A2UIv0Request(prompt = message, mode = context)

        // Build SSE event stream
        return buildEventStream(requestId, a2uiRequest)
            .doFinally { signalType ->
                val remaining = activeConnections.decrementAndGet()
                logger.info("SSE disconnect | requestId={} | signal={} | active={}", requestId, signalType, remaining)
            }
            .doOnError { error ->
                logger.error("SSE error | requestId={} | error={}", requestId, error.message)
            }
    }

    /**
     * Builds the SSE event flux: emits chunk, a2ui, then done events.
     * Uses a sink-based approach to produce events asynchronously.
     */
    private fun buildEventStream(requestId: String, request: A2UIv0Request): Flux<ServerSentEvent<String>> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<ServerSentEvent<String>>()

        // Emit initial chunk event (TTFB < 2s)
        val initialContent = if (request.prompt.contains("Acabo de activar el modo agéntico")) {
            "Estamos en modo agéntico para facilitar tu experiencia. Por el momento te entrego un resumen de tu inventario y ventas."
        } else {
            "Procesando tu consulta..."
        }

        sink.tryEmitNext(
            ServerSentEvent.builder("")
                .event("chunk")
                .data("""{"type":"chunk","content":"$initialContent","done":false}""")
                .build()
        )

        // Run A2UIService asynchronously
        val responseMono = a2uiService.generateSurface(request)
            .timeout(Duration.ofSeconds(60))

        responseMono.subscribe(
            { response ->
                // Emit narrative if present
                response.surface.narrative?.let { narrative ->
                    sink.tryEmitNext(
                        ServerSentEvent.builder("")
                            .event("chunk")
                            .data("""{"type":"chunk","content":"$narrative","done":false}""")
                            .build()
                    )
                }

                // Emit tool event
                sink.tryEmitNext(
                    ServerSentEvent.builder("")
                        .event("tool")
                        .data("""{"type":"tool","name":"${response.provenance}","status":"done"}""")
                        .build()
                )

                // Emit a2ui event with the surface
                try {
                    val a2uiPayload = mapper.createObjectNode()
                        .put("type", "a2ui")
                        .put("surfaceId", response.surfaceId)
                        .set<com.fasterxml.jackson.databind.JsonNode>("surface", mapper.valueToTree(response.surface))

                    sink.tryEmitNext(
                        ServerSentEvent.builder("")
                            .event("a2ui")
                            .data(mapper.writeValueAsString(a2uiPayload))
                            .build()
                    )
                } catch (e: Exception) {
                    logger.warn("Failed to serialize a2ui event: ${e.message}")
                }

                // Emit done event
                sink.tryEmitNext(
                    ServerSentEvent.builder("")
                        .event("done")
                        .data("""{"type":"done","content":"","done":true}""")
                        .build()
                )

                sink.tryEmitComplete()
            },
            { error ->
                // Emit error event
                sink.tryEmitNext(
                    ServerSentEvent.builder("")
                        .event("error")
                        .data("""{"type":"error","code":"GENERATION_ERROR","message":"${error.message}"}""")
                        .build()
                )
                sink.tryEmitNext(
                    ServerSentEvent.builder("")
                        .event("done")
                        .data("""{"type":"done","content":"","done":true}""")
                        .build()
                )
                sink.tryEmitComplete()
            }
        )

        return sink.asFlux()
            .timeout(Duration.ofSeconds(60))
            .onErrorResume { error ->
                if (error is CancellationException || error.message?.contains("timeout") == true) {
                    Flux.just(
                        ServerSentEvent.builder<String>("")
                            .event("error")
                            .data("""{"type":"error","code":"TIMEOUT","message":"Request timed out"}""")
                            .build(),
                        ServerSentEvent.builder<String>("")
                            .event("done")
                            .data("""{"type":"done","content":"","done":true}""")
                            .build()
                    )
                } else {
                    Flux.error(error)
                }
            }
    }
}
