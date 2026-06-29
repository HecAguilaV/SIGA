package com.siga.auth.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for platform-level administrators (SaaS owners).
 *
 * Platform admins are NOT tenant-scoped: they have no customerId and do not
 * belong to any tenant. They manage the platform itself (pricing, plans,
 * platform-wide metrics, audit, etc.) from a separate UI surface.
 *
 * No JPA, no Spring dependencies — kept in the domain layer for hexagonal isolation.
 */
data class PlatformAdmin(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val isActive: Boolean = true,
    val lastLoginAt: Instant? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
