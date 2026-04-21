package com.siga.ventas.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "TRANSACCIONES_POS", schema = "siga_ventas")
class TransaccionPos(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "venta_id", nullable = false)
    val ventaId: Int,

    @Column(name = "turno_caja_id", nullable = false)
    val turnoCajaId: Int,

    @Column(name = "metodo_pago_id", nullable = false)
    val metodoPagoId: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val monto: BigDecimal,

    @Column(precision = 10, scale = 2)
    val cambio: BigDecimal? = null,

    @Column(nullable = false)
    val fecha: Instant = Instant.now(),

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var estado: EstadoTransaccion = EstadoTransaccion.COMPLETADA
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransaccionPos) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "TransaccionPos(id=$id, ventaId=$ventaId, monto=$monto, estado=$estado)"
}
