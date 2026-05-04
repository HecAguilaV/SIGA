package com.siga.billing.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for a payment record.
 */
data class Payment(
    val id: UUID,
    val subscriptionId: UUID,
    val customerId: UUID,
    val amount: BigDecimal,
    val paymentMethod: String?,
    val status: PaymentStatus,
    val reference: String?,
    val paidAt: Instant
)

enum class PaymentStatus { PENDING, COMPLETED, FAILED, REFUNDED }
