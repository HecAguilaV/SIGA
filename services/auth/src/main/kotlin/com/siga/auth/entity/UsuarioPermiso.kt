package com.siga.auth.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant

@Embeddable
class UsuarioPermisoId(
    @Column(name = "usuario_id", nullable = false)
    var usuarioId: Int = 0,

    @Column(name = "permiso_id", nullable = false)
    var permisoId: Int = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioPermisoId) return false
        return usuarioId == other.usuarioId && permisoId == other.permisoId
    }

    override fun hashCode(): Int = 31 * usuarioId.hashCode() + permisoId.hashCode()
}

@Entity
@Table(name = "USUARIOS_PERMISOS", schema = "siga_saas")
class UsuarioPermiso(
    @EmbeddedId
    val id: UsuarioPermisoId = UsuarioPermisoId(),

    @Column(name = "fecha_asignacion", nullable = false)
    val fechaAsignacion: Instant = Instant.now(),

    @Column(name = "asignado_por")
    val asignadoPor: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    val usuario: UsuarioSaas? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", insertable = false, updatable = false)
    val permiso: Permiso? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioPermiso) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UsuarioPermiso(usuarioId=${id.usuarioId}, permisoId=${id.permisoId})"
}
