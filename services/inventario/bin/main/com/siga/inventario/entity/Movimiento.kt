package com.siga.inventario.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "MOVIMIENTOS", schema = "siga_inventario")
class Movimiento(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "producto_id", nullable = false)
    val productoId: Int,

    @Column(name = "local_id", nullable = false)
    val localId: Int,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val tipo: TipoMovimiento,

    @Column(nullable = false)
    val cantidad: Int,

    @Column(name = "cantidad_anterior", nullable = false)
    val cantidadAnterior: Int,

    @Column(name = "cantidad_nueva", nullable = false)
    val cantidadNueva: Int,

    @Column(name = "usuario_id")
    val usuarioId: Int? = null,

    @Column(name = "venta_id")
    val ventaId: Int? = null,

    @Column(columnDefinition = "TEXT")
    val observaciones: String? = null,

    @Column(nullable = false)
    val fecha: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Movimiento) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Movimiento(id=$id, tipo=$tipo, productoId=$productoId, cantidad=$cantidad)"
}
