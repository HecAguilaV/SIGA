package com.siga.billing.service

import com.siga.billing.domain.model.PaymentRequest
import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.port.PaymentGateway
import com.siga.billing.entity.Payment
import com.siga.billing.entity.PaymentStatus
import com.siga.billing.entity.Subscription
import com.siga.billing.repository.PaymentRepository
import com.siga.billing.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Domain service to orchestrate subscription business logic.
 */
@Service
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentGateway: PaymentGateway
) {

    /**
     * Processes a subscription payment using the payment gateway.
     */
    @Transactional
    fun processSubscriptionPayment(subscriptionId: UUID, customerId: UUID, amount: java.math.BigDecimal): PaymentResponse {
        val request = PaymentRequest(
            amount = amount,
            customerId = customerId,
            description = "Payment for subscription $subscriptionId"
        )

        // Delegate to the port (Hexagonal Architecture)
        val response = paymentGateway.processPayment(request)

        // Save payment record
        val payment = Payment(
            subscriptionId = subscriptionId,
            customerId = customerId,
            amount = amount,
            status = if (response.success) PaymentStatus.COMPLETED else PaymentStatus.FAILED,
            reference = response.transactionId,
            paymentMethod = "TRANSBANK_FICTITIOUS"
        )
        paymentRepository.save(payment)

        return response
    }
}
