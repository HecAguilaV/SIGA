package com.siga.billing.domain.port

import com.siga.billing.domain.model.PaymentRequest
import com.siga.billing.domain.model.PaymentResponse

/**
 * Port for payment processing.
 * This interface defines the contract for any payment gateway integration.
 */
interface PaymentGateway {
    /**
     * Processes a payment request.
     * @param request The payment details.
     * @return The payment response.
     */
    fun processPayment(request: PaymentRequest): PaymentResponse
}
