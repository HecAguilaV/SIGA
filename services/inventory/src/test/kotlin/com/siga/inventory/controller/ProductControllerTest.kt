package com.siga.inventory.controller

import com.siga.inventory.entity.Product
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ProductControllerTest {

    private val controller = ProductController()

    @Test
    fun `list products returns 401 if no X-Tenant-Id`() {
        val response = controller.listProducts(null)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `list products returns 200 with valid tenant`() {
        val response = controller.listProducts("tenant-42")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `list products returns empty list by default`() {
        val response = controller.listProducts("tenant-1")

        assertTrue(response.body!!.isEmpty())
    }
}