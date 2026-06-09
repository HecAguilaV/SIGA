package com.siga.billing.application.usecase

import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import com.siga.billing.domain.port.PaymentGateway
import com.siga.billing.domain.port.PaymentRepositoryPort
import com.siga.billing.domain.port.SubscriptionRepositoryPort
import com.siga.billing.domain.model.BillingPeriod
import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.time.Instant
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

    // ---------------------------------------------------------------
    // Failure paths and edge cases
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Payment failure returns failed PaymentResponse")
    fun `given payment failure when create subscription then returns failed response`() {
        val subscriptionId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val amount = BigDecimal("15000")

        val subscription = Subscription(
            id = subscriptionId,
            customerId = customerId,
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = Instant.now(),
            endsAt = null
        )

        val failedResponse = PaymentResponse(
            success = false,
            transactionId = "FAILED-001",
            responseCode = "1",
            message = "Insufficient funds",
            siiPayload = null
        )

        `when`(subscriptionPort.save(anyObject())).thenReturn(subscription)
        `when`(paymentGateway.processPayment(anyObject())).thenReturn(failedResponse)
        `when`(paymentPort.save(anyObject())).thenReturn(
            Payment(
                id = UUID.randomUUID(),
                subscriptionId = subscriptionId,
                customerId = customerId,
                amount = amount,
                paymentMethod = "TRANSBANK_FICTITIOUS",
                status = PaymentStatus.FAILED,
                reference = "FAILED-001",
                paidAt = Instant.now()
            )
        )

        val (savedSubscription, response) = useCase.createSubscriptionWithPayment(subscription, amount)

        assertFalse(response.success, "Payment should have failed")
        assertEquals("Insufficient funds", response.message)
        assertEquals(subscriptionId, savedSubscription.id)
        verify(subscriptionPort, times(1)).save(anyObject())
        verify(paymentPort, times(1)).save(anyObject())
    }

    @Test
    @DisplayName("Empty subscriptions list for customer")
    fun `getSubscriptionsByCustomer returns empty list when no subscriptions`() {
        val customerId = UUID.randomUUID()
        `when`(subscriptionPort.findByCustomerId(customerId)).thenReturn(emptyList())

        val subscriptions = useCase.getSubscriptionsByCustomer(customerId)

        assertTrue(subscriptions.isEmpty())
        verify(subscriptionPort, times(1)).findByCustomerId(customerId)
    }

    @Test
    @DisplayName("Get by ID returns null when not found")
    fun `getSubscriptionById returns null when not found`() {
        val unknownId = UUID.randomUUID()
        `when`(subscriptionPort.findById(unknownId)).thenReturn(null)

        val subscription = useCase.getSubscriptionById(unknownId)

        assertNull(subscription)
        verify(subscriptionPort, times(1)).findById(unknownId)
    }

    @Test
    @DisplayName("Active subscriptions filtering returns only ACTIVE subscriptions")
    fun `getActiveSubscriptions returns only active subscriptions`() {
        val customerId = UUID.randomUUID()
        val activeSubscription = Subscription(
            id = UUID.randomUUID(),
            customerId = customerId,
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = Instant.now(),
            endsAt = null
        )
        val cancelledSubscription = Subscription(
            id = UUID.randomUUID(),
            customerId = customerId,
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.CANCELLED,
            startsAt = Instant.now().minusSeconds(86400),
            endsAt = Instant.now()
        )

        // The port should only return active ones when filtering by ACTIVE status
        `when`(subscriptionPort.findByCustomerIdAndStatusIn(customerId, listOf(SubscriptionStatus.ACTIVE)))
            .thenReturn(listOf(activeSubscription))

        val subscriptions = useCase.getActiveSubscriptions(customerId)

        assertEquals(1, subscriptions.size, "Should only return 1 active subscription")
        assertEquals(SubscriptionStatus.ACTIVE, subscriptions[0].status)
        verify(subscriptionPort, times(1))
            .findByCustomerIdAndStatusIn(customerId, listOf(SubscriptionStatus.ACTIVE))
    }
}
