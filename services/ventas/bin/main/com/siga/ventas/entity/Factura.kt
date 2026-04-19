package com.siga.ventas.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

/**
 * Factura del portal comercial (schema siga_comercial).
 * Gestionada por el servicio de ventas para el registro de cobros SaaS.
 */
@Entity
@Table(name = "FACTURAS", schema = "siga_comercial")
class Factura(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "numero_factura", nullable = false, unique = true, length = 50)
    val numeroFactura: String,

    @Column(name = "usuario_id", nullable = false)
    val usuarioId: Int,

    @Column(name = "usuario_nombre", nullable = false, length = 255)
    val usuarioNombre: String,

    @Column(name = "usuario_email", nullable = false, length = 255)
    val usuarioEmail: String,

    @Column(name = "plan_id", nullable = false)
    val planId: Int,

    @Column(name = "plan_nombre", nullable = false, length = 255)
    val planNombre: String,

    @Column(name = "precio_uf", nullable = false, precision = 10, scale = 2)
    val precioUF: BigDecimal,

    @Column(name = "precio_clp", precision = 12, scale = 2)
    val precioCLP: BigDecimal? = null,

    @Column(nullable = false, length = 10)
    val unidad: String = "UF",

    @Column(name = "fecha_compra", nullable = false)
    val fechaCompra: Instant,

    @Column(name = "fecha_vencimiento")
    val fechaVencimiento: Instant? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var estado: EstadoFactura = EstadoFactura.PAGADA,

    @Column(name = "metodo_pago", length = 100)
    val metodoPago: String? = null,

    @Column(name = "ultimos_4_digitos", length = 4)
    val ultimos4Digitos: String? = null,

    @Column(name = "suscripcion_id")
    val suscripcionId: Int? = null,

    @Column(name = "pago_id")
    val pagoId: Int? = null,

    @Column(precision = 10, scale = 2)
    val iva: BigDecimal? = null,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Factura) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Factura(id=$id, numero=$numeroFactura, estado=$estado)"
}
