package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.Customer
import com.siga.billing.entity.CustomerEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class CustomerMapperTest {

    @Test
    fun `toDomain maps entity to domain model`() {
        val id = UUID.randomUUID()
        val planId = UUID.randomUUID()
        val now = Instant.now()

        val entity = CustomerEntity(
            id = id,
            email = "test@example.com",
            passwordHash = "some-hash",
            name = "John",
            lastName = "Doe",
            taxId = "12.345.678-9",
            phone = "+56912345678",
            companyName = "Acme Corp",
            isActive = true,
            isOnTrial = true,
            trialStartAt = now,
            trialEndAt = now.plusSeconds(86400 * 14),
            role = "admin",
            planId = planId,
            createdAt = now,
            updatedAt = now
        )

        val domain = CustomerMapper.toDomain(entity)

        assertEquals(id, domain.id)
        assertEquals("test@example.com", domain.email)
        assertEquals("John", domain.name)
        assertEquals("Doe", domain.lastName)
        assertEquals("12.345.678-9", domain.taxId)
        assertEquals("+56912345678", domain.phoneNumber)
        assertEquals("Acme Corp", domain.companyName)
        assertTrue(domain.isActive)
        assertTrue(domain.isOnTrial)
        assertEquals(now, domain.trialStartAt)
        assertEquals(now.plusSeconds(86400 * 14), domain.trialEndAt)
        assertEquals("admin", domain.role)
        assertEquals(planId, domain.planId)
        assertEquals(now, domain.createdAt)
        assertEquals(now, domain.updatedAt)
    }

    @Test
    fun `toDomain maps entity with null optional fields`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val entity = CustomerEntity(
            id = id,
            email = "minimal@example.com",
            passwordHash = "hash",
            name = "Minimal",
            lastName = null,
            taxId = null,
            phone = null,
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

        val domain = CustomerMapper.toDomain(entity)

        assertNull(domain.lastName)
        assertNull(domain.taxId)
        assertNull(domain.phoneNumber)
        assertNull(domain.companyName)
        assertNull(domain.trialStartAt)
        assertNull(domain.trialEndAt)
        assertNull(domain.planId)
        assertFalse(domain.isOnTrial)
    }

    @Test
    fun `toDomain throws when entity id is null`() {
        val entity = CustomerEntity(
            id = null,
            email = "noid@example.com",
            passwordHash = "hash",
            name = "No ID"
        )

        assertThrows(IllegalStateException::class.java) {
            CustomerMapper.toDomain(entity)
        }
    }

    @Test
    fun `toEntity maps domain model to entity`() {
        val id = UUID.randomUUID()
        val planId = UUID.randomUUID()
        val now = Instant.now()

        val domain = Customer(
            id = id,
            email = "test@example.com",
            name = "John",
            lastName = "Doe",
            taxId = "12.345.678-9",
            phoneNumber = "+56912345678",
            companyName = "Acme Corp",
            isActive = true,
            isOnTrial = true,
            trialStartAt = now,
            trialEndAt = now.plusSeconds(86400 * 14),
            role = "admin",
            planId = planId,
            createdAt = now,
            updatedAt = now
        )

        val entity = CustomerMapper.toEntity(domain)

        assertEquals(id, entity.id)
        assertEquals("test@example.com", entity.email)
        assertEquals("", entity.passwordHash) // password not mapped from domain
        assertEquals("John", entity.name)
        assertEquals("Doe", entity.lastName)
        assertEquals("12.345.678-9", entity.taxId)
        assertEquals("+56912345678", entity.phone)
        assertEquals("Acme Corp", entity.companyName)
        assertTrue(entity.isActive)
        assertTrue(entity.isOnTrial)
        assertEquals(now, entity.trialStartAt)
        assertEquals(now.plusSeconds(86400 * 14), entity.trialEndAt)
        assertEquals("admin", entity.role)
        assertEquals(planId, entity.planId)
        assertEquals(now, entity.createdAt)
        assertEquals(now, entity.updatedAt)
    }

    @Test
    fun `toEntity maps domain with null optional fields`() {
        val now = Instant.now()

        val domain = Customer(
            id = UUID.randomUUID(),
            email = "minimal@example.com",
            name = "Minimal",
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
            createdAt = now,
            updatedAt = now
        )

        val entity = CustomerMapper.toEntity(domain)

        assertNull(entity.lastName)
        assertNull(entity.taxId)
        assertNull(entity.phone)
        assertNull(entity.companyName)
        assertNull(entity.trialStartAt)
        assertNull(entity.trialEndAt)
        assertNull(entity.planId)
        assertFalse(entity.isActive)
    }

    @Test
    fun `roundtrip domain to entity to domain`() {
        val now = Instant.now()
        val original = Customer(
            id = UUID.randomUUID(),
            email = "roundtrip@example.com",
            name = "Round",
            lastName = "Trip",
            taxId = "1-9",
            phoneNumber = "+56900000000",
            companyName = "RT Corp",
            isActive = true,
            isOnTrial = false,
            trialStartAt = null,
            trialEndAt = null,
            role = "customer",
            planId = UUID.randomUUID(),
            createdAt = now,
            updatedAt = now
        )

        val entity = CustomerMapper.toEntity(original)
        val domain = CustomerMapper.toDomain(entity)

        assertEquals(original.id, domain.id)
        assertEquals(original.email, domain.email)
        assertEquals(original.name, domain.name)
        assertEquals(original.lastName, domain.lastName)
        assertEquals(original.taxId, domain.taxId)
        assertEquals(original.phoneNumber, domain.phoneNumber)
        assertEquals(original.companyName, domain.companyName)
        assertEquals(original.isActive, domain.isActive)
        assertEquals(original.isOnTrial, domain.isOnTrial)
        assertEquals(original.role, domain.role)
        assertEquals(original.planId, domain.planId)
    }
}
