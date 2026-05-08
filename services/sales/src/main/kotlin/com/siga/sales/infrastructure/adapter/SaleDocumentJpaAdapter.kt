package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.SaleDocument
import com.siga.sales.domain.port.SaleDocumentRepositoryPort
import com.siga.sales.entity.SaleDocumentEntity
import com.siga.sales.infrastructure.mapper.SaleDocumentMapper
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for SaleDocument.
 */
@Component
class SaleDocumentJpaAdapter(
    private val saleDocumentRepository: com.siga.sales.repository.SaleDocumentRepository,
    private val saleDocumentMapper: SaleDocumentMapper
) : SaleDocumentRepositoryPort {

    override fun findById(id: UUID): SaleDocument? {
        return saleDocumentRepository.findById(id).orElse(null)?.let { saleDocumentMapper.toDomain(it) }
    }

    override fun save(document: SaleDocument): SaleDocument {
        val entity = saleDocumentMapper.toEntity(document)
        return saleDocumentMapper.toDomain(saleDocumentRepository.save(entity))
    }

    override fun findBySaleId(saleId: UUID): SaleDocument? {
        return saleDocumentRepository.findBySaleId(saleId)?.let { saleDocumentMapper.toDomain(it) }
    }
}
