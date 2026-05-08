package com.siga.sales.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Legal document issued for a sale (Boleta or Factura).
 *
 * Represents the tax document that Chilean law requires for every commercial
 * transaction. Boletas are for end consumers; Facturas are for businesses
 * and require a customer reference.
 *
 * @see Sale the sale associated with this document
 * @see Customer optional customer reference (mandatory for Facturas)
 */
data class SaleDocument(
    val id: UUID,
    val saleId: UUID,
    val customerId: UUID?,
    val type: DocumentType,
    val folio: Long,
    val totalAmount: BigDecimal,
    val taxAmount: BigDecimal,
    val status: DocumentStatus,
    val pdfUrl: String?,
    val xmlUrl: String?,
    val createdAt: Instant
)
