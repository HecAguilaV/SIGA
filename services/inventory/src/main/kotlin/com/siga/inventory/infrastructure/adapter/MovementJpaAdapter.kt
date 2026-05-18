package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.infrastructure.mapper.MovementMapper
import com.siga.inventory.repository.MovementRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

/**
 * JPA Adapter for Movement audit trail.
 */
@Component
class MovementJpaAdapter(
    private val movementRepository: MovementRepository
) : MovementRepositoryPort {

    override fun save(movement: Movement): Movement {
        val entity = MovementMapper.toEntity(movement)
        val savedEntity = movementRepository.save(entity)
        return MovementMapper.toDomain(savedEntity)
    }

    override fun findBySaleId(saleId: UUID): List<Movement> {
        return movementRepository.findBySaleId(saleId).map { MovementMapper.toDomain(it) }
    }

    override fun findByFilters(
        storeId: UUID?,
        type: MovementType?,
        from: Instant?,
        to: Instant?,
        pageable: Pageable
    ): Page<Movement> {
        val entityType = type?.let { mapToEntityType(it) }
        val spec = MovementRepository.filterBy(storeId, entityType, from, to)
        return movementRepository.findAll(spec, pageable).map { MovementMapper.toDomain(it) }
    }

    private fun mapToEntityType(domainType: MovementType): com.siga.inventory.entity.MovementType {
        return when (domainType) {
            MovementType.ENTRY -> com.siga.inventory.entity.MovementType.IN
            MovementType.SALE -> com.siga.inventory.entity.MovementType.SALE
            MovementType.ADJUSTMENT -> com.siga.inventory.entity.MovementType.ADJUSTMENT
            MovementType.RECONCILIATION -> com.siga.inventory.entity.MovementType.RECONCILIATION
            MovementType.TRANSFER -> com.siga.inventory.entity.MovementType.TRANSFER
        }
    }
}
