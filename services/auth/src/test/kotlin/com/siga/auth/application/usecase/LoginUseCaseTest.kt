package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.model.Permission
import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserPermission
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.port.PermissionRepositoryPort
import com.siga.auth.domain.port.PlatformAdminRepositoryPort
import com.siga.auth.domain.port.RolePermissionRepositoryPort
import com.siga.auth.domain.port.UserPermissionRepositoryPort
import com.siga.auth.security.JwtService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

/**
 * Unit test for [LoginUseCase] with Mockito.
 * Tests login resolution order: platform_admin → user → customer.
 */
class LoginUseCaseTest {

    private val manageCustomerUseCase = mock(ManageCustomerUseCase::class.java)
    private val manageUserUseCase = mock(ManageUserUseCase::class.java)
    private val platformAdminRepositoryPort = mock(PlatformAdminRepositoryPort::class.java)
    private val passwordEncoder = BCryptPasswordEncoder()
    private val jwtService = mock(JwtService::class.java)
    private val userPermissionRepositoryPort = mock(UserPermissionRepositoryPort::class.java)
    private val permissionRepositoryPort = mock(PermissionRepositoryPort::class.java)
    private val rolePermissionRepositoryPort = mock(RolePermissionRepositoryPort::class.java)
    private val useCase = LoginUseCase(
        manageCustomerUseCase,
        manageUserUseCase,
        platformAdminRepositoryPort,
        passwordEncoder,
        jwtService,
        userPermissionRepositoryPort,
        permissionRepositoryPort,
        rolePermissionRepositoryPort
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    private fun hashPassword(raw: String): String = passwordEncoder.encode(raw)!!

    @Test
    fun `customer login success returns LoginResult with principalType customer`() {
        val email = "customer_${UUID.randomUUID()}@test.com"
        val rawPassword = "SecurePass123!"
        val customer = Customer(
            id = 1,
            email = email,
            passwordHash = hashPassword(rawPassword),
            name = "Test Customer",
            companyName = "Test Corp",
            isActive = true,
            emailVerified = true
        )

        `when`(platformAdminRepositoryPort.findByEmail(email)).thenReturn(null)
        `when`(manageUserUseCase.findByEmail(email)).thenReturn(null)
        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(customer)
        `when`(jwtService.generateToken(anyString(), anyString(), any(), anyString(), anyList()))
            .thenReturn("mock-jwt-token")

        val result = useCase.login(email, rawPassword)

        assertEquals("mock-jwt-token", result.token)
        assertEquals(email, result.email)
        assertEquals(1, result.tenantId)
        assertEquals("customer", result.role)
        assertEquals("customer", result.principalType)
        assertNull(result.userId)
    }

    @Test
    fun `user login success returns LoginResult with principalType user and permissions`() {
        val email = "user_${UUID.randomUUID()}@test.com"
        val rawPassword = "UserPass123!"
        val userId = UUID.randomUUID()
        val user = User(
            id = userId,
            email = email,
            passwordHash = hashPassword(rawPassword),
            firstName = "Test",
            lastName = "User",
            role = UserRole.OPERATOR,
            isActive = true
        )
        val permissionId1 = UUID.randomUUID()
        val permissionId2 = UUID.randomUUID()
        val userPermissions = listOf(
            UserPermission(userId = userId, permissionId = permissionId1, assignedAt = java.time.Instant.now()),
            UserPermission(userId = userId, permissionId = permissionId2, assignedAt = java.time.Instant.now())
        )
        val permission1 = Permission(id = permissionId1, code = "SALES_REP", name = "Sales Rep", category = "SALES")
        val permission2 = Permission(id = permissionId2, code = "INVENTORY_VIEW", name = "Inventory View", category = "INVENTORY")

        `when`(platformAdminRepositoryPort.findByEmail(email)).thenReturn(null)
        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(null)
        `when`(manageUserUseCase.findByEmail(email)).thenReturn(user)
        `when`(jwtService.generateToken(anyString(), anyString(), any(), anyString(), anyList()))
            .thenReturn("mock-user-jwt")
        `when`(userPermissionRepositoryPort.findByUserId(userId)).thenReturn(userPermissions)
        `when`(permissionRepositoryPort.findById(permissionId1)).thenReturn(permission1)
        `when`(permissionRepositoryPort.findById(permissionId2)).thenReturn(permission2)

        val result = useCase.login(email, rawPassword)

        assertEquals("mock-user-jwt", result.token)
        assertEquals(email, result.email)
        assertEquals("user", result.principalType)
        assertEquals("OPERATOR", result.role)
        assertNull(result.tenantId)
        assertEquals(userId, result.userId)
        assertEquals(listOf("SALES_REP", "INVENTORY_VIEW"), result.permissions)
    }

    @Test
    fun `wrong password throws IllegalArgumentException`() {
        val email = "wrongpass_${UUID.randomUUID()}@test.com"
        val correctPassword = "CorrectPass123!"
        val wrongPassword = "WrongPass456!"
        val customer = Customer(
            id = 1,
            email = email,
            passwordHash = hashPassword(correctPassword),
            name = "Test",
            isActive = true,
            emailVerified = true
        )

        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(customer)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.login(email, wrongPassword)
        }
        assertEquals("Invalid credentials", exception.message)
    }

