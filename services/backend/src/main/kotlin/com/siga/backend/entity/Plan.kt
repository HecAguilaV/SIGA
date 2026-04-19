package com.siga.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "PLANES", schema = "siga_comercial")
class Plan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true, length = 100)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @Column(name = "precio_mensual", nullable = false, precision = 10, scale = 2)
    var precioMensual: BigDecimal,

    @Column(name = "precio_anual", precision = 10, scale = 2)
    var precioAnual: BigDecimal? = null,

    @Column(name = "limite_bodegas")
    var limiteBodegas: Int? = null,

    @Column(name = "limite_usuarios")
    var limiteUsuarios: Int? = null,

    @Column(name = "limite_productos")
    var limiteProductos: Int? = null,

    @Column(columnDefinition = "JSONB")
    var caracteristicas: String? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(nullable = false)
    var orden: Int = 0,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Plan) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Plan(id=$id, nombre=$nombre)"
}
