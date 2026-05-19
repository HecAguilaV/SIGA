package com.siga.auth.repository

import com.siga.auth.entity.UserPermission
import com.siga.auth.entity.UserPermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for user-permission assignments.
 * Uses [UserPermissionId] composite key (userId + permissionId).
 */
@Repository
interface UserPermissionRepository : JpaRepository<UserPermission, UserPermissionId> {
    fun findById_UserId(userId: UUID): List<UserPermission>
    fun findById_PermissionId(permissionId: UUID): List<UserPermission>
    fun deleteById_UserIdAndId_PermissionId(userId: UUID, permissionId: UUID)
    fun existsById_UserIdAndId_PermissionId(userId: UUID, permissionId: UUID): Boolean
}
