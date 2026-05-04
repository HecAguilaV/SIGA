package com.siga.billing.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for an active subscription.
 */
data class Subscription(
    val id: UUID,
    val customerId: UUID,
    val planId: UUID,
    val period: BillingPeriod,
    val status: SubscriptionStatus,
    val startsAt: Instant,
    val endsAt: Instant?
)

enum class BillingPeriod { MONTHLY, ANNUAL }
enum class SubscriptionStatus { ACTIVE, SUSPENDED, CANCELLED, EXPIRED }
