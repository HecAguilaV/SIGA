package com.siga.auth.security

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class JwtServiceTests {

    @Test
    fun `generateToken should create a valid signed JWT string`() {
        // Arrange
        val secret = "super-secret-key-too-long-to-be-secure-enough-probably-123456"
        val jwtService = JwtService(secret)

        // Act
        val token = jwtService.generateToken("admin@siga.cl", "ADMINISTRADOR", tenantId = null, principalType = "customer")

        // Assert
        assertNotNull(token)
        assertTrue(token.split(".").size == 3, "JWT should have 3 parts")
    }

    @Test
    fun `generateToken should include tenantId claim if provided`() {
        // Arrange
        val secret = "super-secret-key-too-long-to-be-secure-enough-probably-123456"
        val jwtService = JwtService(secret)

        // Act
        val token = jwtService.generateToken("admin@siga.cl", "ADMINISTRADOR", tenantId = 50, principalType = "customer")

        // Assert: auth0 jwt decoder
        val decoded = com.auth0.jwt.JWT.decode(token)
        assertEquals(50, decoded.getClaim("tenantId").asInt())
    }
}
