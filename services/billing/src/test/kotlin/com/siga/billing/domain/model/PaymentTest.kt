package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentTest {

    @Test
    fun `create payment with all fields`() {
        val id = UUID.randomUUID()
        val subscriptionId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val now = Instant.now()

        val payment = Payment(
            id = id,
            subscriptionId = subscriptionId,
            customerId = customerId,
            amount = BigDecimal("49.99"),
            paymentMethod = "VISA",
            status = PaymentStatus.COMPLETED,
            reference = "TXN-001",
            paidAt = now
        )

        assertEquals(id, payment.id)
        assertEquals(subscriptionId, payment.subscriptionId)
        assertEquals(customerId, payment.customerId)
        assertEquals(BigDecimal("49.99"), payment.amount)
        assertEquals("VISA", payment.paymentMethod)
        assertEquals(PaymentStatus.COMPLETED, payment.status)
        assertEquals("TXN-001", payment.reference)
        assertEquals(now, payment.paidAt)
    }

    @Test
    fun `create payment with nullable fields set to null`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val payment = Payment(
            id = id,
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("0.00"),
            paymentMethod = null,
            status = PaymentStatus.PENDING,
            reference = null,
            paidAt = now
        )

        assertNull(payment.paymentMethod)
        assertNull(payment.reference)
        assertEquals(PaymentStatus.PENDING, payment.status)
    }

    @Test
    fun `payment data class equality`() {
        val id = UUID.randomUUID()
        val now = Instant.now()
        val payment1 = Payment(id, UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "MC", PaymentStatus.COMPLETED, "REF", now)
        val payment2 = payment1.copy()

        assertEquals(payment1, payment2)
        assertEquals(payment1.hashCode(), payment2.hashCode())
    }

    @Test
    fun `payment data class inequality on different id`() {
        val now = Instant.now()
        val payment1 = Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PaymentStatus.FAILED, null, now)
        val payment2 = Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PaymentStatus.FAILED, null, now)

        assertNotEquals(payment1, payment2)
    }

    @Test
    fun `payment data class toString contains fields`() {
        val now = Instant.now()
        val payment = Payment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "VISA", PaymentStatus.PENDING, null, now)
        val toString = payment.toString()

        assertTrue(toString.contains("Payment("))
        assertTrue(toString.contains("VISA"))
        assertTrue(toString.contains("PENDING"))
    }
}
