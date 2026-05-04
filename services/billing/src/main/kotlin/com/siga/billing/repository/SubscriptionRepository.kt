package com.siga.billing.repository

import com.siga.billing.entity.SubscriptionEntity
import com.siga.billing.entity.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA Repository for Subscription.
 * Used by SubscriptionJpaAdapter.
 */
@Repository
interface SubscriptionRepository : JpaRepository<SubscriptionEntity, UUID> {
    fun findByCustomerId(customerId: UUID): List<SubscriptionEntity>
    fun findByCustomerIdAndStatus(customerId: UUID, status: SubscriptionStatus): List<SubscriptionEntity>
    fun findByCustomerIdAndStatusIn(customerId: UUID, statuses: List<SubscriptionStatus>): List<SubscriptionEntity>
}
