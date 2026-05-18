package com.siga.inventory.domain.port

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.UUID

/**
 * Port (Hexagonal Architecture) for Movement audit trail persistence.
 */
interface MovementRepositoryPort {
    fun save(movement: Movement): Movement
    fun findBySaleId(saleId: UUID): List<Movement>
    fun findByFilters(
        storeId: UUID?,
        type: MovementType?,
        from: Instant?,
        to: Instant?,
        pageable: Pageable
    ): Page<Movement>
}
