package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.SaleItem
import com.siga.sales.domain.port.SaleItemRepositoryPort
import com.siga.sales.entity.SaleItemEntity
import com.siga.sales.infrastructure.mapper.SaleItemMapper
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for SaleItem.
 */
@Component
class SaleItemJpaAdapter(
    private val saleItemRepository: com.siga.sales.repository.SaleItemRepository,
    private val saleItemMapper: SaleItemMapper
) : SaleItemRepositoryPort {

    override fun findById(id: UUID): SaleItem? {
        return saleItemRepository.findById(id).orElse(null)?.let { saleItemMapper.toDomain(it) }
    }

    override fun save(saleItem: SaleItem): SaleItem {
        val entity = saleItemMapper.toEntity(saleItem)
        return saleItemMapper.toDomain(saleItemRepository.save(entity))
    }

    override fun findBySaleId(saleId: UUID): List<SaleItem> {
        return saleItemRepository.findBySaleId(saleId).map { saleItemMapper.toDomain(it) }
    }
}
