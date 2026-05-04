package com.siga.billing.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for a commercial user/company.
 * No JPA/Hibernate annotations here. This is the core business logic holder.
 */
data class Customer(
    val id: UUID,
    val email: String,
    val name: String,
    val lastName: String?,
    val taxId: String?,
    val phoneNumber: String?,
    val companyName: String?,
    val isActive: Boolean,
    val isOnTrial: Boolean,
    val trialStartAt: Instant?,
    val trialEndAt: Instant?,
    val role: String,
    val planId: UUID?,
    val createdAt: Instant,
    val updatedAt: Instant
)
