package com.siga.auth.domain.model

import java.time.Instant

/**
 * Pure domain model for customers (business owners).
 * No JPA, no Spring dependencies.
 */
data class Customer(
    val id: Int? = null,
    val email: String,
    val passwordHash: String,
    val name: String,
    val lastName: String? = null,
    val taxId: String? = null,
    val phone: String? = null,
    val companyName: String? = null,
    val isActive: Boolean = true,
    val isOnTrial: Boolean = false,
    val trialStartAt: Instant? = null,
    val trialEndAt: Instant? = null,
    val emailVerified: Boolean = false,
    val verificationToken: String? = null,
    val verificationTokenExpiresAt: Instant? = null,
    val role: String = "customer",
    val planId: Int? = null,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)
