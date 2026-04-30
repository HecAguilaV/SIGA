package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.PaymentRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class TransbankAdapterTest {

    private val adapter = TransbankFictitiousAdapter()

    @Test
    fun `given valid request when process payment then return success and SII payload`() {
        val request = PaymentRequest(
            amount = BigDecimal("10000"),
            customerId = UUID.randomUUID(),
            description = "Test subscription"
        )

        val response = adapter.processPayment(request)

        assertTrue(response.success)
        assertEquals("0", response.responseCode)
        assertNotNull(response.transactionId)
        assertNotNull(response.siiPayload)
        assertEquals(BigDecimal("10000"), response.siiPayload?.get("montoTotal"))
    }
}
