package com.siga.ventas.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "VENTAS", schema = "siga_saas")
class Venta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "local_id", nullable = false)
    val localId: Int,

    @Column(name = "usuario_id")
    val usuarioId: Int? = null,

    @Column(name = "usuario_comercial_id")
    val usuarioComercialId: Int? = null,

    @Column(nullable = false)
    val fecha: Instant = Instant.now(),

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

    override fun toString(): String = "Venta(id=$id, localId=$localId, total=$total, estado=$estado)"
}
