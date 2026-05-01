package com.siga.billing.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * Shopping cart for plan selection.
 */
@Entity
@Table(name = "shopping_carts", schema = "billing")
class ShoppingCart(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "customer_id", nullable = false)
    var customerId: UUID,

    @Column(name = "plan_id")
    var planId: UUID? = null,

    @Column(name = "billing_period", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var period: BillingPeriod = BillingPeriod.MONTHLY,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    fun onPrePersist() {
        val now = Instant.now()
        if (createdAt == null) createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShoppingCart) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "ShoppingCart(id=$id, customerId=$customerId, period=$period)"
}