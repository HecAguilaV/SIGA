package com.siga.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class UsuarioPermisoId(
    @Column(name = "usuario_id", nullable = false)
    var usuarioId: Int,

    @Column(name = "permiso_id", nullable = false)
    var permisoId: Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UsuarioPermisoId) return false
        return usuarioId == other.usuarioId && permisoId == other.permisoId
    }

    override fun hashCode(): Int = 31 * usuarioId + permisoId
}