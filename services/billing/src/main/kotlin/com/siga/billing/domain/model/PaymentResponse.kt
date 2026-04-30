package com.siga.billing.domain.model

/**
 * Domain model for a payment response.
 */
data class PaymentResponse(
    val success: Boolean,
    val transactionId: String,
    val responseCode: String,
    val message: String,
    val siiPayload: Map<String, Any>? = null
)
