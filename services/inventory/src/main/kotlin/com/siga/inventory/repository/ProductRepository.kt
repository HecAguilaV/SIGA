package com.siga.inventory.repository

import com.siga.inventory.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for products.
 */
@Repository
interface ProductRepository : JpaRepository<Product, Int> {
    fun findByCommercialUserId(userId: Int): List<Product>
    fun findByCategoryId(categoryId: Int): List<Product>
    fun findByBarcode(barcode: String): Product?
}
