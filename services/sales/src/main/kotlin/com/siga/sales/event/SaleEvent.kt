package com.siga.sales.event

import java.time.Instant
import java.util.UUID

/**
 * Event emitted by Sales when a new sale is created (SAGA step 1).
 *
 * Published to the `sale-events` Kafka topic. Inventory consumes this
 * event to attempt stock reservation for each item.
 *
 * @property eventId unique identifier for idempotency checking
 * @property saleId the sale waiting for stock confirmation
 * @property items list of products and quantities to reserve
 */
data class SaleEvent(
    val eventId: UUID = UUID.randomUUID(),
    val eventType: SaleEventType,
    val saleId: UUID,
    val tenantId: UUID,
    val userId: UUID? = null,
    val items: List<SaleItemEvent> = emptyList(),
    val timestamp: Instant = Instant.now()
)

/**
 * Individual item within a [SaleEvent].
 */
data class SaleItemEvent(
    val productId: UUID,
    val quantity: Int
)

/**
 * Types of events emitted by the Sales service.
 */
enum class SaleEventType {
    /** Sale created, requesting stock reservation */
    SALE_INITIATED,
    /** Sale explicitly cancelled by user before stock response */
    SALE_CANCELLED
}
