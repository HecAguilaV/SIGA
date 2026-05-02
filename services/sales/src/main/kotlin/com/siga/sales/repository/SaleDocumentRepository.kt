package com.siga.sales.repository

import com.siga.sales.entity.SaleDocument
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for sale documents (boletas and facturas).
 */
@Repository
interface SaleDocumentRepository : JpaRepository<SaleDocument, UUID> {
    fun findBySaleId(saleId: UUID): SaleDocument?
    fun findByCustomerId(customerId: UUID): List<SaleDocument>
}
