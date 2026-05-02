package com.siga.inventory.repository

import com.siga.inventory.entity.Movement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for stock movements (Kardex entries).
 */
@Repository
interface MovementRepository : JpaRepository<Movement, UUID> {
    fun findByProductId(productId: UUID): List<Movement>
    fun findByStoreId(storeId: UUID): List<Movement>
    fun findBySaleId(saleId: UUID): List<Movement>
}
