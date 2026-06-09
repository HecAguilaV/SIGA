package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PaymentResponseTest {

    @Test
    fun `create successful payment response`() {
        val response = PaymentResponse(
            success = true,
            transactionId = "TXN-12345",
            responseCode = "0",
            message = "Transacción aprobada",
            siiPayload = mapOf("authorization_code" to "AUTH-001", "installments" to 1)
        )

        assertTrue(response.success)
        assertEquals("TXN-12345", response.transactionId)
        assertEquals("0", response.responseCode)
        assertEquals("Transacción aprobada", response.message)
        assertNotNull(response.siiPayload)
        assertEquals("AUTH-001", response.siiPayload!!["authorization_code"])
        assertEquals(1, response.siiPayload!!["installments"])
    }

    @Test
    fun `create failed payment response`() {
        val response = PaymentResponse(
            success = false,
            transactionId = "",
            responseCode = "500",
            message = "Fondos insuficientes",
            siiPayload = null
        )

        assertFalse(response.success)
        assertTrue(response.transactionId.isEmpty())
        assertEquals("500", response.responseCode)
        assertEquals("Fondos insuficientes", response.message)
        assertNull(response.siiPayload)
    }

    @Test
    fun `create payment response without optional siiPayload`() {
        val response = PaymentResponse(
            success = true,
            transactionId = "TXN-999",
            responseCode = "0",
            message = "OK"
        )

        assertTrue(response.success)
        assertNull(response.siiPayload)
    }

    @Test
    fun `payment response data class equality`() {
        val response1 = PaymentResponse(true, "TXN-1", "0", "OK", mapOf("key" to "value"))
        val response2 = response1.copy()

        assertEquals(response1, response2)
        assertEquals(response1.hashCode(), response2.hashCode())
    }

    @Test
    fun `payment response data class inequality`() {
        val response1 = PaymentResponse(true, "TXN-1", "0", "OK", null)
        val response2 = PaymentResponse(false, "TXN-1", "500", "Error", null)

        assertNotEquals(response1, response2)
    }
}
