package com.siga.billing.repository

import com.siga.billing.entity.PlanEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA Repository for Plan.
 * Used by PlanJpaAdapter.
 */
@Repository
interface PlanRepository : JpaRepository<PlanEntity, UUID> {
    fun findByName(name: String): PlanEntity?
    fun findByIsActiveTrue(): List<PlanEntity>
}
