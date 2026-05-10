package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.Instant
import java.util.*

/**
 * Unit test for [VerifyCustomerUseCase] with Mockito.
 * Tests token validation, expiry check, and customer activation.
 */
class VerifyCustomerUseCaseTest {

    private val customerRepositoryPort = mock(CustomerRepositoryPort::class.java)
    private val useCase = VerifyCustomerUseCase(customerRepositoryPort)

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `verify activates customer for valid token`() {
        val token = UUID.randomUUID().toString()
        val customer = Customer(
            id = 1,
            email = "verify_${UUID.randomUUID()}@test.com",
            passwordHash = "hash",
            name = "Test",
            isActive = false,
            emailVerified = false,
            verificationToken = token,
            verificationTokenExpiresAt = Instant.now().plusSeconds(3600)
        )

        `when`(customerRepositoryPort.findByVerificationToken(token)).thenReturn(customer)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0)
        }

        val result = useCase.verify(token)

        assertTrue(result.isActive)
        assertTrue(result.emailVerified)
        assertNull(result.verificationToken)
        assertNull(result.verificationTokenExpiresAt)
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `verify throws exception for expired token`() {
        val token = UUID.randomUUID().toString()
        val customer = Customer(
            id = 1,
            email = "expired_${UUID.randomUUID()}@test.com",
            passwordHash = "hash",
            name = "Test",
            isActive = false,
            emailVerified = false,
            verificationToken = token,
            verificationTokenExpiresAt = Instant.now().minusSeconds(3600) // 1 hour ago
        )

        `when`(customerRepositoryPort.findByVerificationToken(token)).thenReturn(customer)

        val exception = assertThrows<IllegalStateException> {
            useCase.verify(token)
        }
        assertEquals("Verification token has expired", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `verify throws exception for invalid token`() {
        val token = UUID.randomUUID().toString()

        `when`(customerRepositoryPort.findByVerificationToken(token)).thenReturn(null)

        val exception = assertThrows<NoSuchElementException> {
            useCase.verify(token)
        }
        assertEquals("Invalid verification token", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }
}
