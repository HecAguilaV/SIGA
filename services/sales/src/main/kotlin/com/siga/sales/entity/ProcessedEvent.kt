package com.siga.sales.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * Tracks processed Kafka events to guarantee idempotency.
 *
 * Before processing any incoming event, the consumer checks this table.
 * If the eventId already exists, the event is skipped (duplicate).
 * This prevents double-processing when Kafka redelivers messages.
 */
@Entity
@Table(name = "processed_events", schema = "sales")
class ProcessedEvent(
    @Id
    val eventId: UUID,

    @Column(name = "event_type", nullable = false, length = 50)
    val eventType: String,

    @Column(name = "processed_at", nullable = false)
    val processedAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProcessedEvent) return false
        return eventId == other.eventId
    }

    override fun hashCode(): Int = eventId.hashCode()

    override fun toString(): String = "ProcessedEvent(eventId=$eventId, eventType=$eventType)"
}
