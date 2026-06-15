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
    fun `register with duplicate email returns 409`() {
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
            .andExpect(status().isConflict)
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
    fun `minimal registration with only email and password returns 201`() {
        val email = "minimal_${UUID.randomUUID()}@test.com"
        val requestBody = """
            {
                "email": "$email",
                "password": "SecurePass123!"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("pending"))

        // Verify customer was created with email prefix as name
        val customer = customerRepositoryPort.findByEmail(email)
        assertNotNull(customer)
        assertEquals(email.substringBefore("@"), customer!!.name)
        assertNull(customer.companyName)
        assertFalse(customer.isActive)
        assertFalse(customer.emailVerified)
        assertNotNull(customer.verificationToken)
    }

    @Test
    fun `full registration with name and companyName still works`() {
        val email = "full_integration_${UUID.randomUUID()}@test.com"
        val requestBody = """
            {
                "email": "$email",
                "password": "SecurePass123!",
                "name": "María García",
                "companyName": "Mi Empresa SRL"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("pending"))

        // Verify customer was created with provided name and companyName
        val customer = customerRepositoryPort.findByEmail(email)
        assertNotNull(customer)
        assertEquals("María García", customer!!.name)
        assertEquals("Mi Empresa SRL", customer.companyName)
    }

    @Test
    fun `existing customer endpoints remain accessible`() {
        // Verify that CustomerController at /api/v1/auth/customers still works
        mockMvc.perform(get("/api/v1/auth/customers"))
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

    @Test
    fun `register then verify then login full flow`() {
        val email = "full_flow_${UUID.randomUUID()}@test.com"
        val password = "Pass123!"

        // Step 1: Register
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "$email", "password": "$password", "name": "Full Flow", "companyName": "Flow Corp"}
                """.trimIndent())
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("pending"))

        // Step 2: Get the verification token from database
        val customer = customerRepositoryPort.findByEmail(email)!!
        val token = customer.verificationToken
        assertNotNull(token)
        assertFalse(customer.isActive)
        assertFalse(customer.emailVerified)

        // Step 3: Verify email
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

        // Step 5: Login with valid credentials
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "$email", "password": "$password"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.principalType").value("customer"))
            .andExpect(jsonPath("$.tenantId").exists())
            .andExpect(jsonPath("$.role").value("customer"))
    }

    @Test
    fun `login with wrong password after verification returns 401`() {
        val email = "wrong_pass_${UUID.randomUUID()}@test.com"
        val password = "CorrectPass123!"

        // Register
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "$email", "password": "$password", "name": "Wrong Pass", "companyName": "Test"}
                """.trimIndent())
        )
            .andExpect(status().isCreated)

        // Verify email first to activate the customer
        val customer = customerRepositoryPort.findByEmail(email)!!
        val token = customer.verificationToken
        assertNotNull(token)
        mockMvc.perform(
            get("/api/v1/auth/verify")
                .param("token", token!!)
        )
            .andExpect(status().isOk)

        // Try login with wrong password on active account
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "$email", "password": "WrongPass456!"}""")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").value("Invalid credentials"))
    }

    @Test
    fun `login with non-existent email returns 401`() {
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "nonexistent_${UUID.randomUUID()}@test.com", "password": "anyPassword"}""")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").value("Invalid credentials"))
    }

    @Test
    fun `login with unverified customer returns 403`() {
        val email = "unverified_${UUID.randomUUID()}@test.com"
        val password = "Pass123!"

        // Register (creates inactive customer)
        mockMvc.perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "$email", "password": "$password", "name": "Unverified", "companyName": "Test"}
                """.trimIndent())
        )
            .andExpect(status().isCreated)

        // Try login without verifying
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "$email", "password": "$password"}""")
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.error").value("Account is not active"))
    }
}
