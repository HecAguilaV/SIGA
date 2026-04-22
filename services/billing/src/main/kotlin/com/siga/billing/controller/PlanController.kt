package com.siga.billing.controller

import com.siga.billing.entity.Plan
import com.siga.billing.repository.PlanRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage subscription plans.
 */
@RestController
@RequestMapping("/api/billing/plans")
class PlanController(
    private val planRepository: PlanRepository
) {
    @GetMapping
    fun getAllPlans(): ResponseEntity<List<Plan>> {
        return ResponseEntity.ok(planRepository.findByIsActiveTrue())
    }

    @GetMapping("/{id}")
    fun getPlanById(@PathVariable id: Int): ResponseEntity<Plan> {
        val plan = planRepository.findById(id)
        return if (plan.isPresent) {
            ResponseEntity.ok(plan.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createPlan(@RequestBody plan: Plan): ResponseEntity<Plan> {
        return ResponseEntity.ok(planRepository.save(plan))
    }

    @PutMapping("/{id}")
    fun updatePlan(@PathVariable id: Int, @RequestBody plan: Plan): ResponseEntity<Plan> {
        return if (planRepository.existsById(id)) {
            plan.id = id
            ResponseEntity.ok(planRepository.save(plan))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deletePlan(@PathVariable id: Int): ResponseEntity<Void> {
        return if (planRepository.existsById(id)) {
            planRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}