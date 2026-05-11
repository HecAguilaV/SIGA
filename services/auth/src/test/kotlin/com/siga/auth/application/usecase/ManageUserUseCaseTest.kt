package com.siga.auth.application.usecase

import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserRole
import com.siga.auth.domain.port.UserRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

/**
 * Unit test for [ManageUserUseCase] with Mockito.
 * Tests validation logic and port delegation without Spring context.
 * Follows the same pattern as ManageSubscriptionUseCaseTest in billing.
 */
class ManageUserUseCaseTest {

    private val userRepositoryPort = mock(UserRepositoryPort::class.java)
    private val useCase = ManageUserUseCase(userRepositoryPort)

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `create validates email not blank`() {
        val user = User(
            id = UUID.randomUUID(),
            email = "   ",
            passwordHash = "hash123",
            firstName = "John",
            role = UserRole.ADMINISTRATOR
        )

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(user)
        }
        assertEquals("Email must not be blank", exception.message)
        verify(userRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create validates passwordHash not blank`() {
        val user = User(
            id = UUID.randomUUID(),
            email = "test@test.com",
            passwordHash = "   ",
            firstName = "John",
            role = UserRole.ADMINISTRATOR
        )

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(user)
        }
        assertEquals("Password hash must not be blank", exception.message)
        verify(userRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create validates firstName not blank`() {
        val user = User(
            id = UUID.randomUUID(),
            email = "test@test.com",
            passwordHash = "hash123",
            firstName = "   ",
            role = UserRole.ADMINISTRATOR
        )

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(user)
        }
        assertEquals("First name must not be blank", exception.message)
        verify(userRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create throws IllegalArgumentException when email exists`() {
        val existingEmail = "existing@test.com"
        val user = User(
            id = UUID.randomUUID(),
            email = existingEmail,
            passwordHash = "hash123",
            firstName = "John",
            role = UserRole.ADMINISTRATOR
        )

        `when`(userRepositoryPort.existsByEmail(existingEmail)).thenReturn(true)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(user)
        }
        assertEquals("Email already exists: $existingEmail", exception.message)
        verify(userRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create calls save with id null`() {
        val user = User(
            id = UUID.randomUUID(),
            email = "new@test.com",
            passwordHash = "hash123",
            firstName = "John",
            role = UserRole.ADMINISTRATOR
        )
        val savedUser = user.copy(id = UUID.randomUUID())

        `when`(userRepositoryPort.existsByEmail("new@test.com")).thenReturn(false)
        `when`(userRepositoryPort.save(anyObject())).thenReturn(savedUser)

        val result = useCase.create(user)

        assertEquals(savedUser, result)
        verify(userRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `update throws IllegalArgumentException when user not found`() {
        val id = UUID.randomUUID()
        val user = User(
            id = id,
            email = "update@test.com",
            passwordHash = "hash123",
            firstName = "John",
            role = UserRole.ADMINISTRATOR
        )

        `when`(userRepositoryPort.findById(id)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.update(id, user)
        }
        assertEquals("User not found: $id", exception.message)
        verify(userRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `update saves when user exists`() {
        val id = UUID.randomUUID()
        val existingUser = User(
            id = id,
            email = "existing@test.com",
            passwordHash = "oldhash",
            firstName = "Old",
            role = UserRole.OPERATOR
        )
        val updatedUser = User(
            id = UUID.randomUUID(),
            email = "updated@test.com",
            passwordHash = "newhash",
            firstName = "Updated",
            role = UserRole.ADMINISTRATOR
        )
        val savedUser = updatedUser.copy(id = id)

        `when`(userRepositoryPort.findById(id)).thenReturn(existingUser)
        `when`(userRepositoryPort.save(anyObject())).thenReturn(savedUser)

        val result = useCase.update(id, updatedUser)

        assertEquals(savedUser, result)
        verify(userRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `findById delegates to port`() {
        val id = UUID.randomUUID()
        val user = User(
            id = id,
            email = "find@test.com",
            passwordHash = "hash",
            firstName = "Test",
            role = UserRole.OPERATOR
        )

        `when`(userRepositoryPort.findById(id)).thenReturn(user)

        val result = useCase.findById(id)

        assertEquals(user, result)
        verify(userRepositoryPort, times(1)).findById(id)
    }

    @Test
    fun `findAll delegates to port`() {
        val users = listOf(
            User(id = UUID.randomUUID(), email = "u1@test.com", passwordHash = "h", firstName = "U1", role = UserRole.OPERATOR),
            User(id = UUID.randomUUID(), email = "u2@test.com", passwordHash = "h", firstName = "U2", role = UserRole.CASHIER)
        )

        `when`(userRepositoryPort.findAll()).thenReturn(users)

        val result = useCase.findAll()

        assertEquals(users, result)
        verify(userRepositoryPort, times(1)).findAll()
    }

    @Test
    fun `findByCustomerId returns only users for given customer`() {
        val customerId = 1
        val tenantUsers = listOf(
            User(id = UUID.randomUUID(), email = "u1@test.com", passwordHash = "h", firstName = "U1", role = UserRole.OPERATOR, customerId = customerId),
            User(id = UUID.randomUUID(), email = "u2@test.com", passwordHash = "h", firstName = "U2", role = UserRole.CASHIER, customerId = customerId)
        )

        `when`(userRepositoryPort.findByCustomerId(customerId)).thenReturn(tenantUsers)

        val result = useCase.findByCustomerId(customerId)

        assertEquals(tenantUsers, result)
        verify(userRepositoryPort, times(1)).findByCustomerId(customerId)
    }

    @Test
    fun `findByCustomerId returns empty list for customer with no users`() {
        val customerId = 99

        `when`(userRepositoryPort.findByCustomerId(customerId)).thenReturn(emptyList())

        val result = useCase.findByCustomerId(customerId)

        assertTrue(result.isEmpty())
        verify(userRepositoryPort, times(1)).findByCustomerId(customerId)
    }

    @Test
    fun `findByEmail delegates to port`() {
        val email = "delegate@test.com"
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            passwordHash = "hash",
            firstName = "Test",
            role = UserRole.OPERATOR
        )

        `when`(userRepositoryPort.findByEmail(email)).thenReturn(user)

        val result = useCase.findByEmail(email)

        assertEquals(user, result)
        verify(userRepositoryPort, times(1)).findByEmail(email)
    }
}
