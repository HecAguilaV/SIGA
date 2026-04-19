package com.siga.auth.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class JwtServiceTest {

    private val secret = "test-secret-key-for-unit-testing-purposes-only-12345"
    private val jwtService = JwtService(secret)

    @Test
    fun `token generado contiene el email como subject`() {
        val token = jwtService.generateToken("admin@siga.cl", "ADMIN", 1)
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals("admin@siga.cl", decoded.subject)
    }

    @Test
    fun `token generado contiene el rol como claim`() {
        val token = jwtService.generateToken("vendedor@tienda.cl", "VENDEDOR", 5)
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals("VENDEDOR", decoded.getClaim("rol").asString())
    }

    @Test
    fun `token generado contiene el tenant_id cuando se provee`() {
        val token = jwtService.generateToken("admin@siga.cl", "ADMIN", 42)
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals(42, decoded.getClaim("tenant_id").asInt())
    }

    @Test
    fun `token generado NO contiene tenant_id cuando es null`() {
        val token = jwtService.generateToken("superadmin@siga.cl", "SUPERADMIN", null)
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertTrue(decoded.getClaim("tenant_id").isMissing)
    }

    @Test
    fun `token generado tiene fecha de expiracion a 24 horas`() {
        val token = jwtService.generateToken("test@siga.cl", "USER", 1)
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        val issuedAt = decoded.issuedAt.toInstant()
        val expiresAt = decoded.expiresAt.toInstant()
        val hours = java.time.Duration.between(issuedAt, expiresAt).toHours()

        assertEquals(24, hours)
    }
}
