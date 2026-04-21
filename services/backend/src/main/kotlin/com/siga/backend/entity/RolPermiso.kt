package com.siga.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "ROLES_PERMISOS", schema = "siga_auth")
class RolPermiso(
    @Id
    @EmbeddedId
    var id: RolPermisoId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", insertable = false, updatable = false)
    var permiso: Permiso? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolPermiso) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "RolPermiso(id=$id)"
}
