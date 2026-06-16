package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.SaleDocument
import com.siga.sales.domain.model.DocumentType
import com.siga.sales.domain.model.DocumentStatus
import com.siga.sales.entity.SaleDocumentEntity
import com.siga.sales.entity.DocumentType as EntityDocumentType
import com.siga.sales.entity.DocumentStatus as EntityDocumentStatus
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Mapper between SaleDocument domain model and SaleDocumentEntity JPA entity.
 */
@Component
class SaleDocumentMapper {

    fun toDomain(entity: SaleDocumentEntity): SaleDocument {
        return SaleDocument(
            id = entity.id ?: UUID.randomUUID(),
            saleId = entity.saleId,
            customerId = entity.customerId,
            type = DocumentType.valueOf(entity.type.name),
            folio = entity.folio,
            totalAmount = entity.totalAmount,
            taxAmount = entity.taxAmount,
            status = DocumentStatus.valueOf(entity.status.name),
            pdfUrl = entity.pdfUrl,
            xmlUrl = entity.xmlUrl,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: SaleDocument): SaleDocumentEntity {
        return SaleDocumentEntity(
            id = if (domain.id == UUID.fromString("00000000-0000-0000-0000-000000000000")) null else domain.id,
            saleId = domain.saleId,
            customerId = domain.customerId,
            type = EntityDocumentType.valueOf(domain.type.name),
            folio = domain.folio,
            totalAmount = domain.totalAmount,
            taxAmount = domain.taxAmount,
            status = EntityDocumentStatus.valueOf(domain.status.name),
            pdfUrl = domain.pdfUrl,
            xmlUrl = domain.xmlUrl,
            createdAt = domain.createdAt
        )
    }
}
