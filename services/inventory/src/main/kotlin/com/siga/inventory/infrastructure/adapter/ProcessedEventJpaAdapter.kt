package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.port.ProcessedEventRepositoryPort
import com.siga.inventory.entity.ProcessedEvent
import com.siga.inventory.repository.ProcessedEventRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for Idempotency checks (Kafka SAGA).
 */
@Component
class ProcessedEventJpaAdapter(
    private val processedEventRepository: ProcessedEventRepository
) : ProcessedEventRepositoryPort {

    override fun existsById(eventId: UUID): Boolean {
        return processedEventRepository.existsById(eventId)
    }

    override fun save(eventId: UUID, eventType: String) {
        processedEventRepository.save(ProcessedEvent(eventId = eventId, eventType = eventType))
    }
}
