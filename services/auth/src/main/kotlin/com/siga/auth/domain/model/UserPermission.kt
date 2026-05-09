package com.siga.auth.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for user-permission assignments.
 * No JPA, no Spring dependencies.
 */
data class UserPermission(
    val userId: UUID,
    val permissionId: UUID,
    val assignedAt: Instant,
    val assignedBy: UUID? = null
)
