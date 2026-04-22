package com.siga.billing.controller

import com.siga.billing.entity.Payment
import com.siga.billing.repository.PaymentRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage payments.
 */
@RestController
@RequestMapping("/api/billing/payments")
class PaymentController(
    private val paymentRepository: PaymentRepository
) {
    @GetMapping
    fun getAllPayments(): ResponseEntity<List<Payment>> {
        return ResponseEntity.ok(paymentRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getPaymentById(@PathVariable id: Int): ResponseEntity<Payment> {
        val payment = paymentRepository.findById(id)
        return if (payment.isPresent) {
            ResponseEntity.ok(payment.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/customer/{customerId}")
    fun getPaymentsByCustomer(@PathVariable customerId: Int): ResponseEntity<List<Payment>> {
        return ResponseEntity.ok(paymentRepository.findByCustomerId(customerId))
    }

    @PostMapping
    fun createPayment(@RequestBody payment: Payment): ResponseEntity<Payment> {
        return ResponseEntity.ok(paymentRepository.save(payment))
    }
}