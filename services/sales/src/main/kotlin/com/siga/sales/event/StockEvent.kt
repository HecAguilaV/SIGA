package com.siga.sales.event

import java.time.Instant
import java.util.UUID

/**
 * Event consumed by Sales from Inventory (SAGA step 3).
 *
 * Received from the `stock-events` Kafka topic. Sales uses this
 * to confirm or cancel the pending sale.
 *
 * @property eventType STOCK_RESERVED → confirm sale; STOCK_FAILED → cancel sale
 * @property reason explanation when stock fails (null on success)
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
 * Types of events emitted by the Inventory service.
 */
enum class StockEventType {
    /** Stock successfully reserved for all items */
    STOCK_RESERVED,
    /** Stock reservation failed (insufficient quantity) */
    STOCK_FAILED
}
