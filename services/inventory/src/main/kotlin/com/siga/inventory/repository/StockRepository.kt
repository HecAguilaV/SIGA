package com.siga.inventory.repository

import com.siga.inventory.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for product stock by store.
 */
@Repository
interface StockRepository : JpaRepository<Stock, UUID> {
    fun findByProductId(productId: UUID): List<Stock>
    fun findByProductIdIn(productIds: List<UUID>): List<Stock>
    fun findByStoreId(storeId: UUID): List<Stock>
    fun findByProductIdAndStoreId(productId: UUID, storeId: UUID): Stock?
}