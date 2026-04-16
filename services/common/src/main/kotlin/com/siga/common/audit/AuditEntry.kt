package com.siga.common.audit

/**
 * Estructura de datos inmutable que representa una entrada de auditoría.
 * Diseñada para serialización JSON directa hacia sistemas de logging/Big Data.
 *
 * Cumplimiento Ley 21.719: Registra quién (tenantId), cuándo (timestamp),
 * dónde (service + path) y qué (method + statusCode) se accedió.
 */
data class AuditEntry(
    val timestamp: String,
    val service: String,
    val tenantId: String,
    val method: String,
    val path: String,
    val queryParams: String?,
    val statusCode: Int,
    val durationMs: Long,
    val clientIp: String?,
    val userAgent: String?,
    val error: String? = null
)
