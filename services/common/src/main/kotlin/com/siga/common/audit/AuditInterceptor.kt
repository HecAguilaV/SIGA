package com.siga.common.audit

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Instant

/**
 * Interceptor de Auditoría para cumplimiento de Ley 21.719 (Protección de Datos Personales - Chile).
 *
 * Captura automáticamente cada petición HTTP que pasa por un microservicio,
 * registrando: Quién, Cuándo, Dónde, Qué y el Resultado.
 *
 * Los logs se emiten en formato JSON estructurado, listos para ser consumidos
 * por un sistema de Big Data (Kafka, ELK, Loki) en el futuro.
 */
class AuditInterceptor(
    private val objectMapper: ObjectMapper,
    private val serviceName: String
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger("AUDIT_TRAIL")

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // Guardamos el timestamp de inicio para calcular latencia
        request.setAttribute("audit_start_time", System.currentTimeMillis())
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val startTime = request.getAttribute("audit_start_time") as? Long ?: 0L
        val duration = System.currentTimeMillis() - startTime

        val auditEntry = AuditEntry(
            timestamp = Instant.now().toString(),
            service = serviceName,
            tenantId = request.getHeader("X-Tenant-Id") ?: "ANONYMOUS",
            method = request.method,
            path = request.requestURI,
            queryParams = request.queryString,
            statusCode = response.status,
            durationMs = duration,
            clientIp = request.remoteAddr,
            userAgent = request.getHeader("User-Agent"),
            error = ex?.message
        )

        // Emitimos como JSON estructurado al logger
        val json = objectMapper.writeValueAsString(auditEntry)

        if (response.status >= 400) {
            logger.warn(json)
        } else {
            logger.info(json)
        }
    }
}
