package com.siga.billing.repository

import com.siga.billing.entity.Payment
import com.siga.billing.entity.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for payments.
 */
@Repository
interface PaymentRepository : JpaRepository<Payment, Int> {
    fun findByCustomerId(customerId: Int): List<Payment>
    fun findBySubscriptionId(subscriptionId: Int): List<Payment>
    fun findByStatus(status: PaymentStatus): List<Payment>
}