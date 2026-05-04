package com.siga.billing.controller

import com.siga.billing.application.usecase.ManageSubscriptionUseCase
import com.siga.billing.domain.model.PaymentResponse
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage subscriptions.
 * Clean Hexagonal: Controller only talks to the Application Layer (Use Case).
 */
@RestController
@RequestMapping("/api/v1/billing/subscriptions")
class SubscriptionController(
    private val manageSubscriptionUseCase: ManageSubscriptionUseCase
) {
    @GetMapping
    fun getAllSubscriptions(): ResponseEntity<List<Subscription>> {
        // Note: Ideally, the Use Case should have a 'findAll' method.
        // For now, we return empty list as the port doesn't expose findAll in the current definition.
        return ResponseEntity.ok(emptyList())
    }

    @GetMapping("/{id}")
    fun getSubscriptionById(@PathVariable id: UUID): ResponseEntity<Subscription> {
        val subscription = manageSubscriptionUseCase.getSubscriptionById(id)
        return if (subscription != null) {
            ResponseEntity.ok(subscription)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/customer/{customerId}")
    fun getSubscriptionsByCustomer(@PathVariable customerId: UUID): ResponseEntity<List<Subscription>> {
        return ResponseEntity.ok(manageSubscriptionUseCase.getSubscriptionsByCustomer(customerId))
    }

    @GetMapping("/customer/{customerId}/active")
    fun getActiveSubscriptions(@PathVariable customerId: UUID): ResponseEntity<List<Subscription>> {
        return ResponseEntity.ok(manageSubscriptionUseCase.getActiveSubscriptions(customerId))
    }

    @PostMapping
    fun createSubscription(
        @RequestBody subscription: Subscription,
        @RequestParam amount: String
    ): ResponseEntity<Any> {
        val (savedSubscription, paymentResponse) = manageSubscriptionUseCase.createSubscriptionWithPayment(
            subscription, java.math.BigDecimal(amount)
        )

        return if (paymentResponse.success) {
            ResponseEntity.status(HttpStatus.CREATED).body(savedSubscription)
        } else {
            ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(paymentResponse)
        }
    }

    @PutMapping("/{id}")
    fun updateSubscription(
        @PathVariable id: UUID, 
        @RequestBody subscription: Subscription
    ): ResponseEntity<Subscription> {
        // In Hexagonal, updates should also go through the Use Case
        // For simplicity, we assume the Use Case handles the update logic
        val updated = manageSubscriptionUseCase.getSubscriptionById(id)
        return if (updated != null) {
            // Ideally: manageSubscriptionUseCase.update(...)
            ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
