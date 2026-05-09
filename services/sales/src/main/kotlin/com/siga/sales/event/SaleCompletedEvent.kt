package com.siga.sales.event

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Event emitted by Sales when a sale is fully COMPLETED (SAGA step 4).
 *
 * Published to the `sale-completed` Kafka topic. Billing consumes this
 * event to generate the corresponding sale invoice.
 *
 * @property eventId unique identifier for idempotency checking
 * @property saleId the completed sale
 * @property storeId store where the sale occurred
 * @property userId cashier who processed the sale (nullable for legacy)
 * @property total total sale amount
 * @property items summary of products sold
 */
data class SaleCompletedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val saleId: UUID,
    val storeId: UUID,
    val userId: UUID? = null,
    val total: BigDecimal,
    val items: List<SaleCompletedItem> = emptyList(),
    val timestamp: Instant = Instant.now()
)

/**
 * Individual item within a [SaleCompletedEvent].
 */
data class SaleCompletedItem(
    val productId: UUID,
    val productName: String? = null,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val subtotal: BigDecimal
)
