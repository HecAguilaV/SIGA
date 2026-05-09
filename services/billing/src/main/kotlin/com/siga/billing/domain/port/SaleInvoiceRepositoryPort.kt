package com.siga.billing.domain.port

import com.siga.billing.domain.model.SaleInvoice

/**
 * Port for SaleInvoice persistence (hexagonal architecture).
 */
interface SaleInvoiceRepositoryPort {
    fun save(invoice: SaleInvoice): SaleInvoice
}
