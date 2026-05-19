package com.siga.auth.domain.port

import com.siga.auth.domain.model.UserPermission
import java.util.UUID

/**
 * Port for UserPermission persistence (hexagonal architecture).
 */
interface UserPermissionRepositoryPort {
    fun findByUserId(userId: UUID): List<UserPermission>
    fun findByPermissionId(permissionId: UUID): List<UserPermission>
    fun save(userPermission: UserPermission): UserPermission
    fun deleteByUserIdAndPermissionId(userId: UUID, permissionId: UUID)
    fun existsByUserIdAndPermissionId(userId: UUID, permissionId: UUID): Boolean
}
