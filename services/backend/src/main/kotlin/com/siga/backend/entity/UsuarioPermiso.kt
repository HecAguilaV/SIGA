package com.siga.backend.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant

@Entity
@Table(name = "USUARIOS_PERMISOS", schema = "siga_auth")
class UsuarioPermiso(
    @Id
    @EmbeddedId
    var id: UsuarioPermisoId,

    @Column(name = "fecha_asignacion", nullable = false)
    var fechaAsignacion: Instant = Instant.now(),

    @Column(name = "asignado_por")
    var asignadoPor: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    var usuario: UsuarioSaas? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", insertable = false, updatable = false)
    var permiso: Permiso? = null
) : Serializable {
    init {
        if (id.usuarioId == 0 || id.permisoId == 0) {
            throw IllegalArgumentException("usuarioId and permisoId must be non-zero")
        }
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioPermiso) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UsuarioPermiso(id=$id)"
}
