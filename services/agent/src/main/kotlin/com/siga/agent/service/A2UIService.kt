package com.siga.agent.service

import com.siga.agent.engine.FallbackEngine
import com.siga.agent.engine.GeminiEngine
import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.A2UILayout
import com.siga.agent.model.A2UIv0Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Response envelope from the 3-tier A2UI service.
 */
data class A2UIEnvelopeResponse(
    val surfaceId: String,
    val surface: SurfaceEnvelope,
    val provenance: String
)

/**
 * Surface envelope containing the components and layout.
 */
data class SurfaceEnvelope(
    val type: String = "createSurface",
    val surfaceId: String,
    val components: List<A2UIComponent>,
    val layout: A2UILayout? = null,
    val narrative: String? = null
)

/**
 * 3-tier orchestrator for A2UI surface generation.
 *
 * Tier 1: GeminiEngine (LLM-powered, 30s timeout)
 * Tier 2: FallbackEngine (keyword/regex matching, <2s)
 * Tier 3: Catalog (static defaults, <500ms)
 *
 * Features:
 * - Deduplication: identical messages within 2s window return cached result
 * - Provenance tracking: "gemini" | "fallback-engine" | "catalog"
 * - Total request timeout: 60s
 */
@Service
class A2UIService(
    private val geminiEngine: GeminiEngine,
    private val fallbackEngine: FallbackEngine
) {
    private val logger = LoggerFactory.getLogger(A2UIService::class.java)

    /**
     * Dedup cache: prompt → {response, timestamp}
     * Evicts entries older than 2 seconds.
     */
    private val dedupCache = ConcurrentHashMap<String, DedupEntry>()

    private data class DedupEntry(
        val response: A2UIEnvelopeResponse,
        val timestamp: Long
    )

    /**
     * Generates an A2UI surface through the 3-tier pipeline.
     * Returns deduped result for repeated messages within 2s.
     */
    fun generateSurface(request: A2UIv0Request): Mono<A2UIEnvelopeResponse> {
        val cacheKey = request.prompt.trim().lowercase()

        // 1. Check dedup cache
        val cached = checkDedupCache(cacheKey)
        if (cached != null) {
            logger.info("A2UI dedup hit | prompt=%.60s | provenance=%s".format(request.prompt, cached.provenance))
            return Mono.just(cached)
        }

        // 2. Run 3-tier pipeline with total 60s timeout
        return runTierPipeline(request)
            .timeout(Duration.ofSeconds(60))
            .doOnNext { response ->
                // Cache the result for dedup
                dedupCache[cacheKey] = DedupEntry(response, System.currentTimeMillis())
                logger.info("A2UI generated | provenance={} | surfaceId={}", response.provenance, response.surfaceId)
            }
            .onErrorResume { error ->
                logger.error("A2UI all tiers failed: ${error.message}")
                val fallback = generateCatalogFallback()
                Mono.just(fallback)
            }
    }

    /**
     * Runs the 3-tier pipeline: Gemini → FallbackEngine → Catalog.
     */
    private fun runTierPipeline(request: A2UIv0Request): Mono<A2UIEnvelopeResponse> {
        return tryGemini(request)
            .onErrorResume { error1 ->
                logger.warn("Tier 1 (Gemini) failed: ${error1.message}. Trying Tier 2 (FallbackEngine)...")
                tryFallback(request)
                    .onErrorResume { error2 ->
                        logger.warn("Tier 2 (FallbackEngine) failed: ${error2.message}. Falling back to Tier 3 (Catalog)...")
                        Mono.just(generateCatalogFallback())
                    }
            }
    }

    /**
     * Tier 1: GeminiEngine (LLM-powered, 30s timeout)
     */
    private fun tryGemini(request: A2UIv0Request): Mono<A2UIEnvelopeResponse> {
        return geminiEngine.generateSurface(request.prompt, request.context)
            .timeout(Duration.ofSeconds(30))
            .map { surface ->
                A2UIEnvelopeResponse(
                    surfaceId = surface.surfaceId,
                    surface = SurfaceEnvelope(
                        type = "createSurface",
                        surfaceId = surface.surfaceId,
                        components = surface.components,
                        layout = surface.layout,
                        narrative = surface.narrative
                    ),
                    provenance = "gemini"
                )
            }
    }

    /**
     * Tier 2: Try FallbackEngine.
     * FallbackEngine runs synchronously, wrap in Mono.
     */
    private fun tryFallback(request: A2UIv0Request): Mono<A2UIEnvelopeResponse> {
        return Mono.fromCallable {
            fallbackEngine.generateSurface(request.prompt)
        }.timeout(Duration.ofSeconds(30))
            .map { surface ->
                A2UIEnvelopeResponse(
                    surfaceId = surface.surfaceId,
                    surface = SurfaceEnvelope(
                        type = "createSurface",
                        surfaceId = surface.surfaceId,
                        components = surface.components,
                        layout = surface.layout
                    ),
                    provenance = "fallback-engine"
                )
            }
            .onErrorResume { error ->
                // Ensure any error from FallbackEngine is propagated
                Mono.error(RuntimeException("FallbackEngine failed: ${error.message}"))
            }
    }

    /**
     * Tier 3: Catalog fallback — generates suggestion components.
     */
    private fun generateCatalogFallback(): A2UIEnvelopeResponse {
        val surfaceId = "surf-${UUID.randomUUID().toString().take(8)}"
        val components = listOf(
            A2UIComponent(
                type = "card",
                props = mapOf(
                    "title" to "Catálogo",
                    "description" to "No pude procesar tu solicitud. Probá con preguntar sobre stock, ventas o KPIs."
                ),
                ref = "catalog-fallback"
            ),
            A2UIComponent(
                type = "stat-card",
                props = mapOf(
                    "label" to "Stock",
                    "value" to "Consultar",
                    "trend" to "neutral"
                ),
                ref = "suggestion-stock"
            ),
            A2UIComponent(
                type = "stat-card",
                props = mapOf(
                    "label" to "Ventas",
                    "value" to "Consultar",
                    "trend" to "neutral"
                ),
                ref = "suggestion-ventas"
            ),
            A2UIComponent(
                type = "stat-card",
                props = mapOf(
                    "label" to "KPIs",
                    "value" to "Consultar",
                    "trend" to "neutral"
                ),
                ref = "suggestion-kpi"
            )
        )

        return A2UIEnvelopeResponse(
            surfaceId = surfaceId,
            surface = SurfaceEnvelope(
                type = "createSurface",
                surfaceId = surfaceId,
                components = components
            ),
            provenance = "catalog"
        )
    }

    /**
     * Checks the dedup cache. Returns cached response if found within 2s window.
     */
    private fun checkDedupCache(key: String): A2UIEnvelopeResponse? {
        val entry = dedupCache[key] ?: return null
        val age = System.currentTimeMillis() - entry.timestamp

        if (age < 2000) {
            return entry.response
        }

        // Stale entry — remove it
        dedupCache.remove(key)
        return null
    }
}
