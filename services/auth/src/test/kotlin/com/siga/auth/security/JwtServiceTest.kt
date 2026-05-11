package com.siga.auth.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant
import java.time.temporal.ChronoUnit

class JwtServiceTest {

    private val secret = "test-secret-key-for-unit-testing-purposes-only-12345"
    private val jwtService = JwtService(secret)

    @Test
    fun `token generado contiene el email como subject`() {
        val token = jwtService.generateToken("admin@siga.cl", "ADMIN", 1, "customer")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals("admin@siga.cl", decoded.subject)
    }

    @Test
    fun `token generado contiene el rol como claim`() {
        val token = jwtService.generateToken("vendedor@tienda.cl", "VENDEDOR", 5, "user")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals("VENDEDOR", decoded.getClaim("rol").asString())
    }

    @Test
    fun `token generado contiene el tenant_id cuando se provee`() {
        val token = jwtService.generateToken("admin@siga.cl", "ADMIN", 42, "customer")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals(42, decoded.getClaim("tenantId").asInt())
    }

    @Test
    fun `token generado NO contiene tenant_id cuando es null`() {
        val token = jwtService.generateToken("superadmin@siga.cl", "SUPERADMIN", null, "user")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertTrue(decoded.getClaim("tenantId").isMissing)
    }

    @Test
    fun `token generado tiene fecha de expiracion a 24 horas`() {
        val token = jwtService.generateToken("test@siga.cl", "USER", 1, "customer")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        val issuedAt = decoded.issuedAt.toInstant()
        val expiresAt = decoded.expiresAt.toInstant()
        val hours = java.time.Duration.between(issuedAt, expiresAt).toHours()

        assertEquals(24, hours)
    }

    @Test
    fun `token generado contiene principalType claim`() {
        val token = jwtService.generateToken("user@siga.cl", "OPERATOR", 1, "user")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals("user", decoded.getClaim("principalType").asString())
    }

    @Test
    fun `token generado con principalType customer`() {
        val token = jwtService.generateToken("owner@siga.cl", "customer", 1, "customer")
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)

        assertEquals("customer", decoded.getClaim("principalType").asString())
    }

    @Test
    fun `verify devuelve DecodedJWT valido para token correcto`() {
        val token = jwtService.generateToken("test@siga.cl", "ADMIN", 1, "customer")
        val decoded = jwtService.verify(token)

        assertNotNull(decoded)
        assertEquals("test@siga.cl", decoded.subject)
        assertEquals("ADMIN", decoded.getClaim("rol").asString())
        assertEquals(1, decoded.getClaim("tenantId").asInt())
    }

    @Test
    fun `verify lanza excepcion para token con firma invalida`() {
        val otherSecret = "different-secret-key-that-is-not-the-same-one-12345"
        val algorithm = Algorithm.HMAC256(otherSecret)
        val token = JWT.create()
            .withSubject("test@siga.cl")
            .withClaim("rol", "ADMIN")
            .withClaim("tenantId", 1)
            .withClaim("principalType", "customer")
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
            .sign(algorithm)

        val exception = assertThrows<RuntimeException> {
            jwtService.verify(token)
        }
        assertNotNull(exception)
    }

    @Test
    fun `verify lanza excepcion para token expirado`() {
        val algorithm = Algorithm.HMAC256(secret)
        val token = JWT.create()
            .withSubject("expired@siga.cl")
            .withClaim("rol", "ADMIN")
            .withClaim("tenantId", 1)
            .withClaim("principalType", "customer")
            .withIssuedAt(Instant.now().minus(48, ChronoUnit.HOURS))
            .withExpiresAt(Instant.now().minus(24, ChronoUnit.HOURS))
            .sign(algorithm)

        val exception = assertThrows<RuntimeException> {
            jwtService.verify(token)
        }
        assertNotNull(exception)
    }

    @Test
    fun `extractClaims devuelve todos los claims del token`() {
        val token = jwtService.generateToken("claims@siga.cl", "OPERATOR", 7, "user")
        val claims = jwtService.extractClaims(token)

        assertEquals("claims@siga.cl", claims["email"])
        assertEquals("OPERATOR", claims["rol"])
        assertEquals(7, claims["tenantId"])
        assertEquals("user", claims["principalType"])
    }

    @Test
    fun `extractClaims no incluye tenant_id cuando es null`() {
        val token = jwtService.generateToken("no-tenant@siga.cl", "ADMIN", null, "customer")
        val claims = jwtService.extractClaims(token)

        assertEquals("no-tenant@siga.cl", claims["email"])
        assertEquals("ADMIN", claims["rol"])
        assertEquals("customer", claims["principalType"])
        assertNull(claims["tenantId"])
    }
}
