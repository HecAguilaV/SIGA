package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Temporary cart item at the point of sale.
 *
 * Holds items before a [Sale] is finalized. Once the sale is created,
 * cart items are converted to [SaleItem] entries and the cart is cleared.
 */
@Entity
@Table(name = "pos_cart", schema = "sales")
class PosCart(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "sale_id")
    val saleId: UUID? = null,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,

    @Column(name = "store_id", nullable = false)
    val storeId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PosCart) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "PosCart(id=$id, productId=$productId, quantity=$quantity)"
}
