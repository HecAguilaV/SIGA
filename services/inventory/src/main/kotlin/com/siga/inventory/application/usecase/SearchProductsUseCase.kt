package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

/**
 * Use Case: Searches products by name with case/accent-insensitive partial matching.
 *
 * Validates minimum query length (2 characters) then delegates to
 * [ProductRepositoryPort.search] which uses PostgreSQL ILIKE + unaccent.
 */
@Service
class SearchProductsUseCase(
    private val productPort: ProductRepositoryPort
) {
    /**
     * Searches products by query string.
     *
     * @param query Search term (must be at least 2 characters).
     * @param page Zero-based page number.
     * @param size Page size.
     * @return [Page] of [Product] matching the query.
     * @throws IllegalArgumentException if query is shorter than 2 characters.
     */
    fun execute(query: String, page: Int, size: Int): Page<Product> {
        if (query.length < 2) {
            throw IllegalArgumentException("Search query must be at least 2 characters")
        }
        return productPort.search(query, page, size)
    }
}
