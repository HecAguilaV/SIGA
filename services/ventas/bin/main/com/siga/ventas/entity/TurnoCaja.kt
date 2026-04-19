package com.siga.ventas.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "TURNOS_CAJA", schema = "siga_saas")
class TurnoCaja(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "local_id", nullable = false)
    val localId: Int,

    @Column(name = "usuario_id", nullable = false)
    val usuarioId: Int,

    @Column(name = "fecha_apertura", nullable = false)
    val fechaApertura: Instant = Instant.now(),

    @Column(name = "fecha_cierre")
    var fechaCierre: Instant? = null,

    @Column(name = "monto_inicial", nullable = false, precision = 10, scale = 2)
    val montoInicial: BigDecimal,

    @Column(name = "monto_final", precision = 10, scale = 2)
    var montoFinal: BigDecimal? = null,

    @Column(name = "monto_contado", precision = 10, scale = 2)
    var montoContado: BigDecimal? = null,

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    var estado: EstadoTurno = EstadoTurno.ABIERTO
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TurnoCaja) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "TurnoCaja(id=$id, localId=$localId, usuarioId=$usuarioId, estado=$estado)"
}
