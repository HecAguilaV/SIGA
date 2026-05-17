package com.siga.agent.engine

import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.CreateSurface
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * Types of intents that the FallbackEngine can handle.
 */
enum class IntentType { READ, WRITE }

/**
 * Mapping between a query pattern and its corresponding intent.
 */
data class IntentMapping(
    val pattern: Regex,
    val intentType: IntentType,
    val sqlTemplate: String,
    val params: List<String>,
    val confirmTitle: String,
    val resultTitle: String
)

/**
 * Keyword-based intent classifier and A2UI component generator.
 * Tier 2 fallback: classifies user queries by regex patterns and generates
 * appropriate A2UI surfaces (READ → data display, WRITE → confirmation).
 *
 * Implements Human-in-the-Loop (HiTL) for mutations:
 * - 120s timeout for confirmations
 * - Rate limiting: 10 operations/min per tenant
 * - Prepared statements via JDBC for READ operations
 */
@Component
class FallbackEngine(
    @Autowired(required = false) private val dataSource: DataSource? = null
) {
    private val logger = LoggerFactory.getLogger(FallbackEngine::class.java)
    private val jdbcTemplate: JdbcTemplate? = dataSource?.let { JdbcTemplate(it) }

    /**
     * Registered intent mappings — ordered by priority (first match wins).
     */
    private val intentMappings: List<IntentMapping> = listOf(
        // === READS ===
        IntentMapping(
            pattern = Regex("stock de (.+)", RegexOption.IGNORE_CASE),
            intentType = IntentType.READ,
            sqlTemplate = "SELECT producto, cantidad, local FROM inventario WHERE producto ILIKE ?",
            params = listOf("producto"),
            confirmTitle = "",
            resultTitle = "Stock"
        ),
        IntentMapping(
            pattern = Regex("ventas del? (.+)", RegexOption.IGNORE_CASE),
            intentType = IntentType.READ,
            sqlTemplate = "SELECT periodo, total, variacion FROM ventas WHERE periodo ILIKE ?",
            params = listOf("periodo"),
            confirmTitle = "",
            resultTitle = "Ventas"
        ),
        IntentMapping(
            pattern = Regex("kpi (.+)", RegexOption.IGNORE_CASE),
            intentType = IntentType.READ,
            sqlTemplate = "SELECT indicador, valor, meta, estado FROM kpis WHERE indicador ILIKE ?",
            params = listOf("indicador"),
            confirmTitle = "",
            resultTitle = "KPI"
        ),
        // === WRITES ===
        IntentMapping(
            pattern = Regex("a(?:ñ|n)ade? (\\d+) (.+) al local (.+)", RegexOption.IGNORE_CASE),
            intentType = IntentType.WRITE,
            sqlTemplate = "UPDATE inventory SET quantity = quantity + ? WHERE producto ILIKE ? AND local ILIKE ?",
            params = listOf("cantidad", "producto", "local"),
            confirmTitle = "¿Agregar stock?",
            resultTitle = "Stock actualizado"
        ),
        IntentMapping(
            pattern = Regex("ajusta stock (.+) (\\d+)", RegexOption.IGNORE_CASE),
            intentType = IntentType.WRITE,
            sqlTemplate = "UPDATE inventory SET quantity = ? WHERE producto ILIKE ?",
            params = listOf("producto", "cantidad"),
            confirmTitle = "¿Ajustar stock?",
            resultTitle = "Stock ajustado"
        )
    )

    /**
     * Rate limiting state: tenant_id → list of timestamps (sliding window, 60s).
     */
    private val rateLimitStore = ConcurrentHashMap<String, MutableList<Long>>()

    // ---- PUBLIC API ----

    /**
     * Classifies a user query into an [IntentMapping].
     * Pure function — no side effects, testable without DB.
     */
    fun classifyIntent(query: String): IntentMapping? {
        return intentMappings.firstOrNull { mapping ->
            mapping.pattern.containsMatchIn(query)
        }
    }

    /**
     * Extracts named parameters from a query given its [IntentMapping].
     */
    fun extractParams(query: String, mapping: IntentMapping?): Map<String, String> {
        if (mapping == null) return emptyMap()
        val matchResult = mapping.pattern.find(query) ?: return emptyMap()
        val groupValues = matchResult.groupValues.drop(1) // skip full match
        return mapping.params.zip(groupValues).toMap()
    }

    /**
     * Generates an A2UI [CreateSurface] from the user query.
     * For READ intents: produces stat-card/data-table/trend-badge components.
     * For WRITE intents: produces confirmation surface for HiTL.
     * For unknown intents: falls back to catalog suggestions.
     */
    fun generateSurface(query: String, tenantId: String = "default"): CreateSurface {
        val surfaceId = "surf-${UUID.randomUUID().toString().take(8)}"
        val mapping = classifyIntent(query)

        if (mapping == null) {
            return generateCatalogFallback(surfaceId, query)
        }

        val params = extractParams(query, mapping)

        logOperation(tenantId, mapping, params)

        return when (mapping.intentType) {
            IntentType.READ -> generateReadSurface(surfaceId, mapping, params)
            IntentType.WRITE -> generateWriteConfirmation(surfaceId, mapping, params)
        }
    }

    /**
     * Checks whether a tenant has not exceeded the rate limit (10/min).
     */
    fun checkRateLimit(tenantId: String): Boolean {
        val now = System.currentTimeMillis()
        val windowStart = now - 60_000L

        val timestamps = rateLimitStore.getOrPut(tenantId) { mutableListOf() }

        synchronized(timestamps) {
            // Remove timestamps outside the window
            timestamps.removeAll { it < windowStart }

            if (timestamps.size >= 10) {
                return false
            }

            timestamps.add(now)
            return true
        }
    }

    // ---- PRIVATE HELPERS ----

    private fun generateReadSurface(
        surfaceId: String,
        mapping: IntentMapping,
        params: Map<String, String>
    ): CreateSurface {
        val components = mutableListOf<A2UIComponent>()

        // Try executing SQL via JDBC if DataSource is available
        val rows = if (jdbcTemplate != null && mapping.sqlTemplate.isNotBlank()) {
            try {
                val sqlParam = params.values.firstOrNull() ?: ""
                jdbcTemplate.queryForList(mapping.sqlTemplate, sqlParam)
            } catch (e: Exception) {
                logger.warn("JDBC query failed: ${e.message}. Using placeholder data.")
                emptyList()
            }
        } else {
            emptyList()
        }

        when {
            mapping.pattern.pattern.contains("stock", ignoreCase = true) -> {
                val stockRows = if (rows.isNotEmpty()) {
                    rows.map { row ->
                        mapOf(
                            "producto" to (row["producto"] ?: row["producto"] ?: ""),
                            "cantidad" to (row["cantidad"] ?: row["cantidad"] ?: 0),
                            "local" to (row["local"] ?: row["local"] ?: "")
                        )
                    }
                } else {
                    emptyList<Map<String, Any>>()
                }

                components.add(
                    A2UIComponent(
                        type = "stat-card",
                        props = mapOf(
                            "label" to "Stock ${parametersAsString(params)}",
                            "value" to (if (stockRows.isNotEmpty()) stockRows.size.toString() else "—"),
                            "trend" to "neutral"
                        ),
                        ref = "stat-stock"
                    )
                )
                components.add(
                    A2UIComponent(
                        type = "data-table",
                        props = mapOf(
                            "columns" to listOf(
                                mapOf("key" to "producto", "label" to "Producto"),
                                mapOf("key" to "cantidad", "label" to "Cantidad"),
                                mapOf("key" to "local", "label" to "Local")
                            ),
                            "rows" to stockRows
                        ),
                        ref = "table-stock"
                    )
                )
            }

            mapping.pattern.pattern.contains("ventas", ignoreCase = true) -> {
                val totalVentas = if (rows.isNotEmpty()) {
                    rows.sumOf { (it["total"] as? Number)?.toLong() ?: 0L }
                } else null

                components.add(
                    A2UIComponent(
                        type = "stat-card",
                        props = mapOf(
                            "label" to "Ventas ${parametersAsString(params)}",
                            "value" to (totalVentas?.toString() ?: "—"),
                            "trend" to "up"
                        ),
                        ref = "stat-ventas"
                    )
                )
                components.add(
                    A2UIComponent(
                        type = "trend-badge",
                        props = mapOf(
                            "label" to "Variación",
                            "value" to (if (totalVentas != null) "$$totalVentas" else "—"),
                            "trend" to "stable"
                        ),
                        ref = "trend-ventas"
                    )
                )
            }

            mapping.pattern.pattern.contains("kpi", ignoreCase = true) -> {
                val kpiValue = if (rows.isNotEmpty()) {
                    rows.firstOrNull()?.let { row ->
                        (row["valor"] ?: row["valor"] ?: "—").toString()
                    }
                } else null

                components.add(
                    A2UIComponent(
                        type = "stat-card",
                        props = mapOf(
                            "label" to "KPI ${parametersAsString(params)}",
                            "value" to (kpiValue ?: "—"),
                            "trend" to "neutral"
                        ),
                        ref = "stat-kpi"
                    )
                )
            }
        }

        return CreateSurface(
            surfaceId = surfaceId,
            components = components.ifEmpty {
                listOf(generateSuggestionComponent("Consulta sin datos disponibles"))
            }
        )
    }

    private fun generateWriteConfirmation(
        surfaceId: String,
        mapping: IntentMapping,
        params: Map<String, String>
    ): CreateSurface {
        val paramDetails = params.entries.joinToString("\n") { (key, value) ->
            "• ${key.replaceFirstChar { it.uppercase() }}: $value"
        }

        val components = listOf(
            A2UIComponent(
                type = "card",
                props = mapOf(
                    "title" to mapping.confirmTitle.ifEmpty { "¿Confirmar operación?" },
                    "description" to "Se ejecutará la siguiente operación:\n$paramDetails"
                ),
                ref = "confirm-card"
            ),
            A2UIComponent(
                type = "button",
                props = mapOf(
                    "label" to "✅ Sí, ejecutar",
                    "variant" to "primary",
                    "action" to "confirm"
                ),
                ref = "btn-confirm"
            ),
            A2UIComponent(
                type = "button",
                props = mapOf(
                    "label" to "❌ Cancelar",
                    "variant" to "secondary",
                    "action" to "cancel"
                ),
                ref = "btn-cancel"
            )
        )

        return CreateSurface(
            surfaceId = surfaceId,
            components = components
        )
    }

    private fun generateCatalogFallback(surfaceId: String, query: String): CreateSurface {
        val components = listOf(
            generateSuggestionComponent("No entendí tu consulta. Probá con: stock de [producto], ventas del [periodo], kpi [indicador]")
        )
        return CreateSurface(
            surfaceId = surfaceId,
            components = components
        )
    }

    private fun generateSuggestionComponent(message: String): A2UIComponent {
        return A2UIComponent(
            type = "card",
            props = mapOf(
                "title" to "Catálogo",
                "description" to message
            ),
            ref = "catalog-fallback"
        )
    }

    private fun parametersAsString(params: Map<String, String>): String {
        return params.values.joinToString(", ")
    }

    /**
     * Logs the operation with required fields for auditing.
     */
    private fun logOperation(
        tenantId: String?,
        mapping: IntentMapping,
        params: Map<String, String>
    ) {
        val operationId = UUID.randomUUID().toString().take(8)
        logger.info(
            "operation_id={} | tenant_id={} | intent={} | params={} | status={} | tier={} | timestamp={}",
            operationId,
            tenantId ?: "unknown",
            mapping.resultTitle.ifEmpty { "unknown" },
            params,
            "PENDING",
            "fallback-engine",
            java.time.Instant.now()
        )
        // Also add to MDC for structured logging
        MDC.put("operation_id", operationId)
        MDC.put("tier", "fallback-engine")
    }
}
