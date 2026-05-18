package com.siga.inventory.domain.port

import com.siga.inventory.domain.model.Stock
import java.util.UUID

/**
 * Port (Hexagonal Architecture) for Stock persistence.
 *
 * WHY: The Domain/Application layers depend on this interface,
 * NOT on JPA. The infrastructure layer provides the implementation.
 */
interface StockRepositoryPort {
    fun findByProductIdAndStoreId(productId: UUID, storeId: UUID): Stock?
    fun findByProductId(productId: UUID): List<Stock>
    fun findByProductIds(productIds: List<UUID>): List<Stock>
    fun findAll(): List<Stock>
    fun save(stock: Stock): Stock
}
