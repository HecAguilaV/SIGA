package com.siga.billing.controller

import com.siga.billing.entity.Subscription
import com.siga.billing.entity.SubscriptionStatus
import com.siga.billing.repository.SubscriptionRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage subscriptions.
 */
@RestController
@RequestMapping("/api/billing/subscriptions")
class SubscriptionController(
    private val subscriptionRepository: SubscriptionRepository
) {
    @GetMapping
    fun getAllSubscriptions(): ResponseEntity<List<Subscription>> {
        return ResponseEntity.ok(subscriptionRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getSubscriptionById(@PathVariable id: Int): ResponseEntity<Subscription> {
        val subscription = subscriptionRepository.findById(id)
        return if (subscription.isPresent) {
            ResponseEntity.ok(subscription.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/customer/{customerId}")
    fun getSubscriptionsByCustomer(@PathVariable customerId: Int): ResponseEntity<List<Subscription>> {
        return ResponseEntity.ok(subscriptionRepository.findByCustomerId(customerId))
    }

    @GetMapping("/customer/{customerId}/active")
    fun getActiveSubscriptions(@PathVariable customerId: Int): ResponseEntity<List<Subscription>> {
        val activeStatuses = listOf(SubscriptionStatus.ACTIVE)
        return ResponseEntity.ok(subscriptionRepository.findByCustomerIdAndStatusIn(customerId, activeStatuses))
    }

    @PostMapping
    fun createSubscription(@RequestBody subscription: Subscription): ResponseEntity<Subscription> {
        return ResponseEntity.ok(subscriptionRepository.save(subscription))
    }

    @PutMapping("/{id}")
    fun updateSubscription(@PathVariable id: Int, @RequestBody subscription: Subscription): ResponseEntity<Subscription> {
        return if (subscriptionRepository.existsById(id)) {
            subscription.id = id
            ResponseEntity.ok(subscriptionRepository.save(subscription))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}