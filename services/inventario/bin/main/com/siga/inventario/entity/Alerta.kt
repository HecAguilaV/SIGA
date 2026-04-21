package com.siga.inventario.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "ALERTAS", schema = "siga_inventario")
class Alerta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val tipo: TipoAlerta,

    @Column(name = "producto_id")
    val productoId: Int? = null,

    @Column(name = "local_id")
    val localId: Int? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    val mensaje: String,

    @Column(nullable = false)
    var leida: Boolean = false,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Alerta) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Alerta(id=$id, tipo=$tipo, leida=$leida)"
}
