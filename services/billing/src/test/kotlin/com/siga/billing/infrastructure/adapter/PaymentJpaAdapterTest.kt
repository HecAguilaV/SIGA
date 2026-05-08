package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [PaymentJpaAdapter].
 * Verifies Payment persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class PaymentJpaAdapterTest @Autowired constructor(
    private val adapter: PaymentJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val payment = Payment(
            id = UUID.randomUUID(),
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("49.99"),
            paymentMethod = "VISA",
            status = PaymentStatus.COMPLETED,
            reference = "TXN-001",
            paidAt = Instant.now()
        )

        val saved = adapter.save(payment)
        assertEquals(payment.id, saved.id)
        assertEquals(BigDecimal("49.99"), saved.amount)
        assertEquals(PaymentStatus.COMPLETED, saved.status)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals("VISA", found?.paymentMethod)
        assertEquals("TXN-001", found?.reference)
    }

    @Test
    fun `findById returns null when payment does not exist`() {
        val found = adapter.findById(UUID.randomUUID())
        assertNull(found)
    }

    @Test
    fun `findByCustomerId returns payments for a customer`() {
        val customerId = UUID.randomUUID()
        val payment1 = Payment(
            id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(),
            customerId = customerId, amount = BigDecimal("10.00"),
            paymentMethod = "MC", status = PaymentStatus.COMPLETED,
            reference = null, paidAt = Instant.now()
        )
        val payment2 = Payment(
            id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(),
            customerId = customerId, amount = BigDecimal("20.00"),
            paymentMethod = "VISA", status = PaymentStatus.PENDING,
            reference = null, paidAt = Instant.now()
        )
        adapter.save(payment1)
        adapter.save(payment2)

        val payments = adapter.findByCustomerId(customerId)
        assertTrue(payments.any { it.id == payment1.id })
        assertTrue(payments.any { it.id == payment2.id })
    }

    @Test
    fun `findBySubscriptionId returns payments for a subscription`() {
        val subscriptionId = UUID.randomUUID()
        val payment = Payment(
            id = UUID.randomUUID(), subscriptionId = subscriptionId,
            customerId = UUID.randomUUID(), amount = BigDecimal("15.00"),
            paymentMethod = "VISA", status = PaymentStatus.COMPLETED,
            reference = "SUB-TXN", paidAt = Instant.now()
        )
        adapter.save(payment)

        val payments = adapter.findBySubscriptionId(subscriptionId)
        assertTrue(payments.any { it.id == payment.id })
    }

    @Test
    fun `findByStatus filters by payment status`() {
        val completedPayment = Payment(
            id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(), amount = BigDecimal("30.00"),
            paymentMethod = null, status = PaymentStatus.COMPLETED,
            reference = null, paidAt = Instant.now()
        )
        val failedPayment = Payment(
            id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(), amount = BigDecimal("5.00"),
            paymentMethod = null, status = PaymentStatus.FAILED,
            reference = null, paidAt = Instant.now()
        )
        adapter.save(completedPayment)
        adapter.save(failedPayment)

        val completed = adapter.findByStatus(PaymentStatus.COMPLETED)
        assertTrue(completed.any { it.id == completedPayment.id })
        assertFalse(completed.any { it.id == failedPayment.id })
    }

    @Test
    fun `save payment with null optional fields`() {
        val payment = Payment(
            id = UUID.randomUUID(), subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(), amount = BigDecimal("0.00"),
            paymentMethod = null, status = PaymentStatus.PENDING,
            reference = null, paidAt = Instant.now()
        )
        val saved = adapter.save(payment)
        assertNull(saved.paymentMethod)
        assertNull(saved.reference)
    }
}
