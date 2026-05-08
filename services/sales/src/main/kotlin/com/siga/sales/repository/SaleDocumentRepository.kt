package com.siga.sales.repository

import com.siga.sales.entity.SaleDocumentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for sale documents (JPA).
 */
@Repository
interface SaleDocumentRepository : JpaRepository<SaleDocumentEntity, UUID> {
    fun findBySaleId(saleId: UUID): SaleDocumentEntity?
    fun findByCustomerId(customerId: UUID): List<SaleDocumentEntity>
}
