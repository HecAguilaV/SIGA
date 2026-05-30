package com.siga.inventory.application.usecase

import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Use Case: Returns consolidated stock view per product across all stores.
 *
 * Flow:
 * 1. Load all stock entries (optionally filtered by storeId)
 * 2. Group stock by product ID
 * 3. Load product details for each product
 * 4. Aggregate total stock per product and build per-store breakdown
 * 5. Apply pagination in-memory
 *
 * WHY TWO-PHASE: Paginated products + batch load stock avoids N+1 while
 * keeping pagination correct across the product domain, not the stock domain.
 */
@Service
class ConsolidatedStockUseCase(
    private val productPort: ProductRepositoryPort,
    private val stockPort: StockRepositoryPort
) {
    /**
     * Executes the consolidated stock query.
     *
     * Results are cached in Redis for 60s (configured via `spring.cache.redis.time-to-live`).
     * Cache key format: `consolidatedStock::<storeId_or_"all">:<page>:<size>`.
     *
     * @param storeId Optional filter: if provided, only stock for this store is included.
     * @param page Zero-based page number.
     * @param size Page size.
     * @return [ConsolidatedStockResponse] with aggregated products.
     */
    @Cacheable(cacheNames = ["consolidatedStock"], key = "(#storeId?.toString() ?: 'all') + ':' + #page + ':' + #size")
    fun execute(storeId: UUID?, page: Int, size: Int): ConsolidatedStockResponse {
        val allStock = if (storeId != null) {
            stockPort.findAll().filter { it.storeId == storeId }
        } else {
            stockPort.findAll()
        }

        val groupedByProduct = allStock.groupBy { it.productId }
        val productIds = groupedByProduct.keys.toList()

        // Load product details for the stock entries
        val products = productIds.mapNotNull { productPort.findById(it) }
        val productMap = products.associateBy { it.id }

        val consolidatedProducts = productIds.mapNotNull { productId ->
            val product = productMap[productId] ?: return@mapNotNull null
            val stocks = groupedByProduct[productId] ?: emptyList()

            ConsolidatedProduct(
                productId = productId,
                productName = product.name,
                sku = product.sku ?: "",
                totalStock = stocks.sumOf { it.quantity },
                stores = stocks.map { stock ->
                    StoreStock(
                        storeId = stock.storeId,
                        quantity = stock.quantity,
                        lastMovementAt = stock.lastMovementAt
                    )
                }
            )
        }

        // In-memory pagination
        val startIndex = page * size
        val pagedProducts = consolidatedProducts.drop(startIndex).take(size)

        val totalElements = consolidatedProducts.size.toLong()
        val totalPages = if (size > 0) {
            ((totalElements + size - 1) / size).toInt()
        } else {
            0
        }

        return ConsolidatedStockResponse(
            products = pagedProducts,
            page = page,
            size = size,
            totalElements = totalElements,
            totalPages = totalPages
        )
    }
}

/**
 * Response DTO for consolidated stock view.
 */
data class ConsolidatedStockResponse(
    val products: List<ConsolidatedProduct>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

/**
 * Per-product consolidated data.
 */
data class ConsolidatedProduct(
    val productId: UUID,
    val productName: String,
    val sku: String,
    val totalStock: Int,
    val stores: List<StoreStock>
)

/**
 * Per-store breakdown within a consolidated product.
 */
data class StoreStock(
    val storeId: UUID,
    val quantity: Int,
    val lastMovementAt: Instant?
)
