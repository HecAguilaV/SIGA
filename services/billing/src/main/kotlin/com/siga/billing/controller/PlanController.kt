package com.siga.billing.controller

import com.siga.billing.domain.model.Plan
import com.siga.billing.domain.port.PlanRepositoryPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage subscription plans.
 * Uses PlanRepositoryPort (Hexagonal) for persistence.
 */
@RestController
@RequestMapping("/api/v1/billing/plans")
class PlanController(
    private val planPort: PlanRepositoryPort
) {
    @GetMapping
    fun getAllPlans(): ResponseEntity<List<Plan>> {
        return ResponseEntity.ok(planPort.findByIsActiveTrue())
    }

    @GetMapping("/{id}")
    fun getPlanById(@PathVariable id: UUID): ResponseEntity<Plan> {
        val plan = planPort.findById(id)
        return if (plan != null) {
            ResponseEntity.ok(plan)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createPlan(@RequestBody plan: Plan): ResponseEntity<Plan> {
        return ResponseEntity.ok(planPort.save(plan))
    }

    @PutMapping("/{id}")
    fun updatePlan(@PathVariable id: UUID, @RequestBody plan: Plan): ResponseEntity<Plan> {
        // In Hexagonal, updates should go through a Use Case or the Port
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED).build()
    }

    @DeleteMapping("/{id}")
    fun deletePlan(@PathVariable id: UUID): ResponseEntity<Void> {
        // In Hexagonal, deletion logic belongs in a Use Case
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED).build()
    }
}
