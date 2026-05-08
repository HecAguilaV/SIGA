package com.siga.sales.domain.port

import com.siga.sales.domain.model.SaleItem
import java.util.UUID

/**
 * Port for SaleItem persistence.
 */
interface SaleItemRepositoryPort {
    fun findById(id: UUID): SaleItem?
    fun save(saleItem: SaleItem): SaleItem
    fun findBySaleId(saleId: UUID): List<SaleItem>
}
