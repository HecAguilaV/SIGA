package com.siga.inventory.controller

import com.siga.inventory.domain.model.Product
import java.util.UUID

/**
 * Response DTO for product search results.
 * Maps from domain [Product] to a lightweight search result.
 */
data class SearchResponse(
    val products: List<SearchProductItem>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
) {
    companion object {
        fun from(page: org.springframework.data.domain.Page<Product>, pageParam: Int, sizeParam: Int): SearchResponse {
            return SearchResponse(
                products = page.content.map { SearchProductItem.from(it) },
                page = pageParam,
                size = sizeParam,
                totalElements = page.totalElements,
                totalPages = page.totalPages
            )
        }
    }
}

data class SearchProductItem(
    val productId: UUID,
    val name: String,
    val sku: String,
    val categoryName: String? = null
) {
    companion object {
        fun from(product: Product): SearchProductItem {
            return SearchProductItem(
                productId = product.id,
                name = product.name,
                sku = product.sku ?: "",
                categoryName = null // TODO: resolve category name when CategoryRepositoryPort is available
            )
        }
    }
}
