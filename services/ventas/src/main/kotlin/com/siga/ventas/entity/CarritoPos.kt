package com.siga.ventas.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "CARRITO_POS", schema = "siga_saas")
class CarritoPos(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "usuario_id", nullable = false)
    val usuarioId: Int,

    @Column(name = "local_id", nullable = false)
    val localId: Int,

    @Column(name = "producto_id", nullable = false)
    val productoId: Int,

    @Column(nullable = false)
    var cantidad: Int,

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    val precioUnitario: BigDecimal,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CarritoPos) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "CarritoPos(id=$id, productoId=$productoId, cantidad=$cantidad)"
}
