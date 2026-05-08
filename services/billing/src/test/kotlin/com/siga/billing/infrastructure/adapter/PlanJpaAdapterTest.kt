package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Plan
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.UUID

/**
 * Integration test for [PlanJpaAdapter].
 * Verifies Plan persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class PlanJpaAdapterTest @Autowired constructor(
    private val adapter: PlanJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val plan = Plan(
            id = UUID.randomUUID(),
            name = "Premium Plan",
            description = "Premium subscription",
            storeLimit = 5,
            userLimit = 10,
            productLimit = 100,
            monthlyPrice = BigDecimal("49.99"),
            yearlyPrice = BigDecimal("499.99"),
            displayOrder = 1,
            isActive = true
        )

        val saved = adapter.save(plan)
        assertEquals(plan.id, saved.id)
        assertEquals("Premium Plan", saved.name)
        assertEquals(BigDecimal("49.99"), saved.monthlyPrice)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals("Premium Plan", found?.name)
    }

    @Test
    fun `findById returns null when plan does not exist`() {
        val found = adapter.findById(UUID.randomUUID())
        assertNull(found)
    }

    @Test
    fun `findByName returns plan by name`() {
        val plan = Plan(
            id = UUID.randomUUID(),
            name = "Unique-Name-Plan",
            description = null,
            storeLimit = 1,
            userLimit = 3,
            productLimit = null,
            monthlyPrice = BigDecimal("9.99"),
            yearlyPrice = null,
            displayOrder = 0,
            isActive = true
        )
        adapter.save(plan)

        val found = adapter.findByName("Unique-Name-Plan")
        assertNotNull(found)
        assertEquals(plan.id, found?.id)
    }

    @Test
    fun `findByName returns null when name does not exist`() {
        val found = adapter.findByName("NONEXISTENT-PLAN")
        assertNull(found)
    }

    @Test
    fun `findByIsActiveTrue returns only active plans`() {
        val activePlan = Plan(
            id = UUID.randomUUID(),
            name = "Active Plan",
            description = null,
            storeLimit = 1,
            userLimit = 3,
            productLimit = null,
            monthlyPrice = BigDecimal("10.00"),
            yearlyPrice = null,
            displayOrder = 0,
            isActive = true
        )
        val inactivePlan = Plan(
            id = UUID.randomUUID(),
            name = "Inactive Plan",
            description = null,
            storeLimit = 1,
            userLimit = 3,
            productLimit = null,
            monthlyPrice = BigDecimal("5.00"),
            yearlyPrice = null,
            displayOrder = 0,
            isActive = false
        )
        adapter.save(activePlan)
        adapter.save(inactivePlan)

        val activePlans = adapter.findByIsActiveTrue()
        assertTrue(activePlans.any { it.id == activePlan.id })
        assertFalse(activePlans.any { it.id == inactivePlan.id })
    }

    @Test
    fun `save plan with null optional fields`() {
        val plan = Plan(
            id = UUID.randomUUID(),
            name = "Minimal Plan",
            description = null,
            storeLimit = 1,
            userLimit = 1,
            productLimit = null,
            monthlyPrice = BigDecimal("0.00"),
            yearlyPrice = null,
            displayOrder = 0,
            isActive = true
        )
        val saved = adapter.save(plan)
        assertNull(saved.description)
        assertNull(saved.productLimit)
        assertNull(saved.yearlyPrice)
    }

    @Test
    fun `update plan by saving with same id`() {
        val plan = Plan(
            id = UUID.randomUUID(),
            name = "Original Plan",
            description = "Original description",
            storeLimit = 1,
            userLimit = 3,
            productLimit = 50,
            monthlyPrice = BigDecimal("10.00"),
            yearlyPrice = BigDecimal("100.00"),
            displayOrder = 0,
            isActive = true
        )
        val saved = adapter.save(plan)

        val updated = saved.copy(
            name = "Updated Plan",
            monthlyPrice = BigDecimal("19.99"),
            isActive = false
        )
        adapter.save(updated)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals("Updated Plan", found?.name)
        assertEquals(BigDecimal("19.99"), found?.monthlyPrice)
        assertFalse(found?.isActive!!)
    }
}
