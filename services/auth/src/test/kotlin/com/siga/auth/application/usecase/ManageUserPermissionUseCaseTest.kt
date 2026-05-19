package com.siga.auth.application.usecase

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserPermission
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.port.UserPermissionRepositoryPort
import com.siga.auth.domain.port.UserRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.Instant
import java.util.*

/**
 * Unit test for [ManageUserPermissionUseCase] with Mockito.
 * Tests assign, revoke, verify, and cross-tenant rejection.
 */
class ManageUserPermissionUseCaseTest {

    private val userPermissionRepositoryPort = mock(UserPermissionRepositoryPort::class.java)
    private val userRepositoryPort = mock(UserRepositoryPort::class.java)
    private val useCase = ManageUserPermissionUseCase(userPermissionRepositoryPort, userRepositoryPort)

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `assign creates user permission when tenant matches`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val assignedBy = UUID.randomUUID()
        val tenantId = 1

        val user = User(
            id = userId,
            email = "user@test.com",
            passwordHash = "hash",
            firstName = "Test",
            role = UserRole.EMPLOYEE,
            customerId = tenantId
        )

        val savedPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now(),
            assignedBy = assignedBy
        )

        `when`(userRepositoryPort.findById(userId)).thenReturn(user)
        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(false)
        `when`(userPermissionRepositoryPort.save(anyObject())).thenReturn(savedPermission)

        val result = useCase.assign(userId, permissionId, assignedBy, tenantId)

        assertEquals(userId, result.userId)
        assertEquals(permissionId, result.permissionId)
        assertEquals(assignedBy, result.assignedBy)

        verify(userRepositoryPort, times(1)).findById(userId)
        verify(userPermissionRepositoryPort, times(1)).existsByUserIdAndPermissionId(userId, permissionId)
        verify(userPermissionRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `assign throws when permission already assigned`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val assignedBy = UUID.randomUUID()
        val tenantId = 1

        val user = User(
            id = userId,
            email = "user@test.com",
            passwordHash = "hash",
            firstName = "Test",
            role = UserRole.EMPLOYEE,
            customerId = tenantId
        )

        `when`(userRepositoryPort.findById(userId)).thenReturn(user)
        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(true)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.assign(userId, permissionId, assignedBy, tenantId)
        }
        assertEquals("Permission already assigned to user", exception.message)
        verify(userPermissionRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `assign throws cross-tenant access denied`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val assignedBy = UUID.randomUUID()
        val assignerTenantId = 1
        val userTenantId = 2

        val user = User(
            id = userId,
            email = "user@other.com",
            passwordHash = "hash",
            firstName = "Other Tenant",
            role = UserRole.OPERATOR,
            customerId = userTenantId
        )

        `when`(userRepositoryPort.findById(userId)).thenReturn(user)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.assign(userId, permissionId, assignedBy, assignerTenantId)
        }
        assertTrue(exception.message!!.contains("Cross-tenant access denied"))
        verify(userPermissionRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `assign throws when user not found`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val assignedBy = UUID.randomUUID()
        val tenantId = 1

        `when`(userRepositoryPort.findById(userId)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.assign(userId, permissionId, assignedBy, tenantId)
        }
        assertEquals("User not found: $userId", exception.message)
        verify(userPermissionRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `assign skips tenant validation when tenantId is null`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val assignedBy = UUID.randomUUID()

        val savedPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now(),
            assignedBy = assignedBy
        )

        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(false)
        `when`(userPermissionRepositoryPort.save(anyObject())).thenReturn(savedPermission)

        val result = useCase.assign(userId, permissionId, assignedBy, null)

        assertEquals(userId, result.userId)
        assertEquals(permissionId, result.permissionId)
        verify(userRepositoryPort, never()).findById(anyObject())
        verify(userPermissionRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `revoke deletes user permission when tenant matches`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val tenantId = 1

        val user = User(
            id = userId,
            email = "user@test.com",
            passwordHash = "hash",
            firstName = "Test",
            role = UserRole.EMPLOYEE,
            customerId = tenantId
        )

        `when`(userRepositoryPort.findById(userId)).thenReturn(user)
        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(true)

        useCase.revoke(userId, permissionId, tenantId)

        verify(userPermissionRepositoryPort, times(1)).deleteByUserIdAndPermissionId(userId, permissionId)
    }

    @Test
    fun `revoke throws when permission not assigned`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val tenantId = 1

        val user = User(
            id = userId,
            email = "user@test.com",
            passwordHash = "hash",
            firstName = "Test",
            role = UserRole.EMPLOYEE,
            customerId = tenantId
        )

        `when`(userRepositoryPort.findById(userId)).thenReturn(user)
        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(false)

        val exception = assertThrows<NoSuchElementException> {
            useCase.revoke(userId, permissionId, tenantId)
        }
        assertTrue(exception.message!!.contains("UserPermission not found"))
        verify(userPermissionRepositoryPort, never()).deleteByUserIdAndPermissionId(anyObject(), anyObject())
    }

    @Test
    fun `revoke throws cross-tenant access denied`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()
        val assignerTenantId = 1
        val userTenantId = 2

        val user = User(
            id = userId,
            email = "user@other.com",
            passwordHash = "hash",
            firstName = "Other",
            role = UserRole.CASHIER,
            customerId = userTenantId
        )

        `when`(userRepositoryPort.findById(userId)).thenReturn(user)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.revoke(userId, permissionId, assignerTenantId)
        }
        assertTrue(exception.message!!.contains("Cross-tenant access denied"))
        verify(userPermissionRepositoryPort, never()).deleteByUserIdAndPermissionId(anyObject(), anyObject())
    }

    @Test
    fun `findByUserId delegates to port`() {
        val userId = UUID.randomUUID()
        val permissions = listOf(
            UserPermission(userId = userId, permissionId = UUID.randomUUID(), assignedAt = Instant.now()),
            UserPermission(userId = userId, permissionId = UUID.randomUUID(), assignedAt = Instant.now())
        )

        `when`(userPermissionRepositoryPort.findByUserId(userId)).thenReturn(permissions)

        val result = useCase.findByUserId(userId)

        assertEquals(permissions, result)
        verify(userPermissionRepositoryPort, times(1)).findByUserId(userId)
    }

    @Test
    fun `findByUserId returns empty list when user has no permissions`() {
        val userId = UUID.randomUUID()

        `when`(userPermissionRepositoryPort.findByUserId(userId)).thenReturn(emptyList())

        val result = useCase.findByUserId(userId)

        assertTrue(result.isEmpty())
        verify(userPermissionRepositoryPort, times(1)).findByUserId(userId)
    }

    @Test
    fun `existsByUserIdAndPermissionId delegates to port`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()

        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(true)

        val result = useCase.existsByUserIdAndPermissionId(userId, permissionId)

        assertTrue(result)
        verify(userPermissionRepositoryPort, times(1)).existsByUserIdAndPermissionId(userId, permissionId)
    }

    @Test
    fun `existsByUserIdAndPermissionId returns false when not assigned`() {
        val userId = UUID.randomUUID()
        val permissionId = UUID.randomUUID()

        `when`(userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)).thenReturn(false)

        val result = useCase.existsByUserIdAndPermissionId(userId, permissionId)

        assertFalse(result)
        verify(userPermissionRepositoryPort, times(1)).existsByUserIdAndPermissionId(userId, permissionId)
    }
}
