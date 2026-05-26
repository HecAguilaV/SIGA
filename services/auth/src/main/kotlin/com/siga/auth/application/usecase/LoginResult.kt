package com.siga.auth.application.usecase

import java.util.UUID

/**
 * Result of a successful login attempt.
 * Contains the JWT token and principal metadata.
 */
data class LoginResult(
    val token: String,
    val email: String,
    val tenantId: Int?,
    val role: String,
    val principalType: String,  // "customer" or "user"
    val userId: UUID? = null,
    val permissions: List<String> = emptyList()
)
