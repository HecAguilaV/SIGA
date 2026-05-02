package com.siga.inventory.event

import java.time.Instant
import java.util.UUID

/**
 * Event emitted by Inventory in response to a [SaleEvent] (SAGA step 2).
 *
 * Published to the `stock-events` Kafka topic. Sales consumes this
 * to confirm or cancel the pending sale.
 */
data class StockEvent(
    val eventId: UUID = UUID.randomUUID(),
    val eventType: StockEventType,
    val saleId: UUID,
    val tenantId: UUID,
    val reason: String? = null,
    val timestamp: Instant = Instant.now()
)

/**
 * Types of stock events emitted by Inventory.
 */
enum class StockEventType {
    /** Stock successfully reserved for all items in the sale */
    STOCK_RESERVED,
    /** Stock reservation failed — insufficient quantity for one or more items */
    STOCK_FAILED
}
