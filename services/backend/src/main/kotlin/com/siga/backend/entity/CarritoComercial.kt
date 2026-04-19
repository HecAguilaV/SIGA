package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "CARRITOS", schema = "siga_comercial")
class CarritoComercial(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "usuario_id", nullable = false, unique = true)
    val usuarioId: Int,

    @Column(name = "plan_id", nullable = false)
    var planId: Int,

    @Column(nullable = false, length = 20)
    var periodo: String = "MENSUAL",

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CarritoComercial) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "CarritoComercial(id=$id, usuarioId=$usuarioId, planId=$planId)"
}
