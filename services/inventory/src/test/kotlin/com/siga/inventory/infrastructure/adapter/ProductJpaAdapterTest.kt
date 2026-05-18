package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Product
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [ProductJpaAdapter].
 * Verifies Product persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = ["classpath:create-unaccent-alias.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class ProductJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: ProductJpaAdapter

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("ProductJpaAdapter") {

            it("save and find by id") {
                val product = Product(
                    id = UUID.randomUUID(),
                    name = "Laptop Pro 16",
                    description = "High performance laptop",
                    categoryId = UUID.randomUUID(),
                    barcode = "LAP-001",
                    unitPrice = BigDecimal("1500.00"),
                    isActive = true,
                    commercialUserId = UUID.randomUUID(),
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )

                val saved = adapter.save(product)
                saved.id shouldBe product.id
                saved.name shouldBe "Laptop Pro 16"
                saved.unitPrice shouldBe BigDecimal("1500.00")
                saved.barcode shouldBe "LAP-001"

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.name shouldBe "Laptop Pro 16"
            }

            it("findById returns null when product does not exist") {
                val found = adapter.findById(UUID.randomUUID())
                found shouldBe null
            }

            it("findByBarcode returns product by barcode") {
                val barcode = "BAR-UNIQUE-001"
                val product = Product(
                    id = UUID.randomUUID(), name = "Barcode Test", description = null,
                    categoryId = null, barcode = barcode,
                    unitPrice = BigDecimal("100.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                adapter.save(product)

                val found = adapter.findByBarcode(barcode)
                found shouldNotBe null
                found?.id shouldBe product.id
            }

            it("findByBarcode returns null when barcode does not exist") {
                val found = adapter.findByBarcode("NONEXISTENT")
                found shouldBe null
            }

            it("findByCommercialUserId returns products for a user") {
                val userId = UUID.randomUUID()
                val product = Product(
                    id = UUID.randomUUID(), name = "User Product", description = null,
                    categoryId = null, barcode = null,
                    unitPrice = BigDecimal("50.00"), isActive = true,
                    commercialUserId = userId, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                adapter.save(product)

                val products = adapter.findByCommercialUserId(userId)
                products.any { it.id == product.id } shouldBe true
            }

            it("findByCategoryId returns products by category") {
                val categoryId = UUID.randomUUID()
                val product = Product(
                    id = UUID.randomUUID(), name = "Category Product", description = null,
                    categoryId = categoryId, barcode = null,
                    unitPrice = BigDecimal("75.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                adapter.save(product)

                val products = adapter.findByCategoryId(categoryId)
                products.any { it.id == product.id } shouldBe true
            }

            it("save product with null optional fields") {
                val product = Product(
                    id = UUID.randomUUID(), name = "Minimal Product",
                    description = null, categoryId = null, barcode = null,
                    unitPrice = BigDecimal("10.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val saved = adapter.save(product)
                saved.description shouldBe null
                saved.categoryId shouldBe null
                saved.barcode shouldBe null
            }

            it("update product by saving with same id") {
                val product = Product(
                    id = UUID.randomUUID(), name = "Original Name",
                    description = "Original desc", categoryId = null, barcode = "ORIG-BAR",
                    unitPrice = BigDecimal("100.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val saved = adapter.save(product)

                val updated = saved.copy(
                    name = "Updated Name", description = "Updated desc",
                    unitPrice = BigDecimal("200.00"), isActive = false
                )
                adapter.save(updated)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.name shouldBe "Updated Name"
                found?.unitPrice shouldBe BigDecimal("200.00")
                found?.isActive shouldBe false
            }

            it("findByNameLike returns products matching name fragment") {
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Galleta Surtida", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("10.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Galleta Salada", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("12.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Arroz 1kg", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("5.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))

                val result = adapter.findByNameLike("Galleta")
                result.size shouldBe 2
                result.all { it.name.startsWith("Galleta") } shouldBe true
            }

            it("findByNameLike returns empty list when no matches") {
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Leche Descremada", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("3.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))

                val result = adapter.findByNameLike("XYZZZ")
                result shouldBe emptyList()
            }

            it("search returns paginated results") {
                // Use non-accented names since H2 ILIKE is case-insensitive but NOT accent-insensitive
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Cafe Instantaneo 200g", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("15.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Cafe Molido 500g", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("20.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))
                adapter.save(Product(
                    id = UUID.randomUUID(), name = "Te Verde", description = null,
                    categoryId = null, barcode = null, unitPrice = BigDecimal("8.00"),
                    isActive = true, commercialUserId = null, sku = null, unitType = null,
                    createdAt = Instant.now(), updatedAt = Instant.now()
                ))

                val result = adapter.search("cafe", 0, 10)
                result.totalElements shouldBe 2
                result.content.size shouldBe 2
                result.content.all { it.name.contains("Cafe", ignoreCase = true) } shouldBe true
            }

            it("search returns empty page when no matches") {
                val result = adapter.search("NONEXISTENTPRODUCT", 0, 10)
                result.totalElements shouldBe 0
                result.content shouldBe emptyList()
            }
        }
    }
}
