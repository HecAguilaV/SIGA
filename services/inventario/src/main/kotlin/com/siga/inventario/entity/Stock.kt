package com.siga.inventario.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "STOCK", schema = "siga_saas",
    uniqueConstraints = [UniqueConstraint(columnNames = ["producto_id", "local_id"])]
)
class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "producto_id", nullable = false)
    val productoId: Int,

    @Column(name = "local_id", nullable = false)
    val localId: Int,

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
}
