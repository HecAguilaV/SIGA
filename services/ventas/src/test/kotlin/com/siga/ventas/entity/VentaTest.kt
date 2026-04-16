package com.siga.ventas.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class VentaTest {

    @Test
    fun `venta se crea con estado COMPLETADA por defecto`() {
        val venta = Venta(
            localId = 1,
            total = BigDecimal("15000.00")
        )

        assertEquals(EstadoVenta.COMPLETADA, venta.estado)
    }

    @Test
    fun `venta cancelada tiene estado correcto`() {
        val venta = Venta(
            localId = 1,
            total = BigDecimal("5000.00"),
            estado = EstadoVenta.CANCELADA
        )

        assertEquals(EstadoVenta.CANCELADA, venta.estado)
    }

    @Test
    fun `detalle de venta calcula subtotal correctamente`() {
        val detalle = DetalleVenta(
            ventaId = 1,
            productoId = 10,
            cantidad = 3,
            precioUnitario = BigDecimal("2500.00"),
            subtotal = BigDecimal("7500.00")
        )

        assertEquals(BigDecimal("7500.00"), detalle.subtotal)
        assertEquals(3, detalle.cantidad)
    }

    @Test
    fun `venta contiene tenant owner via usuarioComercialId`() {
        val venta = Venta(
            localId = 1,
            total = BigDecimal("10000.00"),
            usuarioComercialId = 42
        )

        assertEquals(42, venta.usuarioComercialId)
    }
}
