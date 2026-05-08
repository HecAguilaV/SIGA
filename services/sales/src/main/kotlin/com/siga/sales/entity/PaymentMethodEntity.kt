package com.siga.sales.entity

import jakarta.persistence.*
import java.util.UUID

/**
 * JPA Entity for PaymentMethod.
 * Available payment method for POS transactions.
 *
 * @see com.siga.sales.domain.model.PaymentMethod the domain model
 */
@Entity
@Table(name = "payment_methods", schema = "sales")
class PaymentMethodEntity(
    @Id
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    var name: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaymentMethodEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "PaymentMethodEntity(id=$id, name=$name)"
}
