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
     * Sets the SecurityContext with an OWNER user principal scoped to a tenant.
     * Used to verify the multitenancy fix: OWNER of tenant X only sees users of X.
     */
    private fun authenticateAsOwnerUser(customerTenantId: Int) {
        val details = mapOf(
            "email" to "owner@test.com",
            "rol" to "OWNER",
            "principalType" to "user",
            "tenantId" to customerTenantId
        )
        val auth = UsernamePasswordAuthenticationToken(
            "owner@test.com",
            null,
            listOf(SimpleGrantedAuthority("ROLE_OWNER"))
        ).apply { this.details = details }
        SecurityContextHolder.getContext().authentication = auth
    }

    /**
     * Sets the SecurityContext with a platform_admin principal.
     * Platform admins are SaaS owners, not tenant users — they get 403 on /users.
     */
    private fun authenticateAsPlatformAdmin() {
        val details = mapOf(
            "email" to "platformadmin@siga.cl",
            "rol" to "PLATFORM_ADMIN",
            "principalType" to "platform_admin"
        )
        val auth = UsernamePasswordAuthenticationToken(
            "platformadmin@siga.cl",
            null,
            listOf(SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN"))
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

    // ===== Additional coverage: GET /{id} =====

    @Test
    fun `GET user by id returns user for same tenant`() {
        val customerTenantId = 100
        authenticateAsCustomer(customerTenantId)

        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "by_id_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "ById",
                role = UserRole.OPERATOR,
                customerId = customerTenantId
            )
        )

        mockMvc.perform(get("/api/v1/auth/users/${user.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(user.email))
            .andExpect(jsonPath("$.customerId").value(customerTenantId))
    }

    @Test
    fun `GET user by id returns 404 for wrong tenant`() {
        authenticateAsCustomer(200)

        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "wrong_tenant_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "Wrong",
                role = UserRole.OPERATOR,
                customerId = 999
            )
        )

        mockMvc.perform(get("/api/v1/auth/users/${user.id}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `GET user by id returns 404 when not found`() {
        authenticateAsCustomer(300)

        mockMvc.perform(get("/api/v1/auth/users/${UUID.randomUUID()}"))
            .andExpect(status().isNotFound)
    }

    // ===== Additional coverage: GET /email/{email} =====

    @Test
    fun `GET user by email returns user for same tenant`() {
        val customerTenantId = 400
        authenticateAsCustomer(customerTenantId)

        val email = "by_email_${UUID.randomUUID()}@test.com"
        userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = email,
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "ByEmail",
                role = UserRole.OPERATOR,
                customerId = customerTenantId
            )
        )

        mockMvc.perform(get("/api/v1/auth/users/email/$email"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.customerId").value(customerTenantId))
    }

    @Test
    fun `GET user by email returns 404 for wrong tenant`() {
        authenticateAsCustomer(500)

        val email = "wrong_email_${UUID.randomUUID()}@test.com"
        userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = email,
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "WrongEmail",
                role = UserRole.OPERATOR,
                customerId = 999
            )
        )

        mockMvc.perform(get("/api/v1/auth/users/email/$email"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `GET user by email returns 403 for user principal`() {
        authenticateAsUser()

        mockMvc.perform(get("/api/v1/auth/users/email/test@test.com"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET user by email returns 404 when not found`() {
        authenticateAsCustomer(600)

        mockMvc.perform(get("/api/v1/auth/users/email/nonexistent@test.com"))
            .andExpect(status().isNotFound)
    }

    // ===== Additional coverage: PUT /{id} =====

    @Test
    fun `PUT user updates user for same tenant`() {
        val customerTenantId = 700
        authenticateAsCustomer(customerTenantId)

        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "put_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "Original",
                role = UserRole.OPERATOR,
                customerId = customerTenantId
            )
        )

        val body = createUserJson(
            email = user.email,
            firstName = "Updated",
            role = UserRole.CASHIER,
            customerId = customerTenantId
        )

        mockMvc.perform(
            put("/api/v1/auth/users/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.customerId").value(customerTenantId))
    }

    @Test
    fun `PUT user returns 404 for wrong tenant`() {
        authenticateAsCustomer(800)

        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "put_wrong_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "WrongTenant",
                role = UserRole.OPERATOR,
                customerId = 999
            )
        )

        mockMvc.perform(
            put("/api/v1/auth/users/${user.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson(email = "x@x.com"))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `PUT user returns 404 when not found`() {
        authenticateAsCustomer(900)

        mockMvc.perform(
            put("/api/v1/auth/users/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson(email = "x@test.com"))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `PUT user returns 403 for user principal`() {
        authenticateAsUser()

        mockMvc.perform(
            put("/api/v1/auth/users/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson(email = "x@test.com"))
        )
            .andExpect(status().isForbidden)
    }

    // ===== No auth: must be 403, never leak across tenants (no backward compat) =====

    @Test
    fun `GET user by email without auth returns 403`() {
        val email = "noauth_email_${UUID.randomUUID()}@test.com"
        userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = email,
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "NoAuthEmail",
                role = UserRole.OPERATOR
            )
        )

        mockMvc.perform(get("/api/v1/auth/users/email/$email"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET user by id without auth returns 403`() {
        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "noauth_id_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "NoAuthID",
                role = UserRole.OPERATOR
            )
        )

        mockMvc.perform(get("/api/v1/auth/users/${user.id}"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET users without auth returns 403`() {
        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "noauth_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "NoAuth",
                role = UserRole.OPERATOR
            )
        )

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isForbidden)
    }

    // ===== Multitenancy fix: OWNER of tenant only sees users of their tenant =====

    @Test
    fun `GET users as OWNER user principal returns only same-tenant users (multitenancy)`() {
        val ownerTenantId = 1234
        authenticateAsOwnerUser(ownerTenantId)

        val tenantUser = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "owner_tenant_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "SameTenant",
                role = UserRole.OPERATOR,
                customerId = ownerTenantId
            )
        )
        userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "other_tenant_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "OtherTenant",
                role = UserRole.CASHIER,
                customerId = 9999
            )
        )

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].email").value(tenantUser.email))
            .andExpect(jsonPath("$[0].customerId").value(ownerTenantId))
    }

    @Test
    fun `GET users as OWNER user principal with no tenantId returns 403`() {
        val details = mapOf(
            "email" to "orphan_owner@test.com",
            "rol" to "OWNER",
            "principalType" to "user"
        )
        val auth = UsernamePasswordAuthenticationToken(
            "orphan_owner@test.com",
            null,
            listOf(SimpleGrantedAuthority("ROLE_OWNER"))
        ).apply { this.details = details }
        SecurityContextHolder.getContext().authentication = auth

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isForbidden)
    }

    // ===== Platform admin: 403 on /users (they use /api/v1/platform/* instead) =====

    @Test
    fun `GET users as platform_admin returns 403`() {
        authenticateAsPlatformAdmin()
        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `POST users as platform_admin returns 403`() {
        authenticateAsPlatformAdmin()
        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson(email = "x@x.com"))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `PUT users as platform_admin returns 403`() {
        authenticateAsPlatformAdmin()
        mockMvc.perform(
            put("/api/v1/auth/users/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createUserJson(email = "x@x.com"))
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun `GET user by id as platform_admin returns 403`() {
        authenticateAsPlatformAdmin()
        val user = userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "pa_lookup_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass")!!,
                firstName = "PALookup",
                role = UserRole.OPERATOR,
                customerId = 1
            )
        )
        mockMvc.perform(get("/api/v1/auth/users/${user.id}"))
            .andExpect(status().isForbidden)
    }
}
