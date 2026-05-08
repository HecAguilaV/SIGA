package com.siga.inventory.domain.port

import java.util.UUID

/**
 * Port (Hexagonal Architecture) for idempotency tracking of processed Kafka events.
 */
interface ProcessedEventRepositoryPort {
    fun existsById(eventId: UUID): Boolean
    fun save(eventId: UUID, eventType: String)
}
