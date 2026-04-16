package com.siga.inventario.controller

import com.siga.inventario.entity.Producto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ProductoControllerTest {

    private val controller = ProductoController()

    @Test
    fun `listarProductos retorna 401 si no hay X-Tenant-Id`() {
        val response = controller.listarProductos(null)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `listarProductos retorna 200 con tenant valido`() {
        val response = controller.listarProductos("tenant-42")

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
    }

    @Test
    fun `listarProductos retorna lista vacia por defecto`() {
        val response = controller.listarProductos("tenant-1")

        assertTrue(response.body!!.isEmpty())
    }
}
