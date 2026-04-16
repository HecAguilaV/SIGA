package com.siga.common.audit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class AuditInterceptorTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val interceptor = AuditInterceptor(objectMapper, "siga-auth")

    @Test
    fun `preHandle stores start time and returns true`() {
        val request = MockHttpServletRequest("GET", "/api/usuarios")
        val response = MockHttpServletResponse()

        val result = interceptor.preHandle(request, response, Any())

        assertTrue(result, "preHandle debe permitir el paso siempre")
        assertNotNull(request.getAttribute("audit_start_time"))
    }

    @Test
    fun `afterCompletion logs tenant id from header`() {
        val request = MockHttpServletRequest("POST", "/api/inventario/productos")
        request.addHeader("X-Tenant-Id", "tenant-42")
        request.setAttribute("audit_start_time", System.currentTimeMillis())

        val response = MockHttpServletResponse()
        response.status = 200

        // No debe lanzar excepción — el log se emite al logger
        assertDoesNotThrow {
            interceptor.afterCompletion(request, response, Any(), null)
        }
    }

    @Test
    fun `afterCompletion handles missing tenant id gracefully`() {
        val request = MockHttpServletRequest("GET", "/api/ventas")
        request.setAttribute("audit_start_time", System.currentTimeMillis())

        val response = MockHttpServletResponse()
        response.status = 401

        assertDoesNotThrow {
            interceptor.afterCompletion(request, response, Any(), null)
        }
    }

    @Test
    fun `AuditEntry serializes to JSON correctly`() {
        val entry = AuditEntry(
            timestamp = "2026-04-16T10:00:00Z",
            service = "siga-ventas",
            tenantId = "tenant-99",
            method = "POST",
            path = "/api/ventas/registrar",
            queryParams = null,
            statusCode = 201,
            durationMs = 45,
            clientIp = "192.168.1.10",
            userAgent = "SIGA-Frontend/1.0",
            error = null
        )

        val json = objectMapper.writeValueAsString(entry)

        assertTrue(json.contains("\"tenantId\":\"tenant-99\""))
        assertTrue(json.contains("\"service\":\"siga-ventas\""))
        assertTrue(json.contains("\"statusCode\":201"))
        assertFalse(json.contains("\"error\":\""))
    }
}
