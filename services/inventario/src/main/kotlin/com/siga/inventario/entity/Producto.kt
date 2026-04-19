package com.siga.inventario.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "PRODUCTOS", schema = "siga_saas")
class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, length = 255)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @Column(name = "categoria_id")
    var categoriaId: Int? = null,

    @Column(name = "codigo_barras", unique = true, length = 100)
    var codigoBarras: String? = null,

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    var precioUnitario: BigDecimal,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "usuario_comercial_id")
    val usuarioComercialId: Int? = null,

    @Column(name = "fecha_creacion", nullable = false)
    val fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Producto(id=$id, nombre=$nombre, codigoBarras=$codigoBarras)"
}
