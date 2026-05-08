package com.siga.sales.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Records a payment transaction within a CashShift.
 *
 * Each POS transaction links a Sale to a PaymentMethod and tracks
 * the amount paid. Multiple transactions can exist per sale
 * (e.g., split payments).
 */
data class PosTransaction(
    val id: UUID,
    val saleId: UUID,
    val shiftId: UUID,
    val paymentMethodId: UUID,
    val amount: BigDecimal,
    val last4Digits: String?,
    val createdAt: Instant,
    val status: TransactionStatus
)
