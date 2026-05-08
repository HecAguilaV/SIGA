package com.siga.billing.application.usecase

import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.port.PaymentGateway
import com.siga.billing.domain.port.SubscriptionRepositoryPort
import com.siga.billing.domain.port.PaymentRepositoryPort
import com.siga.billing.infrastructure.mapper.SubscriptionMapper
import com.siga.billing.infrastructure.mapper.PaymentMapper
import java.math.BigDecimal
import java.util.UUID

/**
 * Application Use Case: Orchestrates subscription management logic.
 * This is the "Application Layer" in Hexagonal Architecture.
 */
@org.springframework.stereotype.Service
class ManageSubscriptionUseCase(
    private val subscriptionPort: SubscriptionRepositoryPort,
    private val paymentPort: PaymentRepositoryPort,
    private val paymentGateway: PaymentGateway
) {

    /**
     * Creates a subscription and processes the initial payment.
     * Returns the domain model and the payment response.
     */
    fun createSubscriptionWithPayment(
        subscription: Subscription,
        amount: BigDecimal
    ): Pair<Subscription, PaymentResponse> {
        // 1. Save the subscription intent (Domain -> Persistence via Port)
        val savedSubscription = subscriptionPort.save(subscription)

        // 2. Process payment via Port (Gateway)
        val request = com.siga.billing.domain.model.PaymentRequest(
            amount = amount,
            customerId = savedSubscription.customerId,
            description = "Payment for subscription ${savedSubscription.id}"
        )
        val response = paymentGateway.processPayment(request)

        // 3. Record payment via Port
        val payment = com.siga.billing.domain.model.Payment(
            id = UUID.randomUUID(),
            subscriptionId = savedSubscription.id,
            customerId = savedSubscription.customerId,
            amount = amount,
            paymentMethod = "TRANSBANK_FICTITIOUS",
            status = if (response.success) com.siga.billing.domain.model.PaymentStatus.COMPLETED 
                     else com.siga.billing.domain.model.PaymentStatus.FAILED,
            reference = response.transactionId,
            paidAt = java.time.Instant.now()
        )
        paymentPort.save(payment)

        return Pair(savedSubscription, response)
    }

    fun getSubscriptionById(id: UUID): Subscription? {
        return subscriptionPort.findById(id)
    }

    fun getSubscriptionsByCustomer(customerId: UUID): List<Subscription> {
        return subscriptionPort.findByCustomerId(customerId)
    }

    fun getActiveSubscriptions(customerId: UUID): List<Subscription> {
        return subscriptionPort.findByCustomerIdAndStatusIn(
            customerId, 
            listOf(com.siga.billing.domain.model.SubscriptionStatus.ACTIVE)
        )
    }
}
