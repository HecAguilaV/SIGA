package com.siga.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "DETALLES_VENTA", schema = "siga_ventas")
class DetalleVenta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "venta_id", nullable = false)
    var ventaId: Int,

    @Column(name = "producto_id", nullable = false)
    var productoId: Int,

    @Column(nullable = false)
    var cantidad: Int,

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    var precioUnitario: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DetalleVenta) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "DetalleVenta(id=$id, ventaId=$ventaId, productoId=$productoId)"

    fun copy(
        id: Int = this.id,
        ventaId: Int = this.ventaId,
        productoId: Int = this.productoId,
        cantidad: Int = this.cantidad,
        precioUnitario: BigDecimal = this.precioUnitario,
        subtotal: BigDecimal = this.subtotal
    ): DetalleVenta = DetalleVenta(id, ventaId, productoId, cantidad, precioUnitario, subtotal)
}