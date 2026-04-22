package com.siga.inventory.repository

import com.siga.inventory.entity.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for product stock by store.
 */
@Repository
interface StockRepository : JpaRepository<Stock, Int> {
    fun findByProductId(productId: Int): List<Stock>
    fun findByStoreId(storeId: Int): List<Stock>
    fun findByProductIdAndStoreId(productId: Int, storeId: Int): Stock?
}