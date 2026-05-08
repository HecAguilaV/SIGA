package com.siga.auth.domain.model

import java.time.Instant

/**
 * Pure domain model for permissions.
 * No JPA, no Spring dependencies.
 */
data class Permission(
    val id: Int? = null,
    val code: String,
    val name: String,
    val description: String? = null,
    val category: String,
    val isActive: Boolean = true,
    val createdAt: Instant? = null
)
