package com.siga.sales.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * A line item within a Sale.
 *
 * Each item references a product from the Inventory service by its UUID
 * (logical reference — no FK across service boundaries).
 */
data class SaleItem(
    val id: UUID,
    val saleId: UUID,
    val productId: UUID,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val subtotal: BigDecimal
)
