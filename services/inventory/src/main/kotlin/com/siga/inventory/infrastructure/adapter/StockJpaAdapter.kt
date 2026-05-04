package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.StockRepositoryPort
import com.siga.inventory.entity.Stock as EntityStock
import com.siga.inventory.infrastructure.mapper.StockMapper
import com.siga.inventory.repository.StockRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for Stock persistence.
 */
@Component
class StockJpaAdapter(
    private val stockRepository: StockRepository
) : StockRepositoryPort {

    override fun findByProductIdAndStoreId(productId: UUID, storeId: UUID): Stock? {
        val entity = stockRepository.findByProductIdAndStoreId(productId, storeId) ?: return null
        return StockMapper.toDomain(entity)
    }

    override fun save(stock: Stock): Stock {
        val entity = StockMapper.toEntity(stock)
        // This is simplified; in reality, you'd fetch by ID for updates
        val savedEntity = stockRepository.save(entity)
        return StockMapper.toDomain(savedEntity)
    }
}
