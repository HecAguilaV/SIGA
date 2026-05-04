package com.siga.billing.domain.port

import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus
import java.util.UUID

/**
 * Port for Payment persistence.
 */
interface PaymentRepositoryPort {
    fun findById(id: UUID): Payment?
    fun save(payment: Payment): Payment
    fun findByCustomerId(customerId: UUID): List<Payment>
    fun findBySubscriptionId(subscriptionId: UUID): List<Payment>
    fun findByStatus(status: PaymentStatus): List<Payment>
}
