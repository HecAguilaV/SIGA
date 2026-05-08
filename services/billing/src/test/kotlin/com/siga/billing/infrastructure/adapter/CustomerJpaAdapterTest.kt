package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Customer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [CustomerJpaAdapter].
 * Verifies Customer persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class CustomerJpaAdapterTest @Autowired constructor(
    private val adapter: CustomerJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val customer = Customer(
            id = UUID.randomUUID(),
            email = "test@example.com",
            name = "John",
            lastName = "Doe",
            taxId = "12.345.678-9",
            phoneNumber = "+56912345678",
            companyName = "Test Corp",
            isActive = true,
            isOnTrial = true,
            trialStartAt = Instant.now(),
            trialEndAt = Instant.now().plusSeconds(86400 * 14),
            role = "customer",
            planId = UUID.randomUUID(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val saved = adapter.save(customer)
        assertEquals(customer.id, saved.id)
        assertEquals("test@example.com", saved.email)
        assertEquals("John", saved.name)
        assertTrue(saved.isActive)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals("test@example.com", found?.email)
    }

    @Test
    fun `findById returns null when customer does not exist`() {
        val found = adapter.findById(UUID.randomUUID())
        assertNull(found)
    }

    @Test
    fun `findByEmail returns customer by email`() {
        val email = "unique-email@test.com"
        val customer = Customer(
            id = UUID.randomUUID(), email = email,
            name = "Email Test", lastName = null, taxId = null,
            phoneNumber = null, companyName = null,
            isActive = true, isOnTrial = false,
            trialStartAt = null, trialEndAt = null,
            role = "customer", planId = null,
            createdAt = Instant.now(), updatedAt = Instant.now()
        )
        adapter.save(customer)

        val found = adapter.findByEmail(email)
        assertNotNull(found)
        assertEquals(customer.id, found?.id)
    }

    @Test
    fun `findByEmail returns null when email does not exist`() {
        val found = adapter.findByEmail("nonexistent@test.com")
        assertNull(found)
    }

    @Test
    fun `save customer with null optional fields`() {
        val customer = Customer(
            id = UUID.randomUUID(), email = "minimal@test.com",
            name = "Minimal", lastName = null, taxId = null,
            phoneNumber = null, companyName = null,
            isActive = true, isOnTrial = false,
            trialStartAt = null, trialEndAt = null,
            role = "customer", planId = null,
            createdAt = Instant.now(), updatedAt = Instant.now()
        )
        val saved = adapter.save(customer)
        assertNull(saved.lastName)
        assertNull(saved.taxId)
        assertNull(saved.phoneNumber)
        assertNull(saved.companyName)
        assertNull(saved.trialStartAt)
        assertNull(saved.planId)
    }

    @Test
    fun `update customer by saving with same id`() {
        val customer = Customer(
            id = UUID.randomUUID(), email = "update@test.com",
            name = "Original Name", lastName = null, taxId = null,
            phoneNumber = null, companyName = "Original Corp",
            isActive = true, isOnTrial = false,
            trialStartAt = null, trialEndAt = null,
            role = "customer", planId = null,
            createdAt = Instant.now(), updatedAt = Instant.now()
        )
        val saved = adapter.save(customer)

        val updated = saved.copy(
            name = "Updated Name",
            companyName = "Updated Corp",
            isActive = false
        )
        adapter.save(updated)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals("Updated Name", found?.name)
        assertEquals("Updated Corp", found?.companyName)
        assertFalse(found?.isActive!!)
    }
}
