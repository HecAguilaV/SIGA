package com.siga.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class RolPermisoId(
    @Column(name = "rol", nullable = false, length = 20)
    var rol: String,

    @Column(name = "permiso_id", nullable = false)
    var permisoId: Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolPermisoId) return false
        return rol == other.rol && permisoId == other.permisoId
    }

    override fun hashCode(): Int = 31 * rol.hashCode() + permisoId
}