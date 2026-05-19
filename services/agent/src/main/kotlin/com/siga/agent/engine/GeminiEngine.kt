package com.siga.agent.engine

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.siga.agent.config.GeminiProperties
import com.siga.agent.model.A2UIComponent
import com.siga.agent.model.A2UILayout
import com.siga.agent.model.CreateSurface
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Component
class GeminiEngine(
    private val geminiProperties: GeminiProperties
) {
    private val logger = LoggerFactory.getLogger(GeminiEngine::class.java)
    private val mapper = jacksonObjectMapper()
    private val systemPrompt = buildSystemPrompt()

    /**
     * Generates an A2UI surface from a user prompt using Gemini.
     */
    fun generateSurface(prompt: String, context: Map<String, Any>? = null): Mono<CreateSurface> {
        val modelId = geminiProperties.modelId
        val apiKey = geminiProperties.apiKey

        if (apiKey.isBlank()) {
            return Mono.error(RuntimeException("GEMINI_API_KEY is not configured"))
        }

        val userContent = buildUserContent(prompt, context)
        val surfaceId = "surf-${java.util.UUID.randomUUID().toString().take(8)}"

        val requestBody = mapOf(
            "system_instruction" to mapOf("parts" to listOf(mapOf("text" to systemPrompt))),
            "contents" to listOf(mapOf("parts" to listOf(mapOf("text" to userContent)))),
            "generationConfig" to mapOf(
                "responseMimeType" to "application/json",
                "temperature" to 0.3
            )
        )

        val webClient = WebClient.create(
            "https://generativelanguage.googleapis.com/v1beta"
        )

        return webClient.post()
            .uri("/models/$modelId:generateContent?key=$apiKey")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String::class.java)
            .timeout(Duration.ofSeconds(15))
            .retryWhen(
                Retry.fixedDelay(1, Duration.ofSeconds(2))
                    .filter { throwable ->
                        logger.warn("Gemini call failed, retrying: ${throwable.message}")
                        true
                    }
            )
            .map { responseBody ->
                val geminiText = extractTextFromGeminiResponse(responseBody)
                parseResponse(geminiText, surfaceId)
            }
            .onErrorResume { error ->
                logger.error("Gemini error after retries: ${error.message}")
                Mono.error(RuntimeException("Gemini generation failed: ${error.message}"))
            }
    }

    /**
     * Parses the Gemini JSON response into a [CreateSurface].
     * This is a pure function — testable without HTTP.
     */
    companion object {
        private val staticMapper = jacksonObjectMapper()

        fun parseResponse(geminiJson: String, surfaceId: String): CreateSurface {
            try {
                val root = staticMapper.readTree(geminiJson)

                val actualSurfaceId = root.get("surfaceId")?.asText() ?: surfaceId

                val components = mutableListOf<A2UIComponent>()
                val componentsNode = root.get("components")
                if (componentsNode != null && componentsNode.isArray) {
                    for (node in componentsNode) {
                        components.add(parseComponent(node))
                    }
                }

                val layoutNode = root.get("layout")
                val layout = if (layoutNode != null && layoutNode.isObject) {
                    A2UILayout(
                        layout = layoutNode.get("layout")?.asText() ?: "default",
                        columns = layoutNode.get("columns")?.asInt(),
                        gap = layoutNode.get("gap")?.asInt()
                    )
                } else null

                return CreateSurface(
                    surfaceId = actualSurfaceId,
                    components = components,
                    layout = layout,
                    narrative = root.get("narrative")?.asText()
                )
            } catch (e: Exception) {
                throw RuntimeException("Failed to parse Gemini response: ${e.message}", e)
            }
        }

        private fun parseComponent(node: JsonNode): A2UIComponent {
            val type = node.get("type")?.asText() ?: "unknown"
            val props = mutableMapOf<String, Any>()
            val propsNode = node.get("props")
            if (propsNode != null && propsNode.isObject) {
                propsNode.fieldNames().forEachRemaining { key ->
                    val value = propsNode.get(key)
                    props[key] = when {
                        value.isTextual -> value.asText()
                        value.isInt -> value.asInt()
                        value.isLong -> value.asLong()
                        value.isDouble -> value.asDouble()
                        value.isBoolean -> value.asBoolean()
                        value.isObject -> staticMapper.convertValue(value, Map::class.java)
                        value.isArray -> staticMapper.convertValue(value, List::class.java)
                        else -> value.asText()
                    }
                }
            }

            val children = mutableListOf<A2UIComponent>()
            val childrenNode = node.get("children")
            if (childrenNode != null && childrenNode.isArray) {
                for (childNode in childrenNode) {
                    children.add(parseComponent(childNode))
                }
            }

            return A2UIComponent(
                type = type,
                props = if (props.isEmpty()) null else props,
                children = if (children.isEmpty()) null else children,
                ref = node.get("ref")?.asText(),
                nodeId = node.get("nodeId")?.asText()
            )
        }
    }

    private fun extractTextFromGeminiResponse(responseBody: String): String {
        val root = mapper.readTree(responseBody)
        val candidates = root.get("candidates")
        if (candidates != null && candidates.isArray && candidates.size() > 0) {
            val content = candidates[0].get("content")
            if (content != null) {
                val parts = content.get("parts")
                if (parts != null && parts.isArray && parts.size() > 0) {
                    return parts[0].get("text")?.asText() ?: ""
                }
            }
        }
        throw RuntimeException("Unexpected Gemini response format: no candidates found")
    }

    private fun buildUserContent(prompt: String, context: Map<String, Any>?): String {
        val sb = StringBuilder(prompt)
        if (context != null && context.isNotEmpty()) {
            sb.append("\n\nContexto: ")
            sb.append(mapper.writeValueAsString(context))
        }
        return sb.toString()
    }

    private fun buildSystemPrompt(): String {
        return """
Eres siga-agent, el agente de UI generativa de SIGA. Tu trabajo es ÚNICAMENTE generar interfaces de usuario declarativas siguiendo el protocolo A2UI v0.9.

## REGLAS ESTRICTAS
1. Respondés SIEMPRE con un JSON que sigue el protocolo A2UI v0.9.
2. Usá el campo "narrative" para dar una respuesta textual breve, empática y profesional que acompañe a la UI.
3. NO generes múltiples alternativas. Una sola propuesta de UI.

## FORMATO DE RESPUESTA
Debes responder con un JSON con esta estructura:
{
  "surfaceId": "identificador-único",
  "narrative": "Aquí tenés un resumen de los puntos clave...",
  "components": [
    {
      "type": "stat-card",
      "props": { "label": "Ventas", "value": "100" },
      "nodeId": "node-1"
    }
  ],
  "layout": { "layout": "grid", "columns": 2, "gap": 16 }
}

## CATÁLOGO DE COMPONENTES
- stat-card → Indicador numérico. Props: label, value, trend (up|down|neutral), change
- data-table → Tabla de datos. Props: columns (array de {key, label}), rows (array de objetos)
- trend-badge → Badge de tendencia. Props: label, value, trend (up|down|stable)
- card → Contenedor con título, descripción, hijos. Props: title, description
- chart → Gráfico. Props: type (bar|line|pie), labels, datasets, title
- insight-panel → Panel de análisis. Props: title, insights (array de {icon, text, severity})
- button → Botón de acción. Props: label, variant (primary|secondary|danger), action
- badge → Badge de estado. Props: text, variant (success|warning|danger|info)
- spinner → Indicador de carga
- empty-state → Estado vacío. Props: title, description
- error-state → Estado de error. Props: title, description

## ESTRUCTURA
El nodo raíz contiene un array de componentes. Usá children para anidar componentes dentro de containers como card. Máximo 2 niveles de profundidad.
        """.trimIndent()
    }
}
