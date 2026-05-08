package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.Customer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

/**
 * Integration test for [CustomerJpaAdapter].
 * Verifies Customer persistence through the hexagonal port with H2.
 * Uses Int ID (IDENTITY strategy).
 */
@SpringBootTest
@ActiveProfiles("test")
class CustomerJpaAdapterTest @Autowired constructor(
    private val adapter: CustomerJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val uniqueEmail = "customer_savefind_${UUID.randomUUID()}@test.com"
        val customer = Customer(
            id = null, // IDENTITY - will be generated
            email = uniqueEmail,
            passwordHash = "hash123",
            name = "Test Customer",
            lastName = "One",
            taxId = "123-456",
            phone = "+123456789",
            companyName = "Test Corp",
            isActive = true
        )

        val saved = adapter.save(customer)
        assertNotNull(saved.id)
        assertEquals(uniqueEmail, saved.email)
        assertEquals("Test Customer", saved.name)

        val found = adapter.findById(saved.id!!)
        assertNotNull(found)
        assertEquals(uniqueEmail, found?.email)
        assertEquals("Test Customer", found?.name)
    }

    @Test
    fun `findById returns null when customer does not exist`() {
        val found = adapter.findById(999999)
        assertNull(found)
    }

    @Test
    fun `findByEmail finds customer by email`() {
        val uniqueEmail = "customer_email_${UUID.randomUUID()}@test.com"
        val customer = Customer(
            id = null,
            email = uniqueEmail,
            passwordHash = "hash123",
            name = "Email Customer",
            isActive = true
        )
        val saved = adapter.save(customer)

        val found = adapter.findByEmail(uniqueEmail)
        assertNotNull(found)
        assertEquals(saved.id, found?.id)
        assertEquals(uniqueEmail, found?.email)
    }

    @Test
    fun `findByEmail returns null when email does not exist`() {
        val found = adapter.findByEmail("nonexistent_customer_${UUID.randomUUID()}@test.com")
        assertNull(found)
    }

    @Test
    fun `existsByEmail works correctly`() {
        val uniqueEmail = "exists_check_${UUID.randomUUID()}@test.com"
        
        assertFalse(adapter.existsByEmail(uniqueEmail))

        val customer = Customer(
            id = null,
            email = uniqueEmail,
            passwordHash = "hash123",
            name = "Exists Test",
            isActive = true
        )
        adapter.save(customer)

        assertTrue(adapter.existsByEmail(uniqueEmail))
    }

    @Test
    fun `findAll returns all customers`() {
        val initialCount = adapter.findAll().size

        val uniqueEmail1 = "cust1_${UUID.randomUUID()}@test.com"
        val uniqueEmail2 = "cust2_${UUID.randomUUID()}@test.com"
        
        val customer1 = Customer(
            id = null,
            email = uniqueEmail1,
            passwordHash = "hash1",
            name = "Customer One",
            isActive = true
        )
        val customer2 = Customer(
            id = null,
            email = uniqueEmail2,
            passwordHash = "hash2",
            name = "Customer Two",
            isActive = true
        )
        adapter.save(customer1)
        adapter.save(customer2)

        val allCustomers = adapter.findAll()
        assertTrue(allCustomers.size >= initialCount + 2)
        assertTrue(allCustomers.any { it.email == uniqueEmail1 })
        assertTrue(allCustomers.any { it.email == uniqueEmail2 })
    }

    @Test
    fun `save with null optional fields`() {
        val uniqueEmail = "minimal_${UUID.randomUUID()}@test.com"
        val customer = Customer(
            id = null,
            email = uniqueEmail,
            passwordHash = "hash123",
            name = "Minimal Customer",
            lastName = null,
            taxId = null,
            phone = null,
            companyName = null,
            isActive = true
        )

        val saved = adapter.save(customer)
        assertNotNull(saved.id)
        assertNull(saved.lastName)
        assertNull(saved.taxId)
        assertNull(saved.phone)
        assertNull(saved.companyName)
        assertEquals("minimal customer", saved.name.lowercase())
    }

    @Test
    fun `update customer by saving with same id`() {
        val uniqueEmail = "original_${UUID.randomUUID()}@test.com"
        val customer = Customer(
            id = null,
            email = uniqueEmail,
            passwordHash = "original_hash",
            name = "Original Name",
            lastName = "OriginalLastName",
            isActive = true
        )
        val saved = adapter.save(customer)
        val savedId = saved.id!!

        val updated = saved.copy(
            name = "Updated Name",
            lastName = "UpdatedLastName",
            isActive = false
        )
        adapter.save(updated)

        val found = adapter.findById(savedId)
        assertNotNull(found)
        assertEquals("Updated Name", found?.name)
        assertEquals("UpdatedLastName", found?.lastName)
        assertFalse(found?.isActive!!)
    }
}
