package com.siga.inventory.controller

import com.siga.inventory.application.usecase.CreateProductRequest
import com.siga.inventory.application.usecase.CreateProductResponse
import com.siga.inventory.application.usecase.CreateProductUseCase
import com.siga.inventory.application.usecase.SearchProductsUseCase
import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.entity.Product as ProductEntity
import com.siga.inventory.repository.ProductRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional
import java.util.UUID

class ProductControllerTest : DescribeSpec({

    val productRepository = mockk<ProductRepository>()
    val createProductUseCase = mockk<CreateProductUseCase>()
    val searchProductsUseCase = mockk<SearchProductsUseCase>()
    val productRepositoryPort = mockk<ProductRepositoryPort>()
    val controller = ProductController(
        productRepository,
        createProductUseCase,
        searchProductsUseCase,
        productRepositoryPort
    )

    val productId = UUID.randomUUID()
    val companyId = UUID.randomUUID()
    val categoryId = UUID.randomUUID()
    val existingProductEntity = ProductEntity(
        id = productId,
        name = "Test Product",
        description = null,
        categoryId = null,
        barcode = null,
        unitPrice = BigDecimal("99.99"),
        isActive = true,
        commercialUserId = null,
        createdAt = null,
        updatedAt = null
    )

    beforeAny {
        clearAllMocks()
    }

    describe("ProductController") {

        describe("getAllProducts") {

            it("given existing products when getting all then should return 200 OK and list of products") {
                val products = listOf(existingProductEntity)
                every { productRepository.findAll() } returns products

                val response = controller.getAllProducts()

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }

            it("given no products when getting all then should return 200 OK and empty list") {
                every { productRepository.findAll() } returns emptyList()

                val response = controller.getAllProducts()

                response.statusCode shouldBe HttpStatus.OK
                response.body?.isEmpty() shouldBe true
            }
        }

        describe("getProductById") {

            it("given existing product when getting by id then should return 200 OK") {
                every { productRepository.findById(productId) } returns Optional.of(existingProductEntity)

                val response = controller.getProductById(productId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.name shouldBe "Test Product"
            }

            it("given non-existent product when getting by id then should return 404 Not Found") {
                every { productRepository.findById(productId) } returns Optional.empty()

                val response = controller.getProductById(productId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("getProductsByCompany") {

            it("given products for company when getting by company then should return 200 OK") {
                val products = listOf(existingProductEntity)
                every { productRepository.findByCommercialUserId(companyId) } returns products

                val response = controller.getProductsByCompany(companyId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }
        }

        describe("getProductsByCategory") {

            it("given products for category when getting by category then should return 200 OK") {
                val products = listOf(existingProductEntity)
                every { productRepository.findByCategoryId(categoryId) } returns products

                val response = controller.getProductsByCategory(categoryId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }
        }

        describe("getProductByBarcode") {

            it("given existing barcode when getting by barcode then should return 200 OK") {
                every { productRepository.findByBarcode("TEST-001") } returns existingProductEntity

                val response = controller.getProductByBarcode("TEST-001")

                response.statusCode shouldBe HttpStatus.OK
                response.body?.name shouldBe "Test Product"
            }

            it("given non-existent barcode when getting by barcode then should return 404 Not Found") {
                every { productRepository.findByBarcode("NONEXISTENT") } returns null

                val response = controller.getProductByBarcode("NONEXISTENT")

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        // --- Replaced createProduct endpoint (Phase 4.2) ---

        describe("createProduct") {

            it("given valid request with no duplicates when creating then should return 201 Created") {
                val req = CreateProductRequest(
                    name = "New Product",
                    sku = null,
                    categoryId = categoryId,
                    categoryName = "Test",
                    description = "A new product",
                    unitType = "UNIDAD",
                    barcode = null,
                    force = false
                )
                val resp = CreateProductResponse(
                    productId = productId,
                    sku = "NEW-001",
                    name = "New Product",
                    status = "ACTIVE",
                    warning = null
                )
                every { productRepositoryPort.findByNameLike("New Product") } returns emptyList()
                every { createProductUseCase.execute(req, 1L) } returns resp

                val response = controller.createProduct(req)
                val body = response.body as? CreateProductResponse

                response.statusCode shouldBe HttpStatus.CREATED
                body?.sku shouldBe "NEW-001"
                body?.status shouldBe "ACTIVE"
            }

            it("given duplicate found without force when creating then should return 409 Conflict") {
                val req = CreateProductRequest(
                    name = "Existing Product",
                    sku = null,
                    categoryId = categoryId,
                    categoryName = "Test",
                    description = null,
                    unitType = "UNIDAD",
                    barcode = null,
                    force = false
                )
                val existing = Product(
                    id = UUID.randomUUID(),
                    name = "Existing Product",
                    description = null,
                    categoryId = null,
                    barcode = null,
                    unitPrice = BigDecimal.ZERO,
                    isActive = true,
                    commercialUserId = null,
                    sku = "EX-001",
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                every { productRepositoryPort.findByNameLike("Existing Product") } returns listOf(existing)

                val response = controller.createProduct(req)

                response.statusCode shouldBe HttpStatus.CONFLICT
            }

            it("given duplicate found with force=true when creating then should return 201 Created") {
                val req = CreateProductRequest(
                    name = "Existing Product",
                    sku = null,
                    categoryId = categoryId,
                    categoryName = "Test",
                    description = null,
                    unitType = "UNIDAD",
                    barcode = null,
                    force = true
                )
                val existing = Product(
                    id = UUID.randomUUID(),
                    name = "Existing Product",
                    description = null,
                    categoryId = null,
                    barcode = null,
                    unitPrice = BigDecimal.ZERO,
                    isActive = true,
                    commercialUserId = null,
                    sku = "EX-001",
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                val resp = CreateProductResponse(
                    productId = productId,
                    sku = "EX-002",
                    name = "Existing Product",
                    status = "ACTIVE",
                    warning = null
                )
                every { productRepositoryPort.findByNameLike("Existing Product") } returns listOf(existing)
                every { createProductUseCase.execute(req, 1L) } returns resp

                val response = controller.createProduct(req)
                val body = response.body as? CreateProductResponse

                response.statusCode shouldBe HttpStatus.CREATED
                body?.sku shouldBe "EX-002"
            }
        }

        describe("updateProduct") {

            it("given existing product when updating then should return 200 OK") {
                every { productRepository.existsById(productId) } returns true
                every { productRepository.save(any()) } answers { firstArg() }

                val response = controller.updateProduct(productId, existingProductEntity)

                response.statusCode shouldBe HttpStatus.OK
            }

            it("given non-existent product when updating then should return 404 Not Found") {
                every { productRepository.existsById(productId) } returns false

                val response = controller.updateProduct(productId, existingProductEntity)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("deleteProduct") {

            it("given existing product when deleting then should return 204 No Content") {
                every { productRepository.existsById(productId) } returns true
                every { productRepository.deleteById(productId) } just runs

                val response = controller.deleteProduct(productId)

                response.statusCode shouldBe HttpStatus.NO_CONTENT
            }

            it("given non-existent product when deleting then should return 404 Not Found") {
                every { productRepository.existsById(productId) } returns false

                val response = controller.deleteProduct(productId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        // --- New search endpoint (Phase 4.2) ---

        describe("searchProducts") {

            it("given valid query when searching then should return 200 OK with results") {
                val product = Product(
                    id = productId,
                    name = "Café Instantáneo 200g",
                    description = null,
                    categoryId = null,
                    barcode = null,
                    unitPrice = BigDecimal.valueOf(3200),
                    isActive = true,
                    commercialUserId = null,
                    sku = "CAF-001",
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                val page: Page<Product> = PageImpl(listOf(product))
                every { searchProductsUseCase.execute("cafe", 0, 20) } returns page

                val response = controller.searchProducts("cafe", 0, 20)
                val body = response.body as? SearchResponse

                response.statusCode shouldBe HttpStatus.OK
                body?.products?.size shouldBe 1
                body?.products?.first()?.sku shouldBe "CAF-001"
            }

            it("given query with no results when searching then should return 200 OK with empty list") {
                val page: Page<Product> = PageImpl(emptyList())
                every { searchProductsUseCase.execute("xyzzy", 0, 20) } returns page

                val response = controller.searchProducts("xyzzy", 0, 20)
                val body = response.body as? SearchResponse

                response.statusCode shouldBe HttpStatus.OK
                body?.products?.isEmpty() shouldBe true
                body?.totalElements shouldBe 0
            }

            it("given too short query when searching then should return 400 Bad Request") {
                every { searchProductsUseCase.execute("a", 0, 20) } throws IllegalArgumentException("Search query must be at least 2 characters")

                val response = controller.searchProducts("a", 0, 20)

                response.statusCode shouldBe HttpStatus.BAD_REQUEST
            }
        }

        // --- New duplicate-check endpoint (Phase 4.2) ---

        describe("duplicateCheck") {

            it("given name with duplicates when checking then should return 200 OK with duplicate list") {
                val domainProduct = Product(
                    id = productId,
                    name = "Galleta Surtida 250g",
                    description = null,
                    categoryId = null,
                    barcode = null,
                    unitPrice = BigDecimal.ZERO,
                    isActive = true,
                    commercialUserId = null,
                    sku = "GAL-001",
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                every { productRepositoryPort.findByNameLike("Galleta") } returns listOf(domainProduct)

                val response = controller.duplicateCheck("Galleta")
                val body = response.body

                response.statusCode shouldBe HttpStatus.OK
                body?.duplicates?.size shouldBe 1
                body?.duplicates?.first()?.sku shouldBe "GAL-001"
            }

            it("given name with no duplicates when checking then should return 200 OK with empty list") {
                every { productRepositoryPort.findByNameLike("NewProduct") } returns emptyList()

                val response = controller.duplicateCheck("NewProduct")
                val body = response.body

                response.statusCode shouldBe HttpStatus.OK
                body?.duplicates?.isEmpty() shouldBe true
            }
        }
    }
})
