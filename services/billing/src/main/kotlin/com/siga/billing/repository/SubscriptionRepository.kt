package com.siga.billing.repository

import com.siga.billing.entity.Subscription
import com.siga.billing.entity.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for subscriptions.
 */
@Repository
interface SubscriptionRepository : JpaRepository<Subscription, UUID> {
    fun findByCustomerId(customerId: UUID): List<Subscription>
    fun findByCustomerIdAndStatus(customerId: UUID, status: SubscriptionStatus): List<Subscription>
    fun findByCustomerIdAndStatusIn(customerId: UUID, statuses: List<SubscriptionStatus>): List<Subscription>
}