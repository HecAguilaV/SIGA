package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.port.MovementRepositoryPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Use Case: Queries movement history with optional filters.
 *
 * Delegates to [MovementRepositoryPort.findByFilters] which supports
 * filtering by store, movement type, and date range.
 */
@Service
class TransferMovementHistoryUseCase(
    private val movementPort: MovementRepositoryPort
) {
    /**
     * Retrieves filtered movement history.
     *
     * @param storeId Optional filter by store.
     * @param type Optional filter by movement type.
     * @param from Optional start date (inclusive).
     * @param to Optional end date (inclusive).
     * @param pageable Pagination parameters.
     * @return [Page] of [Movement] matching the filters.
     */
    fun execute(
        storeId: UUID?,
        type: MovementType?,
        from: Instant?,
        to: Instant?,
        pageable: Pageable
    ): Page<Movement> {
        return movementPort.findByFilters(storeId, type, from, to, pageable)
    }
}
