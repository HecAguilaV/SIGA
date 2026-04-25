package com.siga.inventory.controller

import com.siga.inventory.entity.Product
import com.siga.inventory.repository.ProductRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.math.BigDecimal

class ProductControllerTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    private lateinit var controller: ProductController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        controller = ProductController(productRepository)
    }

    @Test
    fun `get all products returns 200 and list of products`() {
        val products = listOf(
            Product(id = 1, name = "Product 1", unitPrice = BigDecimal("10.0")),
            Product(id = 2, name = "Product 2", unitPrice = BigDecimal("20.0"))
        )
        `when`(productRepository.findAll()).thenReturn(products)

        val response = controller.getAllProducts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(2, response.body?.size)
    }

    @Test
    fun `get all products returns empty list when no products exist`() {
        `when`(productRepository.findAll()).thenReturn(emptyList())

        val response = controller.getAllProducts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body!!.isEmpty())
    }
}