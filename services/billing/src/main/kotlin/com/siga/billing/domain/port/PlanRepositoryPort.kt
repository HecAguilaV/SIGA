package com.siga.billing.domain.port

import com.siga.billing.domain.model.Plan
import java.util.UUID

/**
 * Port for Plan persistence.
 */
interface PlanRepositoryPort {
    fun findById(id: UUID): Plan?
    fun save(plan: Plan): Plan
    fun findByName(name: String): Plan?
    fun findByIsActiveTrue(): List<Plan>
}
