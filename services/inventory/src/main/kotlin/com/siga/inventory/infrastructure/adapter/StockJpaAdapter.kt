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
 *
 * WHY FETCH-AND-UPDATE: The domain [Stock] model has no `id` field (it identifies
 * stock by `productId + storeId`). When updating, we must fetch the existing entity
 * to preserve its ID and `updatedAt`, otherwise JPA would attempt an INSERT instead
 * of an UPDATE, causing constraint violations.
 */
@Component
class StockJpaAdapter(
    private val stockRepository: StockRepository
) : StockRepositoryPort {

    override fun findByProductIdAndStoreId(productId: UUID, storeId: UUID): Stock? {
        val entity = stockRepository.findByProductIdAndStoreId(productId, storeId) ?: return null
        return StockMapper.toDomain(entity)
    }

    override fun findByProductId(productId: UUID): List<Stock> {
        return stockRepository.findByProductId(productId).map { StockMapper.toDomain(it) }
    }

    override fun findByProductIds(productIds: List<UUID>): List<Stock> {
        return stockRepository.findByProductIdIn(productIds).map { StockMapper.toDomain(it) }
    }

    override fun findAll(): List<Stock> {
        return stockRepository.findAll().map { StockMapper.toDomain(it) }
    }

    override fun save(stock: Stock): Stock {
        val existingEntity = stockRepository.findByProductIdAndStoreId(stock.productId, stock.storeId)
        val entity = if (existingEntity != null) {
            // Update existing row — preserve ID and updatedAt
            existingEntity.quantity = stock.quantity
            existingEntity
        } else {
            StockMapper.toEntity(stock)
        }
        val savedEntity = stockRepository.save(entity)
        return StockMapper.toDomain(savedEntity)
    }
}
