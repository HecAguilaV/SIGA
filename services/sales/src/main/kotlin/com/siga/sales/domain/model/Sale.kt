package com.siga.sales.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Represents a point-of-sale transaction in a store.
 *
 * A sale is created with status PENDING and transitions to
 * COMPLETED or CANCELLED based on the SAGA
 * choreography with the Inventory service via Kafka events.
 *
 * @see SaleItem the line items of this sale
 * @see SaleDocument the legal tax document generated for this sale
 */
data class Sale(
    val id: UUID,
    val storeId: UUID,
    val userId: UUID?,
    val commercialUserId: Int?,
    val createdAt: Instant,
    val total: BigDecimal,
    val status: SaleStatus,
    val observations: String?
)
