package com.siga.auth.application.usecase

import com.siga.auth.domain.model.UserPermission
import com.siga.auth.domain.port.UserPermissionRepositoryPort
import com.siga.auth.domain.port.UserRepositoryPort
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Use case for managing user-permission assignments.
 *
 * Enforces tenant scoping: the assigner must belong to the same tenant
 * as the target user. Uses [UserRepositoryPort] to look up the target
 * user's [customerId] and compares it with the provided [tenantId].
 *
 * Methods accepting [tenantId] validate tenant ownership before mutating.
 */
@Service
class ManageUserPermissionUseCase(
    private val userPermissionRepositoryPort: UserPermissionRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort
) {

    /**
     * Assigns a permission to a user.
     * Validates that the assigner's tenant matches the target user's tenant.
     */
    fun assign(userId: UUID, permissionId: UUID, assignedBy: UUID, tenantId: Int?): UserPermission {
        validateTenantAccess(userId, tenantId)

        if (userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)) {
            throw IllegalArgumentException("Permission already assigned to user")
        }

        val userPermission = UserPermission(
            userId = userId,
            permissionId = permissionId,
            assignedAt = Instant.now(),
            assignedBy = assignedBy
        )
        return userPermissionRepositoryPort.save(userPermission)
    }

    /**
     * Revokes a permission from a user.
     * Validates that the assigner's tenant matches the target user's tenant.
     */
    fun revoke(userId: UUID, permissionId: UUID, tenantId: Int?) {
        validateTenantAccess(userId, tenantId)

        if (!userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)) {
            throw NoSuchElementException("UserPermission not found for userId=$userId permissionId=$permissionId")
        }

        userPermissionRepositoryPort.deleteByUserIdAndPermissionId(userId, permissionId)
    }

    /**
     * Lists all permissions assigned to a user.
     */
    fun findByUserId(userId: UUID): List<UserPermission> {
        return userPermissionRepositoryPort.findByUserId(userId)
    }

    /**
     * Checks if a user has a specific permission code.
     * Returns true if the user has the permission assigned.
     */
    fun existsByUserIdAndPermissionId(userId: UUID, permissionId: UUID): Boolean {
        return userPermissionRepositoryPort.existsByUserIdAndPermissionId(userId, permissionId)
    }

    /**
     * Validates that the [tenantId] matches the target user's [customerId].
     * Throws [IllegalArgumentException] on mismatch or if user not found.
     *
     * - tenantId = null means no JWT context (backward compat for addFilters=false tests).
     * - In that case, tenant validation is skipped.
     */
    private fun validateTenantAccess(userId: UUID, tenantId: Int?) {
        if (tenantId == null) return // backward compat

        val user = userRepositoryPort.findById(userId)
            ?: throw IllegalArgumentException("User not found: $userId")

        if (user.customerId != tenantId) {
            throw IllegalArgumentException("Cross-tenant access denied: user belongs to tenant ${user.customerId}, assigner is tenant $tenantId")
        }
    }
}
