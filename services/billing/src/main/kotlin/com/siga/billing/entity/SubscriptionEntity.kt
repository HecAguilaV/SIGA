package com.siga.billing.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * JPA Entity for Subscription.
 * Infrastructure detail. Use Subscription (Domain Model) in business logic.
 */
@Entity
@Table(name = "subscriptions", schema = "billing")
class SubscriptionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "customer_id", nullable = false)
    var customerId: UUID,

    @Column(name = "plan_id", nullable = false)
    var planId: UUID,

    @Column(name = "billing_period", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var period: BillingPeriod = BillingPeriod.MONTHLY,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: SubscriptionStatus = SubscriptionStatus.ACTIVE,

    @Column(name = "starts_at", nullable = false)
    var startsAt: Instant = Instant.now(),

    @Column(name = "ends_at")
    var endsAt: Instant? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    fun onPrePersist() {
        val now = Instant.now()
        if (startsAt == null) startsAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubscriptionEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "SubscriptionEntity(id=$id, status=$status, period=$period)"
}

enum class BillingPeriod {
    MONTHLY, ANNUAL
}

enum class SubscriptionStatus {
    ACTIVE, SUSPENDED, CANCELLED, EXPIRED
}
