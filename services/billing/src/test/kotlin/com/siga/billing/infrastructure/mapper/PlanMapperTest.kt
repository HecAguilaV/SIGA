package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.Plan
import com.siga.billing.entity.PlanEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class PlanMapperTest {

    @Test
    fun `toDomain maps entity to domain model`() {
        val id = UUID.randomUUID()

        val entity = PlanEntity(
            id = id,
            name = "Premium",
            description = "Premium plan with all features",
            storeLimit = 5,
            userLimit = 10,
            productLimit = 1000,
            monthlyPrice = BigDecimal("29.99"),
            yearlyPrice = BigDecimal("299.99"),
            displayOrder = 1,
            isActive = true
        )

        val domain = PlanMapper.toDomain(entity)

        assertEquals(id, domain.id)
        assertEquals("Premium", domain.name)
        assertEquals("Premium plan with all features", domain.description)
        assertEquals(5, domain.storeLimit)
        assertEquals(10, domain.userLimit)
        assertEquals(1000, domain.productLimit)
        assertEquals(BigDecimal("29.99"), domain.monthlyPrice)
        assertEquals(BigDecimal("299.99"), domain.yearlyPrice)
        assertEquals(1, domain.displayOrder)
        assertTrue(domain.isActive)
    }

    @Test
    fun `toDomain maps entity with null optional fields`() {
        val id = UUID.randomUUID()

        val entity = PlanEntity(
            id = id,
            name = "Free",
            description = null,
            storeLimit = 1,
            userLimit = 1,
            productLimit = null,
            monthlyPrice = BigDecimal.ZERO,
            yearlyPrice = null,
            displayOrder = 0,
            isActive = false
        )

        val domain = PlanMapper.toDomain(entity)

        assertNull(domain.description)
        assertNull(domain.productLimit)
        assertNull(domain.yearlyPrice)
        assertFalse(domain.isActive)
    }

    @Test
    fun `toDomain throws when entity id is null`() {
        val entity = PlanEntity(
            id = null,
            name = "No ID",
            monthlyPrice = BigDecimal.TEN
        )

        assertThrows(IllegalStateException::class.java) {
            PlanMapper.toDomain(entity)
        }
    }

    @Test
    fun `toEntity maps domain model to entity`() {
        val id = UUID.randomUUID()

        val domain = Plan(
            id = id,
            name = "Basic",
            description = "Basic plan",
            storeLimit = 2,
            userLimit = 5,
            productLimit = 500,
            monthlyPrice = BigDecimal("9.99"),
            yearlyPrice = BigDecimal("99.99"),
            displayOrder = 2,
            isActive = true
        )

        val entity = PlanMapper.toEntity(domain)

        assertEquals(id, entity.id)
        assertEquals("Basic", entity.name)
        assertEquals("Basic plan", entity.description)
        assertEquals(2, entity.storeLimit)
        assertEquals(5, entity.userLimit)
        assertEquals(500, entity.productLimit)
        assertEquals(BigDecimal("9.99"), entity.monthlyPrice)
        assertEquals(BigDecimal("99.99"), entity.yearlyPrice)
        assertEquals(2, entity.displayOrder)
        assertTrue(entity.isActive)
    }

    @Test
    fun `toEntity maps domain with null optional fields`() {
        val domain = Plan(
            id = UUID.randomUUID(),
            name = "Minimal",
            description = null,
            storeLimit = 1,
            userLimit = 1,
            productLimit = null,
            monthlyPrice = BigDecimal.ZERO,
            yearlyPrice = null,
            displayOrder = 0,
            isActive = false
        )

        val entity = PlanMapper.toEntity(domain)

        assertNull(entity.description)
        assertNull(entity.productLimit)
        assertNull(entity.yearlyPrice)
        assertFalse(entity.isActive)
    }

    @Test
    fun `roundtrip domain to entity to domain`() {
        val original = Plan(
            id = UUID.randomUUID(),
            name = "Enterprise",
            description = "Enterprise plan",
            storeLimit = 50,
            userLimit = 999,
            productLimit = null,
            monthlyPrice = BigDecimal("99.99"),
            yearlyPrice = BigDecimal("999.99"),
            displayOrder = 5,
            isActive = true
        )

        val entity = PlanMapper.toEntity(original)
        val domain = PlanMapper.toDomain(entity)

        assertEquals(original.id, domain.id)
        assertEquals(original.name, domain.name)
        assertEquals(original.description, domain.description)
        assertEquals(original.storeLimit, domain.storeLimit)
        assertEquals(original.userLimit, domain.userLimit)
        assertEquals(original.productLimit, domain.productLimit)
        assertEquals(original.monthlyPrice, domain.monthlyPrice)
        assertEquals(original.yearlyPrice, domain.yearlyPrice)
        assertEquals(original.displayOrder, domain.displayOrder)
        assertEquals(original.isActive, domain.isActive)
    }
}
