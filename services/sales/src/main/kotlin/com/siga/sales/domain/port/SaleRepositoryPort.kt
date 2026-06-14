package com.siga.sales.domain.port

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
import java.util.UUID
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Port for Sale persistence.
 * Defines the contract the domain needs, implemented in infrastructure.
 */
interface SaleRepositoryPort {
    fun findById(id: UUID): Sale?
    fun save(sale: Sale): Sale
    fun findAll(): List<Sale>
    fun findByStoreId(storeId: UUID): List<Sale>
    fun findByUserId(userId: UUID): List<Sale>
    fun findByStatus(status: SaleStatus): List<Sale>
    fun aggregateSalesByDay(): List<com.siga.sales.repository.DailySalesProjection>
}
