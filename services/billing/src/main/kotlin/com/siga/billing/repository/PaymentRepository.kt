package com.siga.billing.repository

import com.siga.billing.entity.PaymentEntity
import com.siga.billing.entity.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA Repository for Payment.
 * Used by PaymentJpaAdapter.
 */
@Repository
interface PaymentRepository : JpaRepository<PaymentEntity, UUID> {
    fun findByCustomerId(customerId: UUID): List<PaymentEntity>
    fun findBySubscriptionId(subscriptionId: UUID): List<PaymentEntity>
    fun findByStatus(status: PaymentStatus): List<PaymentEntity>
}
