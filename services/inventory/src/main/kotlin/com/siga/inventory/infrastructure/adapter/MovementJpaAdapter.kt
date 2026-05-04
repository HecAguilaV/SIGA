package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.entity.Movement as EntityMovement
import com.siga.inventory.infrastructure.mapper.MovementMapper
import com.siga.inventory.repository.MovementRepository
import org.springframework.stereotype.Component
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
}
