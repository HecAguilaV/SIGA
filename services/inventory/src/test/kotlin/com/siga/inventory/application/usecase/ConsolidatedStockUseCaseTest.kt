package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class ConsolidatedStockUseCaseTest : DescribeSpec({

    val productPort = mockk<ProductRepositoryPort>()
    val stockPort = mockk<StockRepositoryPort>()
    val useCase = ConsolidatedStockUseCase(productPort, stockPort)

    val tenantId = UUID.randomUUID()
    val storeId1 = UUID.randomUUID()
    val storeId2 = UUID.randomUUID()
    val productId1 = UUID.randomUUID()
    val productId2 = UUID.randomUUID()
    val now = Instant.now()

    fun makeProduct(id: UUID, name: String, sku: String? = null): Product =
        Product(
            id = id,
            name = name,
            description = null,
            categoryId = null,
            barcode = null,
            unitPrice = BigDecimal.ZERO,
            isActive = true,
            commercialUserId = tenantId,
            sku = sku,
            unitType = null,
            createdAt = now,
            updatedAt = now
        )

    describe("execute") {

        it("should consolidate stock across all stores when no storeId filter") {
            val product = makeProduct(productId1, "Servilleta 100u", "SER-001")
            val stocks = listOf(
                Stock(productId1, storeId1, 20, now),
                Stock(productId1, storeId2, 5, now)
            )

            every { stockPort.findAll() } returns stocks
            every { productPort.findById(productId1) } returns product

            val result = useCase.execute(storeId = null, page = 0, size = 50)

            result.products.size shouldBe 1
            result.products[0].productId shouldBe productId1
            result.products[0].productName shouldBe "Servilleta 100u"
            result.products[0].sku shouldBe "SER-001"
            result.products[0].totalStock shouldBe 25
            result.products[0].stores.size shouldBe 2
            result.totalElements shouldBe 1
        }

        it("should filter by store when storeId is provided") {
            val product = makeProduct(productId1, "Servilleta 100u", "SER-001")
            val stocks = listOf(
                Stock(productId1, storeId1, 20, now),
                Stock(productId1, storeId2, 5, now)
            )

            every { stockPort.findAll() } returns stocks
            every { productPort.findById(productId1) } returns product

            val result = useCase.execute(storeId = storeId1, page = 0, size = 50)

            result.products.size shouldBe 1
            result.products[0].totalStock shouldBe 20
            result.products[0].stores.size shouldBe 1
            result.products[0].stores[0].storeId shouldBe storeId1
            result.products[0].stores[0].quantity shouldBe 20
        }

        it("should return empty list when no stock exists for store") {
            every { stockPort.findAll() } returns emptyList()

            val result = useCase.execute(storeId = storeId1, page = 0, size = 50)

            result.products.size shouldBe 0
            result.totalElements shouldBe 0
        }

        it("should handle pagination correctly") {
            val product = makeProduct(productId1, "Product A", "PRD-001")
            val stocks = listOf(
                Stock(productId1, storeId1, 10, now)
            )

            every { stockPort.findAll() } returns stocks
            every { productPort.findById(productId1) } returns product

            val result = useCase.execute(storeId = null, page = 0, size = 1)

            result.products.size shouldBe 1
            result.page shouldBe 0
            result.size shouldBe 1
            result.totalPages shouldBe 1
        }

        it("should aggregate multiple products with correct per-store breakdown") {
            val stocks = listOf(
                Stock(productId1, storeId1, 10, now),
                Stock(productId1, storeId2, 15, now),
                Stock(productId2, storeId1, 30, now)
            )

            every { stockPort.findAll() } returns stocks
            every { productPort.findById(productId1) } returns makeProduct(productId1, "Product A", "A-001")
            every { productPort.findById(productId2) } returns makeProduct(productId2, "Product B", "B-001")

            val result = useCase.execute(storeId = null, page = 0, size = 50)

            result.products.size shouldBe 2
            result.products.find { it.productId == productId1 }?.totalStock shouldBe 25
            result.products.find { it.productId == productId2 }?.totalStock shouldBe 30
            result.totalElements shouldBe 2
        }
    }
})
