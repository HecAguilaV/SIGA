package com.siga.sales.repository

import com.siga.sales.entity.Sale
import com.siga.sales.entity.SaleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for sales.
 */
@Repository
interface SaleRepository : JpaRepository<Sale, UUID> {
    fun findByStoreId(storeId: UUID): List<Sale>
    fun findByUserId(userId: UUID): List<Sale>
    fun findByStatus(status: SaleStatus): List<Sale>
}
