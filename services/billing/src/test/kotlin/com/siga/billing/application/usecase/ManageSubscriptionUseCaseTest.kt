package com.siga.billing.application.usecase

import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import com.siga.billing.domain.port.PaymentGateway
import com.siga.billing.domain.port.PaymentRepositoryPort
import com.siga.billing.domain.port.SubscriptionRepositoryPort
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.util.UUID

class ManageSubscriptionUseCaseTest {

    private val subscriptionPort = mock(SubscriptionRepositoryPort::class.java)
    private val paymentPort = mock(PaymentRepositoryPort::class.java)
    private val paymentGateway = mock(PaymentGateway::class.java)
    private val useCase = ManageSubscriptionUseCase(subscriptionPort, paymentPort, paymentGateway)

    private fun <T> anyObject(): T {
        any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }

    @Test
    fun `given valid data when create subscription with payment then return success`() {
        val subscriptionId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val amount = BigDecimal("15000")

        val subscription = Subscription(
            id = subscriptionId,
            customerId = customerId,
            planId = UUID.randomUUID(),
            period = com.siga.billing.domain.model.BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = java.time.Instant.now(),
            endsAt = null
        )

        val expectedResponse = PaymentResponse(
            success = true,
            transactionId = "TEST-123",
            responseCode = "0",
            message = "Approved",
            siiPayload = null
        )

        // Mock Port behavior: return the same subscription when saved
        `when`(subscriptionPort.save(anyObject())).thenReturn(subscription)
        `when`(paymentPort.save(anyObject())).thenReturn(
            com.siga.billing.domain.model.Payment(
                id = UUID.randomUUID(),
                subscriptionId = subscriptionId,
                customerId = customerId,
                amount = amount,
                paymentMethod = "TEST",
                status = com.siga.billing.domain.model.PaymentStatus.COMPLETED,
                reference = "TEST-123",
                paidAt = java.time.Instant.now()
            )
        )
        `when`(paymentGateway.processPayment(anyObject())).thenReturn(expectedResponse)

        val (savedSubscription, response) = useCase.createSubscriptionWithPayment(subscription, amount)

        assertTrue(response.success)
        assertEquals(subscriptionId, savedSubscription.id)
        verify(subscriptionPort, times(1)).save(anyObject())
        verify(paymentPort, times(1)).save(anyObject())
    }
}
