package com.siga.billing.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

/**
 * JPA Entity for Subscription Plans.
 */
@Entity
@Table(name = "plans", schema = "billing")
class PlanEntity(
    @Id
    var id: UUID? = null,

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

    @Column(name = "sort_order", nullable = false)
    var displayOrder: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlanEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "PlanEntity(id=$id, name=$name, monthlyPrice=$monthlyPrice)"
}
