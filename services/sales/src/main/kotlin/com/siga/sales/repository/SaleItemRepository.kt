package com.siga.sales.repository

import com.siga.sales.entity.SaleItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for sale items.
 */
@Repository
interface SaleItemRepository : JpaRepository<SaleItem, Int> {
    fun findBySaleId(saleId: Int): List<SaleItem>
}
