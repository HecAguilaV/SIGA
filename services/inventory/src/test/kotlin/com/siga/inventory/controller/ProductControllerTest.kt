package com.siga.inventory.controller

import com.siga.inventory.entity.Product
import com.siga.inventory.repository.ProductRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.math.BigDecimal

class ProductControllerTest : DescribeSpec({

    val productRepository = mockk<ProductRepository>()
    val controller = ProductController(productRepository)

    describe("ProductController") {

        describe("getAllProducts") {

            it("given existing products when getting all then should return 200 OK and list of products") {
                // Given
                val products = listOf(
                    Product(id = 1, name = "Product 1", unitPrice = BigDecimal("10.0")),
                    Product(id = 2, name = "Product 2", unitPrice = BigDecimal("20.0"))
                )
                every { productRepository.findAll() } returns products

                // When
                val response = controller.getAllProducts()

                // Then
                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 2
            }

            it("given no products when getting all then should return 200 OK and empty list") {
                // Given
                every { productRepository.findAll() } returns emptyList()

                // When
                val response = controller.getAllProducts()

                // Then
                response.statusCode shouldBe HttpStatus.OK
                response.body?.isEmpty() shouldBe true
            }
        }
    }
})