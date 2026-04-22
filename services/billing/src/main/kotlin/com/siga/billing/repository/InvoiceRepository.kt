package com.siga.billing.repository

import com.siga.billing.entity.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for invoices.
 */
@Repository
interface InvoiceRepository : JpaRepository<Invoice, Int> {
    fun findByUserId(userId: Int): List<Invoice>
    fun findByInvoiceNumber(invoiceNumber: String): Invoice?
}