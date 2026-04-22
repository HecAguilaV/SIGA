package com.siga.billing.repository

import com.siga.billing.entity.Subscription
import com.siga.billing.entity.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for subscriptions.
 */
@Repository
interface SubscriptionRepository : JpaRepository<Subscription, Int> {
    fun findByCustomerId(customerId: Int): List<Subscription>
    fun findByCustomerIdAndStatus(customerId: Int, status: SubscriptionStatus): List<Subscription>
    fun findByCustomerIdAndStatusIn(customerId: Int, statuses: List<SubscriptionStatus>): List<Subscription>
}