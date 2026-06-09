package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PaymentStatusTest {

    @Test
    fun `payment status enum has expected values`() {
        val values = PaymentStatus.entries
        assertEquals(4, values.size)
        assertTrue(values.contains(PaymentStatus.PENDING))
        assertTrue(values.contains(PaymentStatus.COMPLETED))
        assertTrue(values.contains(PaymentStatus.FAILED))
        assertTrue(values.contains(PaymentStatus.REFUNDED))
    }

    @Test
    fun `payment status from string`() {
        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"))
        assertEquals(PaymentStatus.COMPLETED, PaymentStatus.valueOf("COMPLETED"))
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"))
        assertEquals(PaymentStatus.REFUNDED, PaymentStatus.valueOf("REFUNDED"))
    }

    @Test
    fun `payment status order is preserved`() {
        val values = PaymentStatus.entries
        assertEquals(PaymentStatus.PENDING, values[0])
        assertEquals(PaymentStatus.COMPLETED, values[1])
        assertEquals(PaymentStatus.FAILED, values[2])
        assertEquals(PaymentStatus.REFUNDED, values[3])
    }
}
