package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Plan
import com.siga.billing.domain.port.PlanRepositoryPort
import com.siga.billing.entity.PlanEntity
import com.siga.billing.infrastructure.mapper.PlanMapper
import com.siga.billing.repository.PlanRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing the PlanRepositoryPort.
 */
@Component
class PlanJpaAdapter(
    private val planRepository: PlanRepository
) : PlanRepositoryPort {

    override fun findById(id: UUID): Plan? {
        val entity = planRepository.findById(id)
        return if (entity.isPresent) PlanMapper.toDomain(entity.get()) else null
    }

    override fun save(plan: Plan): Plan {
        val entity = PlanMapper.toEntity(plan)
        val savedEntity = planRepository.save(entity)
        return PlanMapper.toDomain(savedEntity)
    }

    override fun findByName(name: String): Plan? {
        val entity = planRepository.findByName(name) ?: return null
        return PlanMapper.toDomain(entity)
    }

    override fun findByIsActiveTrue(): List<Plan> {
        return planRepository.findByIsActiveTrue().map { PlanMapper.toDomain(it) }
    }
}
