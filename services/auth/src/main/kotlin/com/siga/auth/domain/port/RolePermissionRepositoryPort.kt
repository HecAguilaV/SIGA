package com.siga.auth.domain.port

import com.siga.auth.domain.model.RolePermission

/**
 * Port for RolePermission persistence (hexagonal architecture).
 */
interface RolePermissionRepositoryPort {
    fun findByRole(role: String): List<RolePermission>
}
