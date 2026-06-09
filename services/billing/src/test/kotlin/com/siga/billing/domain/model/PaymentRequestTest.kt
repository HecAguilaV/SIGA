package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class PaymentRequestTest {

    @Test
    fun `create payment request with all fields`() {
        val request = PaymentRequest(
            amount = BigDecimal("15000"),
            customerId = UUID.randomUUID(),
            description = "Pago suscripción Premium",
            currency = "USD"
        )

        assertEquals(BigDecimal("15000"), request.amount)
        assertNotNull(request.customerId)
        assertEquals("Pago suscripción Premium", request.description)
        assertEquals("USD", request.currency)
    }

    @Test
    fun `payment request defaults to CLP currency`() {
        val request = PaymentRequest(
            amount = BigDecimal("5000"),
            customerId = UUID.randomUUID(),
            description = "Suscripción mensual"
        )

        assertEquals("CLP", request.currency)
    }

    @Test
    fun `payment request data class equality`() {
        val request1 = PaymentRequest(BigDecimal.TEN, UUID.randomUUID(), "Test", "CLP")
        val request2 = request1.copy()

        assertEquals(request1, request2)
        assertEquals(request1.hashCode(), request2.hashCode())
    }

    @Test
    fun `payment request with zero amount`() {
        val request = PaymentRequest(
            amount = BigDecimal.ZERO,
            customerId = UUID.randomUUID(),
            description = "Free payment"
        )

        assertEquals(BigDecimal.ZERO, request.amount)
    }
}
