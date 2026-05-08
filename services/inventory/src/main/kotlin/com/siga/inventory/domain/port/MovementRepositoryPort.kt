package com.siga.inventory.domain.port

import com.siga.inventory.domain.model.Movement
import java.util.UUID

/**
 * Port (Hexagonal Architecture) for Movement audit trail persistence.
 */
interface MovementRepositoryPort {
    fun save(movement: Movement): Movement
    fun findBySaleId(saleId: UUID): List<Movement>
}
