package com.siga.agent.model

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * A2UI v0.9 Protocol message types.
 * See: https://a2ui.org/spec/v0.9/
 */

/**
 * Incoming request from the frontend to generate an A2UI surface.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class A2UIv0Request(
    val prompt: String,
    val context: Map<String, Any>? = null,
    val history: List<Map<String, String>>? = null,
    val mode: String? = null
)

/**
 * Server-to-client: create a new surface with components.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateSurface(
    val surfaceId: String,
    val components: List<A2UIComponent>,
    val layout: A2UILayout? = null
)

/**
 * Server-to-client: update existing surface components.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateComponents(
    val surfaceId: String,
    val components: List<A2UIComponent>,
    val mode: UpdateMode = UpdateMode.REPLACE
)

/**
 * Server-to-client: update data bindings on a surface.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateDataModel(
    val surfaceId: String,
    val data: Map<String, Any>
)

/**
 * A single UI component in the A2UI catalog.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class A2UIComponent(
    val type: String,
    val props: Map<String, Any>? = null,
    val children: List<A2UIComponent>? = null,
    val ref: String? = null,
    val nodeId: String? = null
)

/**
 * Layout configuration for a surface.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class A2UILayout(
    val layout: String,
    val columns: Int? = null,
    val gap: Int? = null
)

/**
 * How to apply component updates to an existing surface.
 */
enum class UpdateMode {
    REPLACE,
    APPEND,
    PATCH
}
