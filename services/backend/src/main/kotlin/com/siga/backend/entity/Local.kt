package com.siga.backend.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "LOCALES", schema = "siga_inventario")
class Local(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, length = 100)
    var nombre: String,

    @Column(columnDefinition = "TEXT")
    var direccion: String? = null,

    @Column(length = 100)
    var ciudad: String? = null,

    @Column(name = "usuario_comercial_id")
    var usuarioComercialId: Int? = null,

    @Column(nullable = false)
    var activo: Boolean = true,
    
    @Column(name = "fecha_creacion", nullable = false)
    var fechaCreacion: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Local) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Local(id=$id, nombre=$nombre, ciudad=$ciudad)"

    fun copy(
        id: Int = this.id,
        nombre: String = this.nombre,
        direccion: String? = this.direccion,
        ciudad: String? = this.ciudad,
        usuarioComercialId: Int? = this.usuarioComercialId,
        activo: Boolean = this.activo,
        fechaCreacion: Instant = this.fechaCreacion
    ): Local = Local(id, nombre, direccion, ciudad, usuarioComercialId, activo, fechaCreacion)
}

