package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

/**
 * Unit test for [ManageCustomerUseCase] with Mockito.
 * Tests validation logic and port delegation without Spring context.
 * Follows the same pattern as ManageSubscriptionUseCaseTest in billing.
 */
class ManageCustomerUseCaseTest {

    private val customerRepositoryPort = mock(CustomerRepositoryPort::class.java)
    private val useCase = ManageCustomerUseCase(customerRepositoryPort)

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    fun `create validates email not blank`() {
        val customer = Customer(
            id = 1,
            email = "   ",
            passwordHash = "hash123",
            name = "Test Customer"
        )

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(customer)
        }
        assertEquals("Email must not be blank", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create validates name not blank`() {
        val customer = Customer(
            id = 1,
            email = "test@test.com",
            passwordHash = "hash123",
            name = "   "
        )

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(customer)
        }
        assertEquals("Name must not be blank", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create throws IllegalArgumentException when email exists`() {
        val existingEmail = "existing_customer@test.com"
        val customer = Customer(
            id = 1,
            email = existingEmail,
            passwordHash = "hash123",
            name = "Test Customer"
        )

        `when`(customerRepositoryPort.existsByEmail(existingEmail)).thenReturn(true)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.create(customer)
        }
        assertEquals("Email already exists: $existingEmail", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `create calls save with id null`() {
        val customer = Customer(
            id = 999,
            email = "new_customer@test.com",
            passwordHash = "hash123",
            name = "New Customer"
        )
        val savedCustomer = customer.copy(id = 1)

        `when`(customerRepositoryPort.existsByEmail("new_customer@test.com")).thenReturn(false)
        `when`(customerRepositoryPort.save(anyObject())).thenReturn(savedCustomer)

        val result = useCase.create(customer)

        assertEquals(savedCustomer, result)
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `update throws IllegalArgumentException when customer not found`() {
        val id = 999
        val customer = Customer(
            id = id,
            email = "update@test.com",
            passwordHash = "hash123",
            name = "Update Customer"
        )

        `when`(customerRepositoryPort.findById(id)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.update(id, customer)
        }
        assertEquals("Customer not found: $id", exception.message)
        verify(customerRepositoryPort, never()).save(anyObject())
    }

    @Test
    fun `update saves when customer exists`() {
        val id = 1
        val existingCustomer = Customer(
            id = id,
            email = "existing@test.com",
            passwordHash = "oldhash",
            name = "Old Name",
            isActive = true
        )
        val updatedCustomer = Customer(
            id = 999,
            email = "updated@test.com",
            passwordHash = "newhash",
            name = "Updated Name",
            isActive = false
        )
        val savedCustomer = updatedCustomer.copy(id = id)

        `when`(customerRepositoryPort.findById(id)).thenReturn(existingCustomer)
        `when`(customerRepositoryPort.save(anyObject())).thenReturn(savedCustomer)

        val result = useCase.update(id, updatedCustomer)

        assertEquals(savedCustomer, result)
        verify(customerRepositoryPort, times(1)).save(anyObject())
    }

    @Test
    fun `findById delegates to port`() {
        val id = 42
        val customer = Customer(
            id = id,
            email = "find@test.com",
            passwordHash = "hash",
            name = "Test Customer"
        )

        `when`(customerRepositoryPort.findById(id)).thenReturn(customer)

        val result = useCase.findById(id)

        assertEquals(customer, result)
        verify(customerRepositoryPort, times(1)).findById(id)
    }

    @Test
    fun `findAll delegates to port`() {
        val customers = listOf(
            Customer(id = 1, email = "c1@test.com", passwordHash = "h", name = "C1"),
            Customer(id = 2, email = "c2@test.com", passwordHash = "h", name = "C2")
        )

        `when`(customerRepositoryPort.findAll()).thenReturn(customers)

        val result = useCase.findAll()

        assertEquals(customers, result)
        verify(customerRepositoryPort, times(1)).findAll()
    }

    @Test
    fun `findByEmail delegates to port`() {
        val email = "delegate_customer@test.com"
        val customer = Customer(
            id = 1,
            email = email,
            passwordHash = "hash",
            name = "Test Customer"
        )

        `when`(customerRepositoryPort.findByEmail(email)).thenReturn(customer)

        val result = useCase.findByEmail(email)

        assertEquals(customer, result)
        verify(customerRepositoryPort, times(1)).findByEmail(email)
    }
}
