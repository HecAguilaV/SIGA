package com.siga.auth.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for user-store assignments.
 * No JPA, no Spring dependencies.
 */
data class UserStore(
    val userId: UUID,
    val storeId: UUID,
    val assignedAt: Instant
)
