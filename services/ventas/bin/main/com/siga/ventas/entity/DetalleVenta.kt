package com.siga.ventas.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "DETALLES_VENTA", schema = "siga_saas")
class DetalleVenta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "venta_id", nullable = false)
    val ventaId: Int,

    @Column(name = "producto_id", nullable = false)
    val productoId: Int,

    @Column(nullable = false)
    val cantidad: Int,

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    val precioUnitario: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val subtotal: BigDecimal
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DetalleVenta) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "DetalleVenta(id=$id, ventaId=$ventaId, productoId=$productoId, cantidad=$cantidad)"
}
