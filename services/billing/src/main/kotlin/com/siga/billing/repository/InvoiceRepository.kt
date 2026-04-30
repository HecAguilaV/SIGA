package com.siga.billing.repository

import com.siga.billing.entity.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for invoices.
 */
@Repository
interface InvoiceRepository : JpaRepository<Invoice, UUID> {
    fun findByCustomerId(customerId: UUID): List<Invoice>
    fun findByInvoiceNumber(invoiceNumber: String): Invoice?
}