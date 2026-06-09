package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentEntityTest {

    @Test
    fun `create payment entity with all fields`() {
        val id = UUID.randomUUID()

        val entity = PaymentEntity(
            id = id,
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("49.99"),
            paymentMethod = "VISA",
            status = PaymentStatus.COMPLETED,
            reference = "TXN-001",
            paidAt = Instant.now()
        )

        assertEquals(id, entity.id)
        assertEquals(BigDecimal("49.99"), entity.amount)
        assertEquals("VISA", entity.paymentMethod)
        assertEquals(PaymentStatus.COMPLETED, entity.status)
        assertEquals("TXN-001", entity.reference)
        assertNotNull(entity.paidAt)
    }

    @Test
    fun `create payment entity with defaults`() {
        val entity = PaymentEntity(
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("0.00")
        )

        assertNull(entity.id)
        assertNull(entity.paymentMethod)
        assertNull(entity.reference)
        assertEquals(PaymentStatus.PENDING, entity.status)
        assertNotNull(entity.paidAt)
    }

    @Test
    fun `payment entity equals by id`() {
        val id = UUID.randomUUID()
        val entity1 = PaymentEntity(id = id, subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.TEN)
        val entity2 = PaymentEntity(id = id, subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.TEN)

        assertEquals(entity1, entity2)
        assertEquals(entity1.hashCode(), entity2.hashCode())
    }

    @Test
    fun `payment entity inequality on different id`() {
        val entity1 = PaymentEntity(id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.TEN)
        val entity2 = PaymentEntity(id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal.TEN)

        assertNotEquals(entity1, entity2)
    }

    @Test
    fun `payment entity toString`() {
        val entity = PaymentEntity(id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(), customerId = UUID.randomUUID(), amount = BigDecimal("100"))
        val toString = entity.toString()

        assertTrue(toString.contains("PaymentEntity"))
        assertTrue(toString.contains("100"))
    }

    @Test
    fun `payment entity onPrePersist preserves existing paidAt`() {
        val now = Instant.now()
        val entity = PaymentEntity(
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal.TEN,
            paidAt = now
        )

        entity.onPrePersist()

        assertEquals(now, entity.paidAt)
    }
}
