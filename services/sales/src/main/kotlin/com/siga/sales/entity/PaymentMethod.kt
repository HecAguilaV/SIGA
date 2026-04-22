package com.siga.sales.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "payment_methods", schema = "sales")
class PaymentMethod(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, unique = true, length = 50)
    var name: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaymentMethod) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "PaymentMethod(id=$id, name=$name)"
}
