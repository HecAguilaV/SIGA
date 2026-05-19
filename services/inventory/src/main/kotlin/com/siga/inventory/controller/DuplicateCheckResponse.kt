package com.siga.inventory.controller

import com.siga.inventory.domain.model.Product
import java.util.UUID

/**
 * Response DTO for product duplicate check.
 */
data class DuplicateCheckResponse(
    val duplicates: List<DuplicateItem>
)

data class DuplicateItem(
    val productId: UUID,
    val name: String,
    val sku: String
) {
    companion object {
        fun from(product: Product): DuplicateItem {
            return DuplicateItem(
                productId = product.id,
                name = product.name,
                sku = product.sku ?: ""
            )
        }
    }
}
