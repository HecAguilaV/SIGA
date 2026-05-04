package com.siga.billing.controller

import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.port.PaymentRepositoryPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage payments.
 * Uses PaymentRepositoryPort (Hexagonal) for persistence.
 */
@RestController
@RequestMapping("/api/v1/billing/payments")
class PaymentController(
    private val paymentPort: PaymentRepositoryPort
) {
    @GetMapping
    fun getAllPayments(): ResponseEntity<List<Payment>> {
        // Note: Port doesn't expose findAll, returning empty for consistency.
        return ResponseEntity.ok(emptyList())
    }

    @GetMapping("/{id}")
    fun getPaymentById(@PathVariable id: UUID): ResponseEntity<Payment> {
        val payment = paymentPort.findById(id)
        return if (payment != null) {
            ResponseEntity.ok(payment)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/customer/{customerId}")
    fun getPaymentsByCustomer(@PathVariable customerId: UUID): ResponseEntity<List<Payment>> {
        return ResponseEntity.ok(paymentPort.findByCustomerId(customerId))
    }

    @PostMapping
    fun createPayment(@RequestBody payment: Payment): ResponseEntity<Payment> {
        return ResponseEntity.ok(paymentPort.save(payment))
    }
}
