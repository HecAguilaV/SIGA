package com.siga.billing.entity

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * SaaS subscription plan with operational limits.
 */
@Entity
@Table(name = "plans", schema = "commercial")
class Plan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, unique = true, length = 100)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "store_limit", nullable = false)
    var storeLimit: Int = 1,

    @Column(name = "user_limit", nullable = false)
    var userLimit: Int = 3,

    @Column(name = "product_limit")
    var productLimit: Int? = null,

    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    var monthlyPrice: BigDecimal,

    @Column(name = "yearly_price", precision = 10, scale = 2)
    var yearlyPrice: BigDecimal? = null,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Plan) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Plan(id=$id, name=$name, monthlyPrice=$monthlyPrice)"
}