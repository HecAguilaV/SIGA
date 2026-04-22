package com.siga.billing.entity

import jakarta.persistence.*
import java.time.Instant

/**
 * Shopping cart for plan selection.
 */
@Entity
@Table(name = "shopping_carts", schema = "commercial")
class ShoppingCart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "customer_id", nullable = false)
    var customerId: Int,

    @Column(name = "plan_id")
    var planId: Int? = null,

    @Column(name = "billing_period", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var period: BillingPeriod = BillingPeriod.MONTHLY,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ShoppingCart) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "ShoppingCart(id=$id, customerId=$customerId, period=$period)"
}