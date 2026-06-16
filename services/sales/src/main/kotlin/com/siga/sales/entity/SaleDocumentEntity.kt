package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * JPA Entity for SaleDocument.
 * Legal document issued for a sale (Boleta or Factura).
 *
 * @see com.siga.sales.domain.model.SaleDocument the domain model
 */
@Entity
@Table(name = "sale_documents", schema = "sales")
class SaleDocumentEntity(
    @Id
    var id: UUID? = null,

    @Column(name = "sale_id", nullable = false, unique = true)
    val saleId: UUID,

    @Column(name = "customer_id")
    val customerId: UUID? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val type: DocumentType,

    @Column(nullable = false)
    val folio: Long,

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    val totalAmount: BigDecimal,

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    val taxAmount: BigDecimal,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: DocumentStatus = DocumentStatus.EMITTED,

    @Column(name = "pdf_url")
    var pdfUrl: String? = null,

    @Column(name = "xml_url")
    var xmlUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaleDocumentEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "SaleDocumentEntity(id=$id, type=$type, folio=$folio, status=$status)"
}
