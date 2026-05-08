package com.siga.inventory.controller

import com.siga.inventory.entity.Product
import com.siga.inventory.repository.ProductRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

class ProductControllerTest : DescribeSpec({

    val productRepository = mockk<ProductRepository>()
    val controller = ProductController(productRepository)

    val productId = UUID.randomUUID()
    val companyId = UUID.randomUUID()
    val categoryId = UUID.randomUUID()
    val existingProduct = Product(
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

    describe("ProductController") {

        describe("getAllProducts") {

            it("given existing products when getting all then should return 200 OK and list of products") {
                val products = listOf(existingProduct)
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
                every { productRepository.findById(productId) } returns Optional.of(existingProduct)

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
                val products = listOf(existingProduct)
                every { productRepository.findByCommercialUserId(companyId) } returns products

                val response = controller.getProductsByCompany(companyId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }
        }

        describe("getProductsByCategory") {

            it("given products for category when getting by category then should return 200 OK") {
                val products = listOf(existingProduct)
                every { productRepository.findByCategoryId(categoryId) } returns products

                val response = controller.getProductsByCategory(categoryId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }
        }

        describe("getProductByBarcode") {

            it("given existing barcode when getting by barcode then should return 200 OK") {
                every { productRepository.findByBarcode("TEST-001") } returns existingProduct

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

        describe("createProduct") {

            it("given valid product when creating then should return 201 Created") {
                every { productRepository.save(any()) } answers { firstArg() }

                val response = controller.createProduct(existingProduct)

                response.statusCode shouldBe HttpStatus.CREATED
            }
        }

        describe("updateProduct") {

            it("given existing product when updating then should return 200 OK") {
                every { productRepository.existsById(productId) } returns true
                every { productRepository.save(any()) } answers { firstArg() }

                val response = controller.updateProduct(productId, existingProduct)

                response.statusCode shouldBe HttpStatus.OK
            }

            it("given non-existent product when updating then should return 404 Not Found") {
                every { productRepository.existsById(productId) } returns false

                val response = controller.updateProduct(productId, existingProduct)

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
    }
})
