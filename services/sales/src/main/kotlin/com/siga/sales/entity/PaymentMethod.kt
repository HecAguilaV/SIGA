package com.siga.sales.entity

import jakarta.persistence.*
import java.util.UUID

/**
 * Available payment method for POS transactions.
 *
 * Examples: Efectivo, Tarjeta Débito, Tarjeta Crédito, Transferencia.
 */
@Entity
@Table(name = "payment_methods", schema = "sales")
class PaymentMethod(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    var name: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaymentMethod) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "PaymentMethod(id=$id, name=$name)"
}
