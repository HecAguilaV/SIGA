package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class SearchProductsUseCaseTest : DescribeSpec({

    val productPort = mockk<ProductRepositoryPort>()
    val useCase = SearchProductsUseCase(productPort)

    val now = Instant.now()
    val productId = UUID.randomUUID()

    fun makeProduct(name: String): Product = Product(
        id = productId,
        name = name,
        description = null,
        categoryId = null,
        barcode = null,
        unitPrice = BigDecimal.TEN,
        isActive = true,
        commercialUserId = UUID.randomUUID(),
        sku = null,
        unitType = null,
        createdAt = now,
        updatedAt = now
    )

    describe("execute") {

        it("should delegate to port search when query is at least 2 characters") {
            val products = listOf(makeProduct("Café Instantáneo 200g"))
            val page: Page<Product> = PageImpl(products)

            every { productPort.search("cafe", 0, 20) } returns page

            val result = useCase.execute("cafe", 0, 20)

            result.content.size shouldBe 1
            result.content[0].name shouldBe "Café Instantáneo 200g"
        }

        it("should return empty page when no results match") {
            val page: Page<Product> = PageImpl(emptyList())

            every { productPort.search("xyzzy", 0, 20) } returns page

            val result = useCase.execute("xyzzy", 0, 20)

            result.content.size shouldBe 0
            result.totalElements shouldBe 0
        }

        it("should throw IllegalArgumentException for query shorter than 2 characters") {
            val exception = shouldThrow<IllegalArgumentException> {
                useCase.execute("a", 0, 20)
            }
            exception.message shouldBe "Search query must be at least 2 characters"
        }

        it("should handle pagination parameters correctly") {
            val products = (1..25).map { makeProduct("Product $it") }
            val page: Page<Product> = PageImpl(products.subList(0, 10))

            every { productPort.search("product", 1, 10) } returns page

            val result = useCase.execute("product", 1, 10)

            result.content.size shouldBe 10
        }
    }
})
