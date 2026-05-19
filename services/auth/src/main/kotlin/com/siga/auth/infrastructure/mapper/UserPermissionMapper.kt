package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.UserPermission as DomainUserPermission
import com.siga.auth.entity.UserPermission as UserPermissionEntity
import com.siga.auth.entity.UserPermissionId

/**
 * Mapper between UserPermission JPA entity (with @EmbeddedId) and UserPermission domain model.
 * Handles:
 * - Embedded UserPermissionId (userId/permissionId) ↔ top-level fields
 * - Nullable UUIDs from entity → non-nullable UUIDs in domain (throws if null)
 */
object UserPermissionMapper {

    fun toDomain(entity: UserPermissionEntity): DomainUserPermission {
        return DomainUserPermission(
            userId = entity.id.userId ?: throw IllegalStateException("UserPermission userId cannot be null"),
            permissionId = entity.id.permissionId ?: throw IllegalStateException("UserPermission permissionId cannot be null"),
            assignedAt = entity.assignedAt,
            assignedBy = entity.assignedBy
        )
    }

    fun toEntity(domain: DomainUserPermission): UserPermissionEntity {
        return UserPermissionEntity(
            id = UserPermissionId(userId = domain.userId, permissionId = domain.permissionId),
            assignedAt = domain.assignedAt,
            assignedBy = domain.assignedBy
        )
    }
}
