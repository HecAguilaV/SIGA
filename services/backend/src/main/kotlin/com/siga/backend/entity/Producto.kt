package com.siga.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "PRODUCTOS", schema = "siga_saas")
class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, length = 200)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @Column(name = "categoria_id")
    var categoriaId: Int? = null,

    @Column(name = "codigo_barras", length = 50, unique = true)
    var codigoBarras: String? = null,

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    var precioUnitario: BigDecimal? = null,

    @Column(name = "usuario_comercial_id")
    var usuarioComercialId: Int? = null,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: Instant = Instant.now(),

    @Column(name = "fecha_actualizacion", nullable = false)
    var fechaActualizacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Producto(id=$id, nombre=$nombre, precio=$precioUnitario)"

    fun copy(
        id: Int = this.id,
        nombre: String = this.nombre,
        descripcion: String? = this.descripcion,
        categoriaId: Int? = this.categoriaId,
        codigoBarras: String? = this.codigoBarras,
        precioUnitario: BigDecimal? = this.precioUnitario,
        usuarioComercialId: Int? = this.usuarioComercialId,
        activo: Boolean = this.activo,
        fechaCreacion: Instant = this.fechaCreacion,
        fechaActualizacion: Instant = this.fechaActualizacion
    ): Producto = Producto(id, nombre, descripcion, categoriaId, codigoBarras, precioUnitario, usuarioComercialId, activo, fechaCreacion, fechaActualizacion)
}

