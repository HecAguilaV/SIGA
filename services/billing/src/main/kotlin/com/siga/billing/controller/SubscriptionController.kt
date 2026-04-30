package com.siga.billing.controller

import com.siga.billing.entity.Subscription
import com.siga.billing.entity.SubscriptionStatus
import com.siga.billing.repository.SubscriptionRepository
import com.siga.billing.service.SubscriptionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage subscriptions.
 */
@RestController
@RequestMapping("/api/v1/billing/subscriptions")
class SubscriptionController(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionService: SubscriptionService
) {
    @GetMapping
    fun getAllSubscriptions(): ResponseEntity<List<Subscription>> {
        return ResponseEntity.ok(subscriptionRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getSubscriptionById(@PathVariable id: UUID): ResponseEntity<Subscription> {
        val subscription = subscriptionRepository.findById(id)
        return if (subscription.isPresent) {
            ResponseEntity.ok(subscription.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/customer/{customerId}")
    fun getSubscriptionsByCustomer(@PathVariable customerId: UUID): ResponseEntity<List<Subscription>> {
        return ResponseEntity.ok(subscriptionRepository.findByCustomerId(customerId))
    }

    @GetMapping("/customer/{customerId}/active")
    fun getActiveSubscriptions(@PathVariable customerId: UUID): ResponseEntity<List<Subscription>> {
        val activeStatuses = listOf(SubscriptionStatus.ACTIVE)
        return ResponseEntity.ok(subscriptionRepository.findByCustomerIdAndStatusIn(customerId, activeStatuses))
    }

    @PostMapping
    fun createSubscription(@RequestBody subscription: Subscription): ResponseEntity<Any> {
        // First save the subscription intent
        val savedSubscription = subscriptionRepository.save(subscription)
        
        // Process payment if needed (simulating orchestration)
        val customerId = savedSubscription.customerId
        val subscriptionId = savedSubscription.id ?: return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        
        // Use a default or calculated amount (this should come from the Plan)
        val paymentResponse = subscriptionService.processSubscriptionPayment(subscriptionId, customerId, java.math.BigDecimal("15000"))
        
        return if (paymentResponse.success) {
            ResponseEntity.status(HttpStatus.CREATED).body(savedSubscription)
        } else {
            ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(paymentResponse)
        }
    }

    @PutMapping("/{id}")
    fun updateSubscription(@PathVariable id: UUID, @RequestBody subscription: Subscription): ResponseEntity<Subscription> {
        return if (subscriptionRepository.existsById(id)) {
            ResponseEntity.ok(subscriptionRepository.save(subscription))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}