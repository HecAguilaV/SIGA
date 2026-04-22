package com.siga.billing.repository

import com.siga.billing.entity.Plan
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for subscription plans.
 */
@Repository
interface PlanRepository : JpaRepository<Plan, Int> {
    fun findByName(name: String): Plan?
    fun findByIsActiveTrue(): List<Plan>
}