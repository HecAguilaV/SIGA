package com.siga.auth.entity

import jakarta.persistence.*
import java.io.Serializable

@Embeddable
class RolePermissionId(
    @Column(name = "role", nullable = false, length = 20)
    var role: String = "",

    @Column(name = "permission_id", nullable = false)
    var permissionId: Int = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolePermissionId) return false
        return role == other.role && permissionId == other.permissionId
    }

    override fun hashCode(): Int = 31 * role.hashCode() + permissionId.hashCode()
}

@Entity
@Table(name = "role_permissions", schema = "auth")
class RolePermission(
    @EmbeddedId
    val id: RolePermissionId = RolePermissionId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    val permission: Permission? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolePermission) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "RolePermission(role=${id.role}, permissionId=${id.permissionId})"
}
