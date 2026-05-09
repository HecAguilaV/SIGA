package com.siga.billing.repository

import com.siga.billing.entity.SaleInvoiceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for sale-generated invoices.
 */
@Repository
interface SaleInvoiceRepository : JpaRepository<SaleInvoiceEntity, UUID> {
    fun findBySaleId(saleId: UUID): SaleInvoiceEntity?
}
