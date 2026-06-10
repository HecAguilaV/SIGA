package com.siga.inventory.integration

import com.siga.inventory.entity.Product
import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.StockRepository
import com.siga.inventory.repository.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Integration test for consolidated stock query via [StockRepository.findByProductIdIn].
 *
 * Verifies that batch-loading stock records by multiple product IDs returns the
 * correct quantities per product, supporting the [ConsolidatedStockUseCase]
 * two-phase pagination strategy (Phase 3 design decision).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("ConsolidatedStockQueryTest: StockRepository batch loading")
class ConsolidatedStockQueryTest {

    @Autowired
    private lateinit var stockRepository: StockRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    private val tenantId = UUID.randomUUID()
    private val storeA = UUID.randomUUID()
    private val storeB = UUID.randomUUID()
    private val now = Instant.now()

    private lateinit var product1: Product
    private lateinit var product2: Product
    private lateinit var product3: Product

    @BeforeEach
    fun setUp() {
        product1 = productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Café Instantáneo 200g",
                description = null,
                categoryId = null,
                barcode = "CAF-001",
                sku = "CAF-001",
                unitPrice = BigDecimal("15.00"),
                isActive = true,
                commercialUserId = tenantId,
                createdAt = now,
                updatedAt = now
            )
        )
        product2 = productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Galleta Surtida 250g",
                description = null,
                categoryId = null,
                barcode = "GAL-001",
                sku = "GAL-001",
                unitPrice = BigDecimal("10.00"),
                isActive = true,
                commercialUserId = tenantId,
                createdAt = now,
                updatedAt = now
            )
        )
        product3 = productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Arroz 1kg",
                description = null,
                categoryId = null,
                barcode = "ARZ-001",
                sku = "ARZ-001",
                unitPrice = BigDecimal("5.00"),
                isActive = true,
                commercialUserId = tenantId,
                createdAt = now,
                updatedAt = now
            )
        )

        // Stock for product1 across two stores
        stockRepository.save(
            Stock(id = UUID.randomUUID(), productId = product1.id!!, storeId = storeA, quantity = 50, updatedAt = now)
        )
        stockRepository.save(
            Stock(id = UUID.randomUUID(), productId = product1.id!!, storeId = storeB, quantity = 30, updatedAt = now)
        )

        // Stock for product2 in one store
        stockRepository.save(
            Stock(id = UUID.randomUUID(), productId = product2.id!!, storeId = storeA, quantity = 100, updatedAt = now)
        )

        // Product3 has no stock (tests edge case)
    }

    @Test
    @DisplayName("findByProductIdIn returns stock for all products with stock")
    fun `findByProductIdIn returns stock for products that have stock`() {
        val ids = listOf(product1.id!!, product2.id!!, product3.id!!)
        val result = stockRepository.findByProductIdIn(ids)

        // Product1 has 2 stock records (storeA=50, storeB=30)
        val p1Stock = result.filter { it.productId == product1.id }
        assertEquals(2, p1Stock.size, "Product1 should have stock in 2 stores")
        assertEquals(50, p1Stock.first { it.storeId == storeA }.quantity)
        assertEquals(30, p1Stock.first { it.storeId == storeB }.quantity)

        // Product2 has 1 stock record (storeA=100)
        val p2Stock = result.filter { it.productId == product2.id }
        assertEquals(1, p2Stock.size, "Product2 should have stock in 1 store")
        assertEquals(100, p2Stock.first().quantity)

        // Product3 has no stock records
        val p3Stock = result.filter { it.productId == product3.id }
        assertTrue(p3Stock.isEmpty(), "Product3 with no stock should not appear in results")
    }

    @Test
    @DisplayName("findByProductIdIn returns empty list for non-existent product IDs")
    fun `findByProductIdIn returns empty list for unknown product IDs`() {
        val unknownIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val result = stockRepository.findByProductIdIn(unknownIds)
        assertTrue(result.isEmpty(), "Should return empty list for unknown product IDs")
    }

    @Test
    @DisplayName("findByProductIdIn returns all stock for a single product across multiple stores")
    fun `findByProductIdIn returns all stock for single product across multiple stores`() {
        val ids = listOf(product1.id!!)
        val result = stockRepository.findByProductIdIn(ids)

        assertEquals(2, result.size, "Product1 should have stock in 2 stores")
        val quantities = result.map { it.quantity }
        assertTrue(quantities.contains(50), "Should contain stock quantity 50")
        assertTrue(quantities.contains(30), "Should contain stock quantity 30")
    }

    @Test
    @DisplayName("findByProductIdIn sums correctly for consolidated view")
    fun `findByProductIdIn enables correct stock consolidation`() {
        // This test simulates what ConsolidatedStockUseCase does:
        // batch-load stock for a set of product IDs, then group in-memory
        val ids = listOf(product1.id!!, product2.id!!, product3.id!!)
        val result = stockRepository.findByProductIdIn(ids)

        val groupedByProduct = result.groupBy { it.productId }
        val totalStockP1 = groupedByProduct[product1.id]?.sumOf { it.quantity } ?: 0
        val totalStockP2 = groupedByProduct[product2.id]?.sumOf { it.quantity } ?: 0
        val totalStockP3 = groupedByProduct[product3.id]?.sumOf { it.quantity } ?: 0

        assertEquals(80, totalStockP1, "Product1 total stock should be 50+30=80")
        assertEquals(100, totalStockP2, "Product2 total stock should be 100")
        assertEquals(0, totalStockP3, "Product3 has no stock")
    }
}
