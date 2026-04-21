package com.siga.auth.entity

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
class RolPermisoId(
    @Column(name = "rol", nullable = false, length = 20)
    var rol: String = "",

    @Column(name = "permiso_id", nullable = false)
    var permisoId: Int = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolPermisoId) return false
        return rol == other.rol && permisoId == other.permisoId
    }

    override fun hashCode(): Int = 31 * rol.hashCode() + permisoId.hashCode()
}

@Entity
@Table(name = "ROLES_PERMISOS", schema = "siga_auth")
class RolPermiso(
    @EmbeddedId
    val id: RolPermisoId = RolPermisoId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", insertable = false, updatable = false)
    val permiso: Permiso? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolPermiso) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "RolPermiso(rol=${id.rol}, permisoId=${id.permisoId})"
}
