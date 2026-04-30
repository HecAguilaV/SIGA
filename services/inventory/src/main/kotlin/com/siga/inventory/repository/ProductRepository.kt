package com.siga.inventory.repository

import com.siga.inventory.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for products.
 */
@Repository
interface ProductRepository : JpaRepository<Product, UUID> {
    fun findByCommercialUserId(userId: UUID): List<Product>
    fun findByCategoryId(categoryId: UUID): List<Product>
    fun findByBarcode(barcode: String): Product?
}
