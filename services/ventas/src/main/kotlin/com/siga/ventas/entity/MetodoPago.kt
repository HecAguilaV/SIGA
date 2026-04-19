package com.siga.ventas.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "METODOS_PAGO", schema = "siga_saas")
class MetodoPago(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true, length = 50)
    var nombre: String,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MetodoPago) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "MetodoPago(id=$id, nombre=$nombre)"
}
