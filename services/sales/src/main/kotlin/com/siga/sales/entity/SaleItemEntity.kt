package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

/**
 * JPA Entity for SaleItem.
 * A line item within a sale.
 *
 * Each item references a product from the Inventory service by its UUID
 * (logical reference — no FK across service boundaries).
 *
 * @see com.siga.sales.domain.model.SaleItem the domain model
 */
@Entity
@Table(name = "sale_items", schema = "sales")
class SaleItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "sale_id", nullable = false)
    val saleId: UUID,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val subtotal: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaleItemEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "SaleItemEntity(id=$id, saleId=$saleId, productId=$productId, quantity=$quantity)"
}
