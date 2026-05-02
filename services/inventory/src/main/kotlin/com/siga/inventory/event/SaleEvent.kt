package com.siga.inventory.event

import java.time.Instant
import java.util.UUID

/**
 * Event consumed by Inventory from Sales (SAGA step 2).
 *
 * Received from the `sale-events` Kafka topic. Inventory processes
 * this to attempt stock reservation for the requested items.
 *
 * This is a mirror of Sales' SaleEvent — kept as a separate class
 * to respect service boundary isolation (no shared library dependency).
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
 * Types of sale events received by Inventory.
 */
enum class SaleEventType {
    SALE_INITIATED,
    SALE_CANCELLED
}
