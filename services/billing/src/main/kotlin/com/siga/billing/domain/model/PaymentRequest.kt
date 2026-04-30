package com.siga.billing.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Domain model for a payment request.
 */
data class PaymentRequest(
    val amount: BigDecimal,
    val customerId: UUID,
    val description: String,
    val currency: String = "CLP"
)
