package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class ShoppingCartTest {

    @Test
    fun `create shopping cart with all fields`() {
        val id = UUID.randomUUID()

        val cart = ShoppingCart(
            id = id,
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.ANNUAL,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals(id, cart.id)
        assertNotNull(cart.customerId)
        assertNotNull(cart.planId)
        assertEquals(BillingPeriod.ANNUAL, cart.period)
        assertNotNull(cart.createdAt)
        assertNotNull(cart.updatedAt)
    }

    @Test
    fun `create shopping cart with defaults`() {
        val cart = ShoppingCart(
            customerId = UUID.randomUUID()
        )

        assertNull(cart.id)
        assertNull(cart.planId)
        assertEquals(BillingPeriod.MONTHLY, cart.period)
        assertNotNull(cart.createdAt)
        assertNotNull(cart.updatedAt)
    }

    @Test
    fun `shopping cart equals by id`() {
        val id = UUID.randomUUID()
        val cart1 = ShoppingCart(id = id, customerId = UUID.randomUUID())
        val cart2 = ShoppingCart(id = id, customerId = UUID.randomUUID())

        assertEquals(cart1, cart2)
        assertEquals(cart1.hashCode(), cart2.hashCode())
    }

    @Test
    fun `shopping cart inequality on different id`() {
        val cart1 = ShoppingCart(id = UUID.randomUUID(), customerId = UUID.randomUUID())
        val cart2 = ShoppingCart(id = UUID.randomUUID(), customerId = UUID.randomUUID())

        assertNotEquals(cart1, cart2)
    }

    @Test
    fun `shopping cart toString`() {
        val cart = ShoppingCart(id = UUID.randomUUID(), customerId = UUID.randomUUID())
        val toString = cart.toString()

        assertTrue(toString.contains("ShoppingCart"))
        assertTrue(toString.contains("MONTHLY"))
    }

    @Test
    fun `shopping cart onPrePersist preserves existing timestamps`() {
        val now = Instant.now()
        val cart = ShoppingCart(
            customerId = UUID.randomUUID(),
            createdAt = now,
            updatedAt = now
        )

        cart.onPrePersist()

        assertEquals(now, cart.createdAt)
        assertNotNull(cart.updatedAt)
    }

    @Test
    fun `shopping cart onPreUpdate updates updatedAt`() {
        val cart = ShoppingCart(customerId = UUID.randomUUID())
        val originalUpdatedAt = cart.updatedAt

        cart.onPreUpdate()

        assertTrue(cart.updatedAt >= originalUpdatedAt)
    }
}
