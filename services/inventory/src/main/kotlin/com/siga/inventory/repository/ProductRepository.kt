package com.siga.inventory.repository

import com.siga.inventory.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    fun findByNameLike(name: String): List<Product>

    /**
     * Accent-insensitive, case-insensitive product name search using PostgreSQL unaccent + ILIKE.
     *
     * Requires the `f_unaccent()` IMMUTABLE wrapper function created in V2 migration
     * and a GIN trigram index on `f_unaccent(name)`.
     *
     * NOTE: This JPQL works with PostgreSQL. For H2/unit tests, the `@Sql` script
     * creates a compatible `f_unaccent` alias.
     */
    @Query("SELECT p FROM Product p WHERE f_unaccent(p.name) ILIKE f_unaccent(CONCAT('%', :query, '%'))")
    fun search(@Param("query") query: String, pageable: Pageable): Page<Product>
}
