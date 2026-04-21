package com.siga.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

enum class EstadoVenta {
    COMPLETADA,
    CANCELADA,
    PENDIENTE
}

@Entity
@Table(name = "VENTAS", schema = "siga_ventas")
class Venta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "local_id", nullable = false)
    var localId: Int,

    @Column(name = "usuario_id")
    var usuarioId: Int? = null,

    @Column(name = "usuario_comercial_id")
    var usuarioComercialId: Int? = null,

    @Column(nullable = false)
    var fecha: Instant = Instant.now(),

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var estado: EstadoVenta = EstadoVenta.COMPLETADA,

    @Column(columnDefinition = "TEXT")
    var observaciones: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Venta) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Venta(id=$id, total=$total, estado=$estado)"

    fun copy(
        id: Int = this.id,
        localId: Int = this.localId,
        usuarioId: Int? = this.usuarioId,
        usuarioComercialId: Int? = this.usuarioComercialId,
        fecha: Instant = this.fecha,
        total: BigDecimal = this.total,
        estado: EstadoVenta = this.estado,
        observaciones: String? = this.observaciones
    ): Venta = Venta(id, localId, usuarioId, usuarioComercialId, fecha, total, estado, observaciones)
}

