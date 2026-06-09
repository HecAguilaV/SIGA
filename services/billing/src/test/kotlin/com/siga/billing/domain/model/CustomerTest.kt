package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class CustomerTest {

    @Test
    fun `create customer with all fields`() {
        val id = UUID.randomUUID()
        val planId = UUID.randomUUID()
        val now = Instant.now()
        val trialStart = now
        val trialEnd = now.plusSeconds(86400 * 14)

        val customer = Customer(
            id = id,
            email = "user@example.com",
            name = "John",
            lastName = "Doe",
            taxId = "12.345.678-9",
            phoneNumber = "+56912345678",
            companyName = "Acme Corp",
            isActive = true,
            isOnTrial = true,
            trialStartAt = trialStart,
            trialEndAt = trialEnd,
            role = "admin",
            planId = planId,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(id, customer.id)
        assertEquals("user@example.com", customer.email)
        assertEquals("John", customer.name)
        assertEquals("Doe", customer.lastName)
        assertEquals("12.345.678-9", customer.taxId)
        assertEquals("+56912345678", customer.phoneNumber)
        assertEquals("Acme Corp", customer.companyName)
        assertTrue(customer.isActive)
        assertTrue(customer.isOnTrial)
        assertEquals(trialStart, customer.trialStartAt)
        assertEquals(trialEnd, customer.trialEndAt)
        assertEquals("admin", customer.role)
        assertEquals(planId, customer.planId)
        assertEquals(now, customer.createdAt)
        assertEquals(now, customer.updatedAt)
    }

    @Test
    fun `create customer with null optional fields`() {
        val now = Instant.now()

        val customer = Customer(
            id = UUID.randomUUID(),
            email = "minimal@example.com",
            name = "Minimal",
            lastName = null,
            taxId = null,
            phoneNumber = null,
            companyName = null,
            isActive = true,
            isOnTrial = false,
            trialStartAt = null,
            trialEndAt = null,
            role = "customer",
            planId = null,
            createdAt = now,
            updatedAt = now
        )

        assertNull(customer.lastName)
        assertNull(customer.taxId)
        assertNull(customer.phoneNumber)
        assertNull(customer.companyName)
        assertNull(customer.trialStartAt)
        assertNull(customer.trialEndAt)
        assertNull(customer.planId)
        assertFalse(customer.isOnTrial)
    }

    @Test
    fun `create inactive customer`() {
        val customer = Customer(
            id = UUID.randomUUID(),
            email = "inactive@example.com",
            name = "Inactive",
            lastName = null,
            taxId = null,
            phoneNumber = null,
            companyName = null,
            isActive = false,
            isOnTrial = false,
            trialStartAt = null,
            trialEndAt = null,
            role = "customer",
            planId = null,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertFalse(customer.isActive)
    }

    @Test
    fun `customer data class equality`() {
        val id = UUID.randomUUID()
        val now = Instant.now()
        val customer1 = Customer(
            id = id,
            email = "same@example.com",
            name = "Same",
            lastName = null,
            taxId = null,
            phoneNumber = null,
            companyName = null,
            isActive = true,
            isOnTrial = false,
            trialStartAt = null,
            trialEndAt = null,
            role = "customer",
            planId = null,
            createdAt = now,
            updatedAt = now
        )
        val customer2 = customer1.copy()

        assertEquals(customer1, customer2)
        assertEquals(customer1.hashCode(), customer2.hashCode())
    }

    @Test
    fun `customer data class inequality on different id`() {
        val now = Instant.now()
        val customer1 = Customer(UUID.randomUUID(), "a@b.com", "A", null, null, null, null, true, false, null, null, "customer", null, now, now)
        val customer2 = Customer(UUID.randomUUID(), "a@b.com", "A", null, null, null, null, true, false, null, null, "customer", null, now, now)

        assertNotEquals(customer1, customer2)
    }

    @Test
    fun `customer with trial dates`() {
        val now = Instant.now()
        val trialEnd = now.plusSeconds(86400 * 30) // 30 day trial

        val customer = Customer(
            id = UUID.randomUUID(),
            email = "trial@example.com",
            name = "Trial",
            lastName = "User",
            taxId = null,
            phoneNumber = null,
            companyName = "Startup Ltd",
            isActive = true,
            isOnTrial = true,
            trialStartAt = now,
            trialEndAt = trialEnd,
            role = "customer",
            planId = UUID.randomUUID(),
            createdAt = now,
            updatedAt = now
        )

        assertTrue(customer.isOnTrial)
        assertNotNull(customer.trialStartAt)
        assertNotNull(customer.trialEndAt)
        assertTrue(customer.trialEndAt!!.isAfter(customer.trialStartAt))
    }
}
