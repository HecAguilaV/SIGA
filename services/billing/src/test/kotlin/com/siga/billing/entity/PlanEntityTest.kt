package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class PlanEntityTest {

    @Test
    fun `create plan entity with all fields`() {
        val id = UUID.randomUUID()

        val entity = PlanEntity(
            id = id,
            name = "Premium",
            description = "Plan premium description",
            storeLimit = 5,
            userLimit = 10,
            productLimit = 1000,
            monthlyPrice = BigDecimal("29.99"),
            yearlyPrice = BigDecimal("299.99"),
            displayOrder = 1,
            isActive = true
        )

        assertEquals(id, entity.id)
        assertEquals("Premium", entity.name)
        assertEquals("Plan premium description", entity.description)
        assertEquals(5, entity.storeLimit)
        assertEquals(10, entity.userLimit)
        assertEquals(1000, entity.productLimit)
        assertEquals(BigDecimal("29.99"), entity.monthlyPrice)
        assertEquals(BigDecimal("299.99"), entity.yearlyPrice)
        assertEquals(1, entity.displayOrder)
        assertTrue(entity.isActive)
    }

    @Test
    fun `create plan entity with defaults`() {
        val entity = PlanEntity(
            name = "Free",
            monthlyPrice = BigDecimal.ZERO
        )

        assertNull(entity.id)
        assertNull(entity.description)
        assertNull(entity.productLimit)
        assertNull(entity.yearlyPrice)
        assertEquals(1, entity.storeLimit)
        assertEquals(3, entity.userLimit)
        assertEquals(0, entity.displayOrder)
        assertTrue(entity.isActive)
    }

    @Test
    fun `create inactive plan entity`() {
        val entity = PlanEntity(
            name = "Archived",
            monthlyPrice = BigDecimal.TEN,
            isActive = false
        )

        assertFalse(entity.isActive)
    }

    @Test
    fun `plan entity equals by id`() {
        val id = UUID.randomUUID()
        val entity1 = PlanEntity(id = id, name = "Basic", monthlyPrice = BigDecimal("9.99"))
        val entity2 = PlanEntity(id = id, name = "Basic", monthlyPrice = BigDecimal("9.99"))

        assertEquals(entity1, entity2)
        assertEquals(entity1.hashCode(), entity2.hashCode())
    }

    @Test
    fun `plan entity inequality on different id`() {
        val entity1 = PlanEntity(id = UUID.randomUUID(), name = "Basic", monthlyPrice = BigDecimal("9.99"))
        val entity2 = PlanEntity(id = UUID.randomUUID(), name = "Basic", monthlyPrice = BigDecimal("9.99"))

        assertNotEquals(entity1, entity2)
    }

    @Test
    fun `plan entity toString`() {
        val entity = PlanEntity(id = UUID.randomUUID(), name = "Enterprise", monthlyPrice = BigDecimal("99.99"))
        val toString = entity.toString()

        assertTrue(toString.contains("PlanEntity"))
        assertTrue(toString.contains("Enterprise"))
        assertTrue(toString.contains("99.99"))
    }
}
