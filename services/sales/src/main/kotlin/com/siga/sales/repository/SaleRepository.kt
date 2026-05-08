package com.siga.sales.repository

import com.siga.sales.entity.SaleEntity
import com.siga.sales.domain.model.SaleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for sales (JPA).
 * Implements persistence for SaleEntity.
 */
@Repository
interface SaleRepository : JpaRepository<SaleEntity, UUID> {
    fun findByStoreId(storeId: UUID): List<SaleEntity>
    fun findByUserId(userId: UUID): List<SaleEntity>
    fun findByStatus(status: SaleStatus): List<SaleEntity>
}
