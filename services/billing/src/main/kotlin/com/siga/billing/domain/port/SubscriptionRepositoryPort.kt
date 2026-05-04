package com.siga.billing.domain.port

import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import java.util.UUID

/**
 * Port for Subscription persistence.
 */
interface SubscriptionRepositoryPort {
    fun findById(id: UUID): Subscription?
    fun save(subscription: Subscription): Subscription
    fun findByCustomerId(customerId: UUID): List<Subscription>
    fun findByCustomerIdAndStatus(customerId: UUID, status: SubscriptionStatus): List<Subscription>
    fun findByCustomerIdAndStatusIn(customerId: UUID, statuses: List<SubscriptionStatus>): List<Subscription>
}
