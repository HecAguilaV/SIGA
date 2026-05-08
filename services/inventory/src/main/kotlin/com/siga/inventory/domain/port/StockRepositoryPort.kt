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
    fun save(stock: Stock): Stock
}
