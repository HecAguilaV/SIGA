package com.siga.sales.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Temporary cart item at the point of sale.
 *
 * Holds items before a Sale is finalized. Once the sale is created,
 * cart items are converted to SaleItem entries and the cart is cleared.
 */
data class PosCart(
    val id: UUID,
    val saleId: UUID?,
    val productId: UUID,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val storeId: UUID,
    val userId: UUID,
    val createdAt: Instant
)
