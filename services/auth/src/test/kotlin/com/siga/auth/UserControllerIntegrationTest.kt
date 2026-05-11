package com.siga.auth

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.port.UserRepositoryPort
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

/**
 * Integration test for UserController tenant scoping (Phase 5).
 *
 * Uses BaseIntegrationTest (addFilters=false) to bypass Spring Security filter chain,
 * then manually sets the SecurityContext with JWT claims to simulate auth.
 * This avoids body-consumption issues with POST requests through the security filter chain.
 *
 * Tasks:
 * - 5.4: GET /api/v1/auth/users returns only users with customerId from JWT
 * - 5.5: POST /api/v1/auth/users auto-sets customerId from JWT
 * - 5.6: Non-Customer principal (User) gets 403 for User CRUD
 */
class UserControllerIntegrationTest @Autowired constructor(
    private val userRepositoryPort: UserRepositoryPort,
    private val passwordEncoder: PasswordEncoder
) : BaseIntegrationTest() {

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    /**
     * Sets the SecurityContext with a customer principal (tenantId = customerTenantId).
     */
    private fun authenticateAsCustomer(customerTenantId: Int) {
        val details = mapOf(
            "email" to "customer@test.com",
            "rol" to "customer",
            "principalType" to "customer",
            "tenantId" to customerTenantId
        )
        val auth = UsernamePasswordAuthenticationToken(
            "customer@test.com",
            null,
            listOf(SimpleGrantedAuthority("ROLE_customer"))
        ).apply { this.details = details }
        SecurityContextHolder.getContext().authentication = auth
    }

    /**
     * Sets the SecurityContext with a user (employee) principal (no tenantId).
     */
    private fun authenticateAsUser() {
        val details = mapOf(
            "email" to "employee@test.com",
            "rol" to "CASHIER",
            "principalType" to "user"
        )
        val auth = UsernamePasswordAuthenticationToken(
            "employee@test.com",
            null,
            listOf(SimpleGrantedAuthority("ROLE_CASHIER"))
        ).apply { this.details = details }
        SecurityContextHolder.getContext().authentication = auth
    }

    /**
     * Creates a User domain object and serializes it to JSON for request body.
     * Uses the auto-configured ObjectMapper (from BaseIntegrationTest) to ensure
     * consistent serialization with the controller's @RequestBody deserialization.
     */
    private fun createUserJson(
        email: String,
        passwordHash: String = "hash123",
        firstName: String = "Test",
        role: UserRole = UserRole.OPERATOR,
        customerId: Int? = null
    ): String {
        val user = User(
            id = null,
            email = email,
            passwordHash = passwordHash,
            firstName = firstName,
            role = role,
            customerId = customerId
        )
        return objectMapper.writeValueAsString(user)
    }

    // ===== 5.4: GET /api/v1/auth/users returns only users with customerId from JWT =====

    @Test
    fun `GET users returns only users for authenticated customer tenant`() {
        val customerTenantId = 42
        authenticateAsCustomer(customerTenantId)

        // Create a user in the same tenant
        val tenantUser = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "tenant_user_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "Tenant",
                role = UserRole.OPERATOR,
                customerId = customerTenantId
            )
        )
        // Create a user in another tenant
        userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "other_tenant_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "Other",
                role = UserRole.CASHIER,
                customerId = 99
            )
        )

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].email").value(tenantUser.email))
            .andExpect(jsonPath("$[0].customerId").value(customerTenantId))
    }

    @Test
    fun `GET users returns empty list for customer with no users`() {
        authenticateAsCustomer(77)

        // Create only a user in another tenant
        userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "other_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "Other",
                role = UserRole.OPERATOR,
                customerId = 99
            )
        )

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))
    }

    // ===== 5.5: POST /api/v1/auth/users auto-sets customerId from JWT =====

    @Test
    fun `POST users auto-sets customerId from JWT`() {
        val customerTenantId = 55
        authenticateAsCustomer(customerTenantId)

        val email = "auto_set_${UUID.randomUUID()}@test.com"
        val requestBody = createUserJson(email = email, firstName = "AutoSet")

        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.customerId").value(customerTenantId))

        // Verify the user is persisted with correct customerId
        val saved = userRepositoryPort.findByEmail(email)
        assertTrue(saved != null && saved.customerId == customerTenantId)
    }

    @Test
    fun `POST users ignores customerId from request body and uses JWT claim`() {
        val customerTenantId = 66
        authenticateAsCustomer(customerTenantId)

        val email = "spoof_${UUID.randomUUID()}@test.com"
        // Request body tries to set customerId=999 (spoof attempt)
        val requestBody = createUserJson(
            email = email,
            firstName = "Secure",
            role = UserRole.CASHIER,
            customerId = 999
        )

        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.customerId").value(customerTenantId)) // NOT 999
    }

    // ===== 5.6: Non-Customer principal (User) gets 403 for User CRUD =====

    @Test
    fun `POST users returns 403 for user principal`() {
        authenticateAsUser()

        val requestBody = createUserJson(email = "new_${UUID.randomUUID()}@test.com")

        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET users returns 403 for user principal`() {
        authenticateAsUser()

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET user by id returns 403 for user principal`() {
        authenticateAsUser()

        mockMvc.perform(get("/api/v1/auth/users/${UUID.randomUUID()}"))
            .andExpect(status().isForbidden)
    }
}