    @Test
    fun `inactive customer throws IllegalStateException`() {
        val email = "inactive_${UUID.randomUUID()}@test.com"
        val rawPassword = "Pass123!"
        val customer = Customer(
            id = 1,
            email = email,
            passwordHash = hashPassword(rawPassword),
            name = "Inactive",
            isActive = false,
            emailVerified = false
        )

        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(customer)

        val exception = assertThrows<IllegalStateException> {
            useCase.login(email, rawPassword)
        }
        assertEquals("Account is not active", exception.message)
    }

    @Test
    fun `inactive user throws IllegalStateException`() {
        val email = "inactive_user_${UUID.randomUUID()}@test.com"
        val rawPassword = "Pass123!"
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            passwordHash = hashPassword(rawPassword),
            firstName = "Inactive",
            lastName = "User",
            role = UserRole.CASHIER,
            isActive = false
        )

        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(null)
        `when`(manageUserUseCase.findByEmail(email)).thenReturn(user)

        val exception = assertThrows<IllegalStateException> {
            useCase.login(email, rawPassword)
        }
        assertEquals("Account is not active", exception.message)
    }

    @Test
    fun `neither customer nor user matches throws NoSuchElementException`() {
        val email = "nonexistent_${UUID.randomUUID()}@test.com"

        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(null)
        `when`(manageUserUseCase.findByEmail(email)).thenReturn(null)

        val exception = assertThrows<NoSuchElementException> {
            useCase.login(email, "anyPassword123!")
        }
        assertEquals("Invalid credentials", exception.message)
    }

    @Test
    fun `customer tried before user when both exist for same email`() {
        val email = "exists_as_both@test.com"
        val rawPassword = "Pass123!"
        val customer = Customer(
            id = 5,
            email = email,
            passwordHash = hashPassword(rawPassword),
            name = "Customer First",
            isActive = true,
            emailVerified = true
        )

        `when`(platformAdminRepositoryPort.findByEmail(email)).thenReturn(null)
        `when`(manageUserUseCase.findByEmail(email)).thenReturn(null)
        `when`(manageCustomerUseCase.findByEmail(email)).thenReturn(customer)
        `when`(jwtService.generateToken(anyString(), anyString(), any(), anyString(), anyList()))
            .thenReturn("customer-first-jwt")

        val result = useCase.login(email, rawPassword)

        assertEquals("customer-first-jwt", result.token)
        assertEquals(5, result.tenantId)
        assertEquals("customer", result.principalType)
    }
}
