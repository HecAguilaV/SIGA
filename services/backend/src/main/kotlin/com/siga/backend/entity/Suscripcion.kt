package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "SUSCRIPCIONES", schema = "siga_comercial")
class Suscripcion(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "usuario_id", nullable = false)
    val usuarioId: Int,

    @Column(name = "plan_id", nullable = false)
    var planId: Int,

    @Column(name = "fecha_inicio", nullable = false)
    val fechaInicio: LocalDate,

    @Column(name = "fecha_fin")
    var fechaFin: LocalDate? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var estado: EstadoSuscripcion = EstadoSuscripcion.ACTIVA,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val periodo: PeriodoSuscripcion = PeriodoSuscripcion.MENSUAL,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Suscripcion) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Suscripcion(id=$id, usuarioId=$usuarioId, estado=$estado)"
}
