package com.siga.auth

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

/**
 * Integration test for SecurityConfig with Spring Security filters ENABLED.
 *
 * Tests:
 * - 2.7: Public endpoints accessible without authentication
 * - 2.8: Protected endpoints require valid JWT
 *
 * Unlike BaseIntegrationTest (addFilters = false), this test enables Spring Security
 * to verify that the SecurityFilterChain correctly permits public paths
 * and rejects unauthenticated requests to protected paths.
 */
@SpringBootTest
@AutoConfigureMockMvc  // Default: addFilters = true
@ActiveProfiles("test")
class SecurityConfigIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    // ===== 2.7: Public endpoints accessible without auth =====

    @Test
    fun `register endpoint is accessible without authentication`() {
        val email = "public_reg_${UUID.randomUUID()}@test.com"
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "$email", "password": "Pass123!", "name": "Public", "companyName": "Test"}
                """.trimIndent())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("pending"))
    }

    @Test
    fun `verify endpoint is accessible without authentication`() {
        mockMvc.perform(
            get("/api/v1/auth/verify")
                .param("token", "non-existent-token-${UUID.randomUUID()}")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `login endpoint is accessible without authentication`() {
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "nonexistent_${UUID.randomUUID()}@test.com", "password": "wrong"}""")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `actuator health is accessible without authentication`() {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk)
    }

    @Test
    fun `existing customer endpoint is accessible without authentication`() {
        // /api/v1/auth/customers/** is accessible without auth
        mockMvc.perform(get("/api/v1/auth/customers"))
            .andExpect(status().isOk)
    }

    // ===== 2.8: Protected endpoints require valid JWT =====

    @Test
    fun `GET users returns 401 without authentication`() {
        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `POST users returns 401 without authentication`() {
        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "noauth_${UUID.randomUUID()}@test.com", "passwordHash": "hash123", "firstName": "NoAuth", "role": "OPERATOR"}""")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET user by id returns 401 without authentication`() {
        mockMvc.perform(get("/api/v1/auth/users/${UUID.randomUUID()}"))
            .andExpect(status().isUnauthorized)
    }
}
