package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class CustomerEntityTest {

    @Test
    fun `create customer entity with all fields`() {
        val id = UUID.randomUUID()

        val entity = CustomerEntity(
            id = id,
            email = "test@example.com",
            passwordHash = "hashed-password",
            name = "John",
            lastName = "Doe",
            taxId = "12.345.678-9",
            phone = "+56912345678",
            companyName = "Acme Corp",
            isActive = true,
            isOnTrial = true,
            trialStartAt = Instant.now(),
            trialEndAt = Instant.now().plusSeconds(86400 * 14),
            role = "admin",
            planId = UUID.randomUUID(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals(id, entity.id)
        assertEquals("test@example.com", entity.email)
        assertEquals("hashed-password", entity.passwordHash)
        assertEquals("John", entity.name)
        assertEquals("Doe", entity.lastName)
        assertEquals("12.345.678-9", entity.taxId)
        assertEquals("+56912345678", entity.phone)
        assertEquals("Acme Corp", entity.companyName)
        assertTrue(entity.isActive)
        assertTrue(entity.isOnTrial)
        assertEquals("admin", entity.role)
    }

    @Test
    fun `create customer entity with defaults`() {
        val entity = CustomerEntity(
            email = "default@example.com",
            passwordHash = "hash",
            name = "Default"
        )

        assertNull(entity.id)
        assertEquals("default@example.com", entity.email)
        assertTrue(entity.isActive)
        assertFalse(entity.isOnTrial)
        assertEquals("customer", entity.role)
        assertNull(entity.lastName)
        assertNull(entity.taxId)
        assertNull(entity.phone)
        assertNull(entity.companyName)
        assertNull(entity.trialStartAt)
        assertNull(entity.trialEndAt)
        assertNull(entity.planId)
        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `customer entity equals by id`() {
        val id = UUID.randomUUID()
        val entity1 = CustomerEntity(id = id, email = "a@b.com", passwordHash = "h1", name = "A")
        val entity2 = CustomerEntity(id = id, email = "a@b.com", passwordHash = "h1", name = "A")

        assertEquals(entity1, entity2)
        assertEquals(entity1.hashCode(), entity2.hashCode())
    }

    @Test
    fun `customer entity inequality on different id`() {
        val entity1 = CustomerEntity(id = UUID.randomUUID(), email = "a@b.com", passwordHash = "h1", name = "A")
        val entity2 = CustomerEntity(id = UUID.randomUUID(), email = "a@b.com", passwordHash = "h1", name = "A")

        assertNotEquals(entity1, entity2)
    }

    @Test
    fun `customer entity toString`() {
        val entity = CustomerEntity(id = UUID.randomUUID(), email = "test@test.com", passwordHash = "hash", name = "Test")
        val toString = entity.toString()

        assertTrue(toString.contains("CustomerEntity"))
        assertTrue(toString.contains("test@test.com"))
    }

    @Test
    fun `customer entity onPrePersist preserves existing createdAt`() {
        val now = Instant.now()
        val entity = CustomerEntity(
            email = "persist@example.com",
            passwordHash = "hash",
            name = "Persist",
            createdAt = now,
            updatedAt = now
        )

        entity.onPrePersist()

        assertEquals(now, entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `customer entity onPreUpdate updates updatedAt`() {
        val entity = CustomerEntity(
            email = "update@example.com",
            passwordHash = "hash",
            name = "Update"
        )
        val originalUpdatedAt = entity.updatedAt

        entity.onPreUpdate()

        assertTrue(entity.updatedAt >= originalUpdatedAt)
    }
}
