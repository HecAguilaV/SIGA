package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class PlanTest {

    @Test
    fun `create plan with all fields`() {
        val id = UUID.randomUUID()

        val plan = Plan(
            id = id,
            name = "Premium",
            description = "Plan premium con todas las funcionalidades",
            storeLimit = 5,
            userLimit = 10,
            productLimit = 1000,
            monthlyPrice = BigDecimal("29.99"),
            yearlyPrice = BigDecimal("299.99"),
            displayOrder = 1,
            isActive = true
        )

        assertEquals(id, plan.id)
        assertEquals("Premium", plan.name)
        assertEquals("Plan premium con todas las funcionalidades", plan.description)
        assertEquals(5, plan.storeLimit)
        assertEquals(10, plan.userLimit)
        assertEquals(1000, plan.productLimit)
        assertEquals(BigDecimal("29.99"), plan.monthlyPrice)
        assertEquals(BigDecimal("299.99"), plan.yearlyPrice)
        assertEquals(1, plan.displayOrder)
        assertTrue(plan.isActive)
    }

    @Test
    fun `create plan with nullable fields`() {
        val plan = Plan(
            id = UUID.randomUUID(),
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

        assertNull(plan.description)
        assertNull(plan.productLimit)
        assertNull(plan.yearlyPrice)
        assertFalse(plan.isActive)
    }

    @Test
    fun `plan data class equality`() {
        val id = UUID.randomUUID()
        val plan1 = Plan(id, "Basic", null, 1, 3, null, BigDecimal("9.99"), null, 0, true)
        val plan2 = plan1.copy()

        assertEquals(plan1, plan2)
        assertEquals(plan1.hashCode(), plan2.hashCode())
    }

    @Test
    fun `plan data class inequality on different id`() {
        val plan1 = Plan(UUID.randomUUID(), "Basic", null, 1, 3, null, BigDecimal("9.99"), null, 0, true)
        val plan2 = Plan(UUID.randomUUID(), "Basic", null, 1, 3, null, BigDecimal("9.99"), null, 0, true)

        assertNotEquals(plan1, plan2)
    }

    @Test
    fun `plan data class toString contains fields`() {
        val plan = Plan(UUID.randomUUID(), "Enterprise", "Big plan", 10, 50, null, BigDecimal("99.99"), BigDecimal("999.99"), 3, true)
        val toString = plan.toString()

        assertTrue(toString.contains("Enterprise"))
        assertTrue(toString.contains("99.99"))
    }
}
