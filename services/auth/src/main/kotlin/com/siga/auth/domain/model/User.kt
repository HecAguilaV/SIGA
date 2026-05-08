package com.siga.auth.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for SaaS users (employees).
 * No JPA, no Spring dependencies.
 */
data class User(
    val id: UUID? = null,
    val email: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String? = null,
    val role: UserRole,
    val commercialUserId: Int? = null,
    val isActive: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
