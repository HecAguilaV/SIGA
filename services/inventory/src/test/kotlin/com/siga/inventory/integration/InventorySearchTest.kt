package com.siga.inventory.integration

import com.siga.inventory.entity.Product
import com.siga.inventory.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Integration test for ILIKE + unaccent product name search via [ProductRepository.search].
 *
 * Verifies that the `f_unaccent` JPQL function combined with ILIKE enables
 * case-insensitive and accent-insensitive partial name matching.
 *
 * NOTE: H2 does not natively support unaccent. The test uses a compatibility alias
 * (see [create-unaccent-alias.sql]) that passes strings through unchanged, so
 * accent-insensitive matching cannot be fully verified in H2. However, the JPQL
 * query structure, ILIKE case-insensitive partial matching, and pagination are
 * validated here. Full accent-insensitive verification requires PostgreSQL.
 *
 * @see ProductRepository.search
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Sql(scripts = ["classpath:create-unaccent-alias.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("InventorySearchTest: ILIKE + unaccent product search")
class InventorySearchTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        // Products with accented names to test accent-insensitive search
        productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Café Instantáneo 200g",
                description = null,
                categoryId = null,
                barcode = "CAFE-001",
                sku = "CAF-001",
                unitPrice = BigDecimal("15.00"),
                isActive = true,
                commercialUserId = null,
                createdAt = now,
                updatedAt = now
            )
        )
        productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Galleta Surtida 250g",
                description = null,
                categoryId = null,
                barcode = "GAL-001",
                sku = "GAL-001",
                unitPrice = BigDecimal("10.00"),
                isActive = true,
                commercialUserId = null,
                createdAt = now,
                updatedAt = now
            )
        )
        productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Galleta Salada 100g",
                description = null,
                categoryId = null,
                barcode = "GAL-002",
                sku = "GAL-002",
                unitPrice = BigDecimal("8.00"),
                isActive = true,
                commercialUserId = null,
                createdAt = now,
                updatedAt = now
            )
        )
        productRepository.save(
            Product(
                id = UUID.randomUUID(),
                name = "Arroz Integral 1kg",
                description = null,
                categoryId = null,
                barcode = "ARZ-001",
                sku = "ARZ-001",
                unitPrice = BigDecimal("5.00"),
                isActive = true,
                commercialUserId = null,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    @Test
    @DisplayName("ILIKE search matches partial words (case-insensitive)")
    fun `ILIKE search matches partial words`() {
        // Search for "INSTANT" (uppercase) should match "Café Instantáneo 200g" (mixed case)
        val result = productRepository.search("INSTANT", PageRequest.of(0, 20))
        assertEquals(1, result.totalElements, "Should find 1 product matching 'INSTANT'")
        assertTrue(
            result.content.any { it.name == "Café Instantáneo 200g" },
            "Result should include 'Café Instantáneo 200g'"
        )
    }

    @Test
    @DisplayName("Case-insensitive search matches regardless of case")
    fun `case insensitive search matches regardless of case`() {
        // "GALLETA" (uppercase) should match "Galleta" (mixed case) via ILIKE
        val result = productRepository.search("GALLETA", PageRequest.of(0, 20))
        assertEquals(2, result.totalElements, "Should find 2 products matching 'GALLETA'")
        assertTrue(
            result.content.any { it.name == "Galleta Surtida 250g" },
            "Result should include 'Galleta Surtida 250g'"
        )
    }

    @Test
    @DisplayName("Partial search should return all matching products")
    fun `partial search returns all matching products`() {
        // Search for "galle" should return both Galleta products
        val result = productRepository.search("galle", PageRequest.of(0, 20))
        assertEquals(2, result.totalElements, "Should find 2 products matching 'galle'")
        assertTrue(
            result.content.any { it.name == "Galleta Surtida 250g" },
            "Result should include 'Galleta Surtida 250g'"
        )
        assertTrue(
            result.content.any { it.name == "Galleta Salada 100g" },
            "Result should include 'Galleta Salada 100g'"
        )
    }

    @Test
    @DisplayName("Search with non-matching query should return empty results")
    fun `non matching query returns empty results`() {
        val result = productRepository.search("xyzzy", PageRequest.of(0, 20))
        assertEquals(0, result.totalElements, "Should find 0 products for non-matching query")
        assertTrue(result.content.isEmpty(), "Result content should be empty")
    }

    @Test
    @DisplayName("Search with partial word matches interior substring")
    fun `partial word search matches interior substring`() {
        // "táneo" should match "Café Instantáneo 200g" (in H2, with pass-through unaccent)
        // NOTE: In H2, f_unaccent is a pass-through, so the accent in "táneo" must
        // match the accent in "Instantáneo" for the ILIKE to work.
        val result = productRepository.search("táneo", PageRequest.of(0, 20))
        assertEquals(1, result.totalElements, "Should find 1 product matching 'táneo'")
        assertTrue(
            result.content.any { it.name == "Café Instantáneo 200g" },
            "Result should include 'Café Instantáneo 200g'"
        )
    }

    @Test
    @DisplayName("Search pagination returns correct sub-sets")
    fun `search pagination returns correct subsets`() {
        // Should find 2 "Galleta" products
        val page1 = productRepository.search("Galleta", PageRequest.of(0, 1))
        assertEquals(2, page1.totalElements, "Total elements should be 2")
        assertEquals(1, page1.content.size, "Page 0 with size 1 should have 1 element")

        val page2 = productRepository.search("Galleta", PageRequest.of(1, 1))
        assertEquals(1, page2.content.size, "Page 1 with size 1 should have 1 element")
        assertTrue(
            page1.content.first().name != page2.content.first().name,
            "Page 1 and Page 2 should contain different products"
        )
    }
}
