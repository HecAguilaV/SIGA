package com.siga.billing.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Pure domain model for Subscription Plans.
 */
data class Plan(
    val id: UUID,
    val name: String,
    val description: String?,
    val storeLimit: Int,
    val userLimit: Int,
    val productLimit: Int?,
    val monthlyPrice: BigDecimal,
    val yearlyPrice: BigDecimal?,
    val displayOrder: Int,
    val isActive: Boolean
)
