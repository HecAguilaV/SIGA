package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "CATEGORIAS", schema = "siga_inventario")
class Categoria(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, unique = true, length = 100)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @Column(name = "usuario_comercial_id")
    var usuarioComercialId: Int? = null,

    @Column(nullable = false)
    var activa: Boolean = true,
    
    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Categoria) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Categoria(id=$id, nombre=$nombre)"

    fun copy(
        id: Int = this.id,
        nombre: String = this.nombre,
        descripcion: String? = this.descripcion,
        usuarioComercialId: Int? = this.usuarioComercialId,
        activa: Boolean = this.activa,
        fechaCreacion: Instant = this.fechaCreacion
    ): Categoria = Categoria(id, nombre, descripcion, usuarioComercialId, activa, fechaCreacion)
}

