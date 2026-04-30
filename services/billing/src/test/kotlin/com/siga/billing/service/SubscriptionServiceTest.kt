package com.siga.billing.service

import com.siga.billing.domain.model.PaymentRequest
import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.port.PaymentGateway
import com.siga.billing.entity.Payment
import com.siga.billing.repository.PaymentRepository
import com.siga.billing.repository.SubscriptionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.util.UUID

class SubscriptionServiceTest {

    private val subscriptionRepository = mock(SubscriptionRepository::class.java)
    private val paymentRepository = mock(PaymentRepository::class.java)
    private val paymentGateway = mock(PaymentGateway::class.java)
    private val service = SubscriptionService(subscriptionRepository, paymentRepository, paymentGateway)

    /**
     * Helper to bypass Kotlin null-safety in Mockito any()
     */
    private fun <T> anyObject(): T {
        any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }

    @Test
    fun `given valid data when process subscription payment then save payment and return response`() {
        val subscriptionId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val amount = BigDecimal("15000")

        val expectedResponse = PaymentResponse(
            success = true,
            transactionId = "TEST-123",
            responseCode = "0",
            message = "Approved"
        )

        `when`(paymentGateway.processPayment(anyObject())).thenReturn(expectedResponse)

        val response = service.processSubscriptionPayment(subscriptionId, customerId, amount)

        assertTrue(response.success)
        verify(paymentRepository, times(1)).save(anyObject())
    }
}
