package com.siga.auth.repository

import com.siga.auth.entity.RolePermission
import com.siga.auth.entity.RolePermissionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for role-based permission assignments.
 */
@Repository
interface RolePermissionRepository : JpaRepository<RolePermission, RolePermissionId> {
    fun findByIdRole(role: String): List<RolePermission>
}
