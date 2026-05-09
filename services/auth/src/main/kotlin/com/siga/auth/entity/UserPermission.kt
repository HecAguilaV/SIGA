package com.siga.auth.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Embeddable
class UserPermissionId(
    @Column(name = "user_id", nullable = false)
    var userId: UUID? = null,

    @Column(name = "permission_id", nullable = false)
    var permissionId: UUID? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserPermissionId) return false
        return userId == other.userId && permissionId == other.permissionId
    }

    override fun hashCode(): Int = 31 * (userId?.hashCode() ?: 0) + permissionId.hashCode()
}

@Entity
@Table(name = "user_permissions", schema = "auth")
class UserPermission(
    @EmbeddedId
    val id: UserPermissionId = UserPermissionId(),

    @Column(name = "assigned_at", nullable = false)
    val assignedAt: Instant = Instant.now(),

    @Column(name = "assigned_by")
    val assignedBy: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    val permission: Permission? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserPermission) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "UserPermission(userId=${id.userId}, permissionId=${id.permissionId})"
}
