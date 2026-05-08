package com.siga.sales.domain.port

import com.siga.sales.domain.model.SaleDocument
import java.util.UUID

/**
 * Port for SaleDocument persistence.
 */
interface SaleDocumentRepositoryPort {
    fun findById(id: UUID): SaleDocument?
    fun save(document: SaleDocument): SaleDocument
    fun findBySaleId(saleId: UUID): SaleDocument?
}
