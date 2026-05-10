package com.siga.auth

import com.siga.auth.domain.port.CustomerRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

/**
 * Integration test for the register → verify flow.
 *
 * Tests the complete stack: Controller → UseCase → Adapter → H2
 * via MockMvc using BaseIntegrationTest (addFilters = false).
 */
class AuthRegistrationIntegrationTest @Autowired constructor(
    private val customerRepositoryPort: CustomerRepositoryPort
) : BaseIntegrationTest() {

    @Test
    fun `register creates pending customer and returns 201`() {
        val email = "reg_integration_${UUID.randomUUID()}@test.com"
        val requestBody = """
            {
                "email": "$email",
                "password": "SecurePass123!",
                "name": "Integration Test",
                "companyName": "Test Corp"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("pending"))

        // Verify customer was persisted with pending state
        val customer = customerRepositoryPort.findByEmail(email)
        assertNotNull(customer)
        assertFalse(customer!!.isActive)
        assertFalse(customer.emailVerified)
        assertNotNull(customer.verificationToken)
        assertNotNull(customer.verificationTokenExpiresAt)
    }

    @Test
    fun `register then verify activates customer`() {
        val email = "reg_verify_${UUID.randomUUID()}@test.com"
        val requestBody = """
            {
                "email": "$email",
                "password": "Pass123!",
                "name": "Verify Test",
                "companyName": "Verify Corp"
            }
        """.trimIndent()

        // Step 1: Register
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)

        // Step 2: Get the verification token from the database
        val customer = customerRepositoryPort.findByEmail(email)!!
        val token = customer.verificationToken
        assertNotNull(token)

        // Step 3: Verify
        mockMvc.perform(
            get("/api/v1/auth/verify")
                .param("token", token!!)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("verified"))

        // Step 4: Verify customer is now active
        val verified = customerRepositoryPort.findByEmail(email)
        assertNotNull(verified)
        assertTrue(verified!!.isActive)
        assertTrue(verified.emailVerified)
        assertNull(verified.verificationToken)
        assertNull(verified.verificationTokenExpiresAt)
    }

    @Test
    fun `register with duplicate email returns 400`() {
        val email = "duplicate_reg_${UUID.randomUUID()}@test.com"
        val requestBody = """
            {
                "email": "$email",
                "password": "Pass123!",
                "name": "Duplicate Test",
                "companyName": "Duplicate Corp"
            }
        """.trimIndent()

        // First registration
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)

        // Duplicate registration
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Email already exists: $email"))
    }

    @Test
    fun `verify with invalid token returns 404`() {
        mockMvc.perform(
            get("/api/v1/auth/verify")
                .param("token", "non-existent-token")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Invalid verification token"))
    }

    @Test
    fun `register with missing fields returns 400`() {
        val requestBody = """
            {
                "email": "",
                "password": "",
                "name": "",
                "companyName": ""
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `existing customer endpoints remain accessible`() {
        // Verify that the original CustomerController at /api/auth/customers still works
        mockMvc.perform(get("/api/auth/customers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `existing user endpoints remain accessible`() {
        // Verify that the original UserController at /api/v1/auth/users still works
        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }
}
