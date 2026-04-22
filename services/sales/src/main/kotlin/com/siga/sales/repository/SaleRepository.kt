package com.siga.sales.repository

import com.siga.sales.entity.Sale
import com.siga.sales.entity.SaleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for sales.
 */
@Repository
interface SaleRepository : JpaRepository<Sale, Int> {
    fun findByStoreId(storeId: Int): List<Sale>
    fun findByUserId(userId: Int): List<Sale>
    fun findByStatus(status: SaleStatus): List<Sale>
}
