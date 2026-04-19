package com.siga.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "PAGOS", schema = "siga_comercial")
class Pago(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "suscripcion_id", nullable = false)
    val suscripcionId: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val monto: BigDecimal,

    @Column(nullable = false, length = 10)
    val moneda: String = "CLP",

    @Column(name = "metodo_pago", length = 50)
    val metodoPago: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var estado: EstadoPago = EstadoPago.PENDIENTE,

    @Column(name = "referencia_externa", length = 255)
    val referenciaExterna: String? = null,

    @Column(name = "fecha_pago")
    var fechaPago: Instant? = null,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pago) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Pago(id=$id, monto=$monto, estado=$estado)"
}
