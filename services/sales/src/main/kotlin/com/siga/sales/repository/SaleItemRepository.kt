package com.siga.sales.repository

import com.siga.sales.entity.SaleItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for sale items (JPA).
 */
@Repository
interface SaleItemRepository : JpaRepository<SaleItemEntity, UUID> {
    fun findBySaleId(saleId: UUID): List<SaleItemEntity>
}
