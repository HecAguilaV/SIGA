package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Alert
import com.siga.inventory.domain.port.AlertRepositoryPort
import com.siga.inventory.infrastructure.mapper.AlertMapper
import com.siga.inventory.repository.AlertRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for Alert persistence.
 *
 * Implements [AlertRepositoryPort] using Spring Data JPA.
 */
@Component
class AlertJpaAdapter(
    private val alertRepository: AlertRepository
) : AlertRepositoryPort {

    override fun save(alert: Alert): Alert {
        val entity = AlertMapper.toEntity(alert)
        val savedEntity = alertRepository.save(entity)
        return AlertMapper.toDomain(savedEntity)
    }

    override fun findByStoreId(storeId: UUID): List<Alert> {
        return alertRepository.findByStoreId(storeId).map { AlertMapper.toDomain(it) }
    }
}
