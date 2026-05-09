package com.siga.billing.event

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Event consumed by Billing from Sales (SAGA step 4).
 *
 * Received from the `sale-completed` Kafka topic. Billing uses this
 * to generate the corresponding sale invoice.
 *
 * @property eventId unique identifier for idempotency checking
 * @property saleId the completed sale
 * @property storeId store where the sale occurred
 * @property userId cashier who processed the sale
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

/**
 * Types of events consumed by the Billing service.
 */
enum class SaleEventType {
    SALE_COMPLETED
}
