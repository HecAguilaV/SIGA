package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.domain.port.EmailSenderPort
import com.siga.auth.event.EmailEventProducer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.Duration
import java.time.Instant
import java.util.*

/**
 * Unit test for [RegisterCustomerUseCase] with Mockito.
 * Tests validation, duplicate detection, password hashing, and email sending.
 */
class RegisterCustomerUseCaseTest {

    private val customerRepositoryPort = mock(CustomerRepositoryPort::class.java)
    private val emailSenderPort = mock(EmailSenderPort::class.java)
    private val emailEventProducer = mock(EmailEventProducer::class.java)
    private val passwordEncoder = BCryptPasswordEncoder()

    // Legacy sync mode use case (emailEventProducer=null)
    private val syncUseCase = RegisterCustomerUseCase(
        customerRepositoryPort,
        emailSenderPort,
        passwordEncoder,
        emailEventProducer = null,
        emailMode = "sync"
    )

    // Async mode use case
    private val asyncUseCase = RegisterCustomerUseCase(
        customerRepositoryPort,
        emailSenderPort,
        passwordEncoder,
        emailEventProducer = emailEventProducer,
        emailMode = "async"
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `register creates pending customer with hashed password and sends verification email`() {
        val email = "register_${UUID.randomUUID()}@test.com"
        val rawPassword = "SecurePass123!"
        val name = "Test User"
        val companyName = "Test Corp"

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            val customer = invocation.getArgument<Customer>(0)
            customer.copy(id = 1)
        }

        val result = syncUseCase.register(email, rawPassword, name, companyName)

        assertNotNull(result.id)
        assertEquals(email, result.email)
        assertEquals(name, result.name)
        assertEquals(companyName, result.companyName)
        assertFalse(result.isActive)
        assertFalse(result.emailVerified)
        assertNotNull(result.verificationToken)
        assertNotNull(result.verificationTokenExpiresAt)
        assertTrue(passwordEncoder.matches(rawPassword, result.passwordHash))

        verify(emailSenderPort, times(1)).sendVerificationEmail(anyObject(), anyObject(), anyObject())
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `register with async mode publishes event instead of calling EmailSenderPort`() {
        val email = "async_${UUID.randomUUID()}@test.com"

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result = asyncUseCase.register(email, "SecurePass123!", "Async User", "Async Corp")

        assertNotNull(result.id)

        // Async mode should NOT call EmailSenderPort
        verify(emailSenderPort, never()).sendVerificationEmail(anyObject(), anyObject(), anyObject())
        // Async mode SHOULD publish an event
        verify(emailEventProducer, times(1)).publish(anyObject())
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `register with sync mode still calls EmailSenderPort directly`() {
        val email = "sync_${UUID.randomUUID()}@test.com"

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result = syncUseCase.register(email, "SecurePass123!", "Sync User", "Sync Corp")

        assertNotNull(result.id)

        // Sync mode should call EmailSenderPort
        verify(emailSenderPort, times(1)).sendVerificationEmail(anyObject(), anyObject(), anyObject())
        // Sync mode should NOT publish an event
        verify(emailEventProducer, never()).publish(anyObject())
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `register throws exception for duplicate email`() {
        val email = "duplicate_${UUID.randomUUID()}@test.com"

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(true)

        val exception = assertThrows<IllegalArgumentException> {
            syncUseCase.register(email, "Pass123!", "Test", "Test Corp")
        }
        assertEquals("Email already exists: $email", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
        verify(emailSenderPort, never()).sendVerificationEmail(anyObject(), anyObject(), anyObject())
    }

    @Test
    fun `register throws exception for blank email`() {
        val exception = assertThrows<IllegalArgumentException> {
            syncUseCase.register("", "Pass123!", "Test", "Test Corp")
        }
        assertEquals("Email must not be blank", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `register throws exception for blank password`() {
        val exception = assertThrows<IllegalArgumentException> {
            syncUseCase.register("test@test.com", "", "Test", "Test Corp")
        }
        assertEquals("Password must not be blank", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `register uses email prefix as name when name is null`() {
        val email = "juan_${UUID.randomUUID()}@example.com"
        val prefix = email.substringBefore("@")

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result = syncUseCase.register(email, "SecurePass123!", null, null)

        assertEquals(prefix, result.name)
        assertNull(result.companyName)
        assertFalse(result.isActive)
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `register passes name and companyName through when provided`() {
        val email = "full_${UUID.randomUUID()}@test.com"

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result = syncUseCase.register(email, "Pass123!", "María García", "Mi Empresa SRL")

        assertEquals("María García", result.name)
        assertEquals("Mi Empresa SRL", result.companyName)
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `register uses email prefix when name is blank`() {
        val email = "blank_name_${UUID.randomUUID()}@test.com"
        val prefix = email.substringBefore("@")

        `when`(customerRepositoryPort.existsByEmail(email)).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result = syncUseCase.register(email, "Pass123!", "", "Some Corp")

        assertEquals(prefix, result.name)
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `register generates unique verification token`() {
        `when`(customerRepositoryPort.existsByEmail(anyObject())).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result1 = syncUseCase.register(
            "token_test_${UUID.randomUUID()}@test.com",
            "Pass123!", "User A", "Corp A"
        )
        val result2 = syncUseCase.register(
            "token_test2_${UUID.randomUUID()}@test.com",
            "Pass456!", "User B", "Corp B"
        )

        assertNotNull(result1.verificationToken)
        assertNotNull(result2.verificationToken)
        assertNotEquals(result1.verificationToken, result2.verificationToken)
    }

    @Test
    fun `register sets 24 hour expiry on verification token`() {
        `when`(customerRepositoryPort.existsByEmail(anyObject())).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenAnswer { invocation ->
            invocation.getArgument<Customer>(0).copy(id = 1)
        }

        val result = syncUseCase.register(
            "expiry_test_${UUID.randomUUID()}@test.com",
            "Pass123!", "Test", "Test Corp"
        )
        val now = Instant.now()
        val twentyFourHours = Duration.ofHours(24)

        assertNotNull(result.verificationTokenExpiresAt)
        val diff = Duration.between(now, result.verificationTokenExpiresAt)
        assertTrue(diff.seconds >= twentyFourHours.minusSeconds(5).seconds) { "Expiry is too short" }
        assertTrue(diff.seconds <= twentyFourHours.plusSeconds(5).seconds) { "Expiry is too long" }
    }
}
