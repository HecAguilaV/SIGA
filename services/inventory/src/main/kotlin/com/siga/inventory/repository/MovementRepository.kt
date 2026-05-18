package com.siga.inventory.repository

import com.siga.inventory.entity.Movement
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

/**
 * Repository for stock movements (Kardex entries).
 */
@Repository
interface MovementRepository : JpaRepository<Movement, UUID>, JpaSpecificationExecutor<Movement> {
    fun findByProductId(productId: UUID): List<Movement>
    fun findByStoreId(storeId: UUID): List<Movement>
    fun findBySaleId(saleId: UUID): List<Movement>

    companion object {
        /**
         * Build a dynamic [Specification] for filtering movements with optional parameters.
         * Each predicate is only applied when its parameter is non-null.
         *
         * WHY Specification: The JPQL `:param IS NULL` pattern is unreliable with Hibernate 6
         * for enum and Instant types. Specifications generate precise WHERE clauses.
         */
        fun filterBy(
            storeId: UUID?,
            type: com.siga.inventory.entity.MovementType?,
            from: Instant?,
            to: Instant?
        ): Specification<Movement> {
            return Specification { root, _, cb ->
                val predicates = mutableListOf<Predicate>()
                storeId?.let { predicates.add(cb.equal(root.get<UUID>("storeId"), it)) }
                type?.let { predicates.add(cb.equal(root.get<com.siga.inventory.entity.MovementType>("type"), it)) }
                from?.let { predicates.add(cb.greaterThanOrEqualTo(root.get<Instant>("createdAt"), it)) }
                to?.let { predicates.add(cb.lessThanOrEqualTo(root.get<Instant>("createdAt"), it)) }
                cb.and(*predicates.toTypedArray())
            }
        }
    }
}
