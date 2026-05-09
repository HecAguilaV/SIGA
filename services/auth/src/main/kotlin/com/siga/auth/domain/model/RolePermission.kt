package com.siga.auth.domain.model

import java.util.UUID

/**
 * Pure domain model for role-permission assignments.
 * No JPA, no Spring dependencies.
 */
data class RolePermission(
    val role: String,
    val permissionId: UUID
)
