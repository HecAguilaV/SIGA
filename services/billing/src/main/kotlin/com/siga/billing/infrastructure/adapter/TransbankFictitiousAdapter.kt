package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.PaymentRequest
import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.port.PaymentGateway
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Fictitious adapter for Transbank payment gateway.
 * Emulates the behavior of a real payment gateway for testing and compliance.
 */
@Component
class TransbankFictitiousAdapter : PaymentGateway {

    override fun processPayment(request: PaymentRequest): PaymentResponse {
        // Emulating Transbank success logic
        val transactionId = "TKB-${UUID.randomUUID().toString().take(8).uppercase()}"
        
        val siiPayload = mapOf(
            "tipoDocumento" to 33, // Factura Electrónica
            "montoTotal" to request.amount,
            "montoNeto" to request.amount.divide(java.math.BigDecimal("1.19"), 0, java.math.RoundingMode.HALF_UP),
            "iva" to request.amount.subtract(request.amount.divide(java.math.BigDecimal("1.19"), 0, java.math.RoundingMode.HALF_UP)),
            "rutEmisor" to "76.000.000-1",
            "rutReceptor" to "77.777.777-7"
        )

        return PaymentResponse(
            success = true,
            transactionId = transactionId,
            responseCode = "0",
            message = "Transacción aprobada (Emulación)",
            siiPayload = siiPayload
        )
    }
}
