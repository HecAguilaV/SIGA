package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "sale_items", schema = "sales")
class SaleItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "sale_id", nullable = false)
    val saleId: Int,

    @Column(name = "product_id", nullable = false)
    val productId: Int,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val subtotal: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaleItem) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "SaleItem(id=$id, saleId=$saleId, productId=$productId, quantity=$quantity)"
}
