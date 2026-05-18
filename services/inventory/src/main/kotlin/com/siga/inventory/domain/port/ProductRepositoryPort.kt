package com.siga.inventory.domain.port

import com.siga.inventory.domain.model.Product
import org.springframework.data.domain.Page
import java.util.UUID

/**
 * Port (Hexagonal Architecture) for Product persistence.
 */
interface ProductRepositoryPort {
    fun findById(id: UUID): Product?
    fun save(product: Product): Product
    fun findByCommercialUserId(userId: UUID): List<Product>
    fun findByCategoryId(categoryId: UUID): List<Product>
    fun findByBarcode(barcode: String): Product?
    fun search(query: String, page: Int, size: Int): Page<Product>
    fun findByNameLike(name: String): List<Product>
}
