package com.siga.auth

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.port.UserRepositoryPort
import com.siga.auth.entity.Permission
import com.siga.auth.repository.PermissionRepository
import com.siga.auth.repository.UserPermissionRepository
import org.junit.jupiter.api.AfterEach
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
 * Integration test for PermissionController.
 *
 * Covers:
 * - Permission catalogue CRUD (GET/POST/PUT/DELETE /api/v1/auth/permissions)
 * - User-permission assignment (POST/GET/DELETE /api/v1/auth/users/{userId}/permissions)
 * - Permission verification (GET /api/v1/auth/users/{userId}/permissions/verify)
 * - Cross-tenant rejection
 *
 * Follows the same pattern as UserControllerIntegrationTest:
 * extends BaseIntegrationTest (addFilters=false), manually sets SecurityContext.
 */
class PermissionControllerIntegrationTest @Autowired constructor(
    private val permissionRepository: PermissionRepository,
    private val userPermissionRepository: UserPermissionRepository,
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
     * Creates a Permission entity and saves it via the JPA repository for test setup.
     */
    private fun createPermission(
        code: String = "TEST_${UUID.randomUUID().toString().take(8).uppercase()}",
        name: String = "Test Permission",
        category: String = "TEST",
        description: String? = "Test description"
    ): Permission {
        return permissionRepository.save(
            Permission(
                id = UUID.randomUUID(),
                code = code,
                name = name,
                category = category,
                description = description
            )
        )
    }

    /**
     * Creates a User domain model persisted via the port.
     */
    private fun createUser(customerId: Int? = null): User {
        return userRepositoryPort.save(
            User(
                id = UUID.randomUUID(),
                email = "user_${UUID.randomUUID()}@test.com",
                passwordHash = passwordEncoder.encode("pass123")!!,
                firstName = "Test",
                role = UserRole.EMPLOYEE,
                customerId = customerId
            )
        )
    }

    // ===== Permission Catalogue CRUD =====

    @Test
    fun `GET permissions returns all seeded permissions`() {
        authenticateAsCustomer(1)

        mockMvc.perform(get("/api/v1/auth/permissions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `POST permissions creates and returns new permission`() {
        authenticateAsCustomer(1)

        val code = "CREATE_${UUID.randomUUID().toString().take(8).uppercase()}"
        val requestBody = """{"code":"$code","name":"New Permission","category":"ADMIN","description":"Test desc","isActive":true}"""

        mockMvc.perform(
            post("/api/v1/auth/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value(code))
            .andExpect(jsonPath("$.name").value("New Permission"))
    }

    @Test
    fun `PUT permissions updates existing permission`() {
        authenticateAsCustomer(1)
        val existing = createPermission()

        val requestBody = """{"id":"${existing.id}","code":"${existing.code}","name":"Updated Name","category":"UPDATED","description":"Updated desc","isActive":true}"""

        mockMvc.perform(
            put("/api/v1/auth/permissions/${existing.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.category").value("UPDATED"))
    }

    @Test
    fun `DELETE permissions removes existing permission`() {
        authenticateAsCustomer(1)
        val existing = createPermission()

        mockMvc.perform(delete("/api/v1/auth/permissions/${existing.id}"))
            .andExpect(status().isNoContent())
    }

    @Test
    fun `DELETE permissions returns 404 for non-existent permission`() {
        authenticateAsCustomer(1)

        mockMvc.perform(delete("/api/v1/auth/permissions/${UUID.randomUUID()}"))
            .andExpect(status().isNotFound())
    }

    // ===== User-Permission Assignment =====

    @Test
    fun `POST assign permission to user returns assigned permission`() {
        val tenantId = 100
        authenticateAsCustomer(tenantId)
        val permission = createPermission()
        val user = createUser(customerId = tenantId)

        val requestBody = """{"permissionId": "${permission.id}"}"""

        mockMvc.perform(
            post("/api/v1/auth/users/${user.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(user.id.toString()))
            .andExpect(jsonPath("$.permissionId").value(permission.id.toString()))
    }

    @Test
    fun `GET user permissions returns assigned permissions`() {
        val tenantId = 101
        authenticateAsCustomer(tenantId)
        val permission = createPermission()
        val user = createUser(customerId = tenantId)

        // Assign permission
        val requestBody = """{"permissionId": "${permission.id}"}"""
        mockMvc.perform(
            post("/api/v1/auth/users/${user.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)

        // Verify list
        mockMvc.perform(get("/api/v1/auth/users/${user.id}/permissions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].permissionId").value(permission.id.toString()))
    }

    @Test
    fun `polyfunctional user gets all assigned permissions listed`() {
        val tenantId = 105
        authenticateAsCustomer(tenantId)
        val perm1 = createPermission(code = "KIOSK_ADMIN", name = "Kiosk Admin", category = "STORE")
        val perm2 = createPermission(code = "SALES_REP", name = "Sales Rep", category = "SALES")
        val user = createUser(customerId = tenantId)

        // Assign both permissions
        mockMvc.perform(
            post("/api/v1/auth/users/${user.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"permissionId": "${perm1.id}"}""")
        )
            .andExpect(status().isOk)

        mockMvc.perform(
            post("/api/v1/auth/users/${user.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"permissionId": "${perm2.id}"}""")
        )
            .andExpect(status().isOk)

        // Verify both are listed
        val result = mockMvc.perform(get("/api/v1/auth/users/${user.id}/permissions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andReturn()

        val json = objectMapper.readTree(result.response.contentAsString)
        val permissionIds = json.map { it.get("permissionId").asText() }.toSet()
        assert(permissionIds.contains(perm1.id.toString()))
        assert(permissionIds.contains(perm2.id.toString()))
    }

    @Test
    fun `DELETE revoke permission from user returns no content`() {
        val tenantId = 102
        authenticateAsCustomer(tenantId)
        val permission = createPermission()
        val user = createUser(customerId = tenantId)

        // Assign
        val requestBody = """{"permissionId": "${permission.id}"}"""
        val result = mockMvc.perform(
            post("/api/v1/auth/users/${user.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)
        val assignedPermissionId = responseJson.get("permissionId").asText()

        // Revoke
        mockMvc.perform(
            delete("/api/v1/auth/users/${user.id}/permissions/$assignedPermissionId")
        )
            .andExpect(status().isNoContent())
    }

    @Test
    fun `GET verify permission returns true when user has permission`() {
        val tenantId = 103
        authenticateAsCustomer(tenantId)
        val permission = createPermission()
        val user = createUser(customerId = tenantId)

        // Assign
        val requestBody = """{"permissionId": "${permission.id}"}"""
        mockMvc.perform(
            post("/api/v1/auth/users/${user.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)

        // Verify
        mockMvc.perform(
            get("/api/v1/auth/users/${user.id}/permissions/verify")
                .param("code", permission.id.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.hasPermission").value(true))
    }

    @Test
    fun `GET verify permission returns false when user does not have permission`() {
        val tenantId = 104
        authenticateAsCustomer(tenantId)
        val user = createUser(customerId = tenantId)

        mockMvc.perform(
            get("/api/v1/auth/users/${user.id}/permissions/verify")
                .param("code", UUID.randomUUID().toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.hasPermission").value(false))
    }

    // ===== Cross-Tenant Rejection =====

    @Test
    fun `POST assign permission returns 403 when cross-tenant`() {
        val tenantId = 200
        authenticateAsCustomer(tenantId)
        val permission = createPermission()

        // User belongs to DIFFERENT tenant (tenantId = 999)
        val otherTenantUser = createUser(customerId = 999)

        val requestBody = """{"permissionId": "${permission.id}"}"""

        mockMvc.perform(
            post("/api/v1/auth/users/${otherTenantUser.id}/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isForbidden())
    }
}
