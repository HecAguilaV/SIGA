package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.RolePermission as DomainRolePermission
import com.siga.auth.entity.RolePermission as RolePermissionEntity

/**
 * Mapper between RolePermission JPA entity (with @EmbeddedId) and RolePermission domain model.
 */
object RolePermissionMapper {

    fun toDomain(entity: RolePermissionEntity): DomainRolePermission {
        return DomainRolePermission(
            role = entity.id.role,
            permissionId = entity.id.permissionId ?: throw IllegalStateException("RolePermission permissionId cannot be null")
        )
    }
}
