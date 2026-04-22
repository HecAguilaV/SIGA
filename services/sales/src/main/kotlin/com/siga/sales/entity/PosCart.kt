package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "pos_cart", schema = "sales")
class PosCart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Int,

    @Column(name = "store_id", nullable = false)
    val storeId: Int,

    @Column(name = "product_id", nullable = false)
    val productId: Int,

    @Column(nullable = false)
    var quantity: Int,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PosCart) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "PosCart(id=$id, productId=$productId, quantity=$quantity)"
}
