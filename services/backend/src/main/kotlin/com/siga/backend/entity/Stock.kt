package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "STOCK",
    schema = "siga_inventario",
    uniqueConstraints = [UniqueConstraint(columnNames = ["producto_id", "local_id"])]
)
class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "producto_id", nullable = false)
    var productoId: Int,

    @Column(name = "local_id", nullable = false)
    var localId: Int,

    @Column(nullable = false)
    var cantidad: Int = 0,

    @Column(name = "cantidad_minima", nullable = false)
    var cantidadMinima: Int = 0,

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stock) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Stock(id=$id, productoId=$productoId, localId=$localId, cantidad=$cantidad)"

    fun copy(
        id: Int = this.id,
        productoId: Int = this.productoId,
        localId: Int = this.localId,
        cantidad: Int = this.cantidad,
        cantidadMinima: Int = this.cantidadMinima,
        fechaActualizacion: Instant = this.fechaActualizacion
    ): Stock = Stock(id, productoId, localId, cantidad, cantidadMinima, fechaActualizacion)
}

