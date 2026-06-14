package com.siga.sales.repository

import com.siga.sales.entity.SaleEntity
import com.siga.sales.domain.model.SaleStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
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

    /**
     * Aggregates total sales by day.
     * Truncates createdAt timestamp to date.
     */
    @Query("SELECT CAST(s.createdAt AS date) as date, SUM(s.total) as total " +
           "FROM SaleEntity s " +
           "WHERE s.status = 'COMPLETED' " +
           "GROUP BY CAST(s.createdAt AS date) " +
           "ORDER BY CAST(s.createdAt AS date) ASC")
    fun aggregateSalesByDay(): List<DailySalesProjection>
}

interface DailySalesProjection {
    fun getDate(): LocalDate
    fun getTotal(): BigDecimal
}
