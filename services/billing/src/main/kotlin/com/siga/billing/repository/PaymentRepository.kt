package com.siga.billing.repository

import com.siga.billing.entity.Payment
import com.siga.billing.entity.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for payments.
 */
@Repository
interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByCustomerId(customerId: UUID): List<Payment>
    fun findBySubscriptionId(subscriptionId: UUID): List<Payment>
    fun findByStatus(status: PaymentStatus): List<Payment>
}