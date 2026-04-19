package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "PERMISOS", schema = "siga_saas")
class Permiso(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, unique = true, length = 50)
    var codigo: String,

    @Column(nullable = false, length = 100)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null,

    @Column(nullable = false, length = 50)
    var categoria: String,

    @Column(nullable = false)
    var activo: Boolean = true,

    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Permiso) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Permiso(id=$id, codigo=$codigo, nombre=$nombre)"

    fun copy(
        id: Int = this.id,
        codigo: String = this.codigo,
        nombre: String = this.nombre,
        descripcion: String? = this.descripcion,
        categoria: String = this.categoria,
        activo: Boolean = this.activo,
        fechaCreacion: Instant = this.fechaCreacion
    ): Permiso = Permiso(id, codigo, nombre, descripcion, categoria, activo, fechaCreacion)
}
