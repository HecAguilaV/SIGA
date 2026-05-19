package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.domain.service.SkuGenerator
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class CreateProductUseCaseTest : DescribeSpec({

    val productPort = mockk<ProductRepositoryPort>()
    val skuGenerator = mockk<SkuGenerator>()
    val useCase = CreateProductUseCase(productPort, skuGenerator)

    val tenantId = 1L
    val now = Instant.now()
    val existingId = UUID.randomUUID()

    beforeEach {
        clearAllMocks()
    }

    fun stubProduct(
        id: UUID = UUID.randomUUID(),
        name: String = "Existing Product",
        sku: String? = null
    ): Product = Product(
        id = id,
        name = name,
        description = null,
        categoryId = null,
        barcode = null,
        unitPrice = BigDecimal.TEN,
        isActive = true,
        commercialUserId = UUID.randomUUID(),
        sku = sku,
        unitType = "UNIDAD",
        createdAt = now,
        updatedAt = now
    )

    describe("execute") {

        it("should create product with auto-generated SKU when sku is null") {
            val request = CreateProductRequest(
                name = "Galleta Surtida 250g",
                sku = null,
                categoryId = null,
                categoryName = null,
                description = null,
                unitType = "UNIDAD",
                force = false
            )

            every { productPort.findByNameLike("Galleta Surtida 250g") } returns emptyList()
            every { skuGenerator.nextSku(tenantId, null) } returns "GAL-0001"
            every { productPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request, tenantId)

            result.productId shouldNotBe null
            result.sku shouldBe "GAL-0001"
            verify { productPort.save(match { it.name == "Galleta Surtida 250g" && it.sku == "GAL-0001" }) }
        }

        it("should use category name for SKU prefix when categoryName is provided") {
            val request = CreateProductRequest(
                name = "Galleta Surtida 250g",
                sku = null,
                categoryId = UUID.randomUUID(),
                categoryName = "Galletas",
                description = null,
                unitType = "UNIDAD",
                force = false
            )

            every { productPort.findByNameLike("Galleta Surtida 250g") } returns emptyList()
            every { skuGenerator.nextSku(tenantId, "Galletas") } returns "GAL-0001"
            every { productPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request, tenantId)

            result.sku shouldBe "GAL-0001"
            verify { skuGenerator.nextSku(tenantId, "Galletas") }
        }

        it("should create product with provided SKU when sku is not null") {
            val request = CreateProductRequest(
                name = "Galleta Surtida 250g",
                sku = "MAN-0001",
                categoryId = null,
                categoryName = null,
                description = null,
                unitType = "UNIDAD",
                force = false
            )

            every { productPort.findByNameLike("Galleta Surtida 250g") } returns emptyList()
            every { productPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request, tenantId)

            result.sku shouldBe "MAN-0001"
            verify(exactly = 0) { skuGenerator.nextSku(any(), any()) }
        }

        it("should detect duplicate when similar name exists and return warning") {
            val existing = stubProduct(id = existingId, name = "Galleta Surtida 250g", sku = "GAL-0001")
            val request = CreateProductRequest(
                name = "Galletas Surtidas 250g",
                sku = null,
                categoryId = null,
                categoryName = null,
                description = null,
                unitType = "UNIDAD",
                force = false
            )

            every { productPort.findByNameLike("Galletas Surtidas 250g") } returns listOf(existing)
            every { skuGenerator.nextSku(tenantId, null) } returns "GAL-0002"
            every { productPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request, tenantId)

            result.warning shouldNotBe null
            result.warning!!.existingProductId shouldBe existingId
            result.warning!!.existingProductName shouldBe "Galleta Surtida 250g"
            result.warning!!.existingSku shouldBe "GAL-0001"
            result.sku shouldBe "GAL-0002"
        }

        it("should not detect duplicate when name does not match any existing product") {
            val request = CreateProductRequest(
                name = "Producto Único",
                sku = null,
                categoryId = null,
                categoryName = null,
                description = null,
                unitType = "UNIDAD",
                force = false
            )

            every { productPort.findByNameLike("Producto Único") } returns emptyList()
            every { skuGenerator.nextSku(tenantId, null) } returns "PRD-0001"
            every { productPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request, tenantId)

            result.warning shouldBe null
        }

        it("should create product with force=true even when duplicate exists") {
            val existing = stubProduct(id = existingId, name = "Galleta Surtida 250g", sku = "GAL-0001")
            val request = CreateProductRequest(
                name = "Galletas Surtidas 250g",
                sku = null,
                categoryId = null,
                categoryName = null,
                description = null,
                unitType = "UNIDAD",
                force = true
            )

            every { productPort.findByNameLike("Galletas Surtidas 250g") } returns listOf(existing)
            every { skuGenerator.nextSku(tenantId, null) } returns "GAL-0002"
            every { productPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request, tenantId)

            result.sku shouldBe "GAL-0002"
            result.warning shouldNotBe null // Still warns, but allows creation
        }
    }
})
