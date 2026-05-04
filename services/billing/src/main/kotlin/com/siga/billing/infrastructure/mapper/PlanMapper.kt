package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.Plan
import com.siga.billing.entity.PlanEntity

object PlanMapper {
    fun toDomain(entity: PlanEntity): Plan {
        return Plan(
            id = entity.id ?: throw IllegalStateException("Plan ID cannot be null"),
            name = entity.name,
            description = entity.description,
            storeLimit = entity.storeLimit,
            userLimit = entity.userLimit,
            productLimit = entity.productLimit,
            monthlyPrice = entity.monthlyPrice,
            yearlyPrice = entity.yearlyPrice,
            displayOrder = entity.displayOrder,
            isActive = entity.isActive
        )
    }

    fun toEntity(model: Plan): PlanEntity {
        return PlanEntity(
            id = model.id,
            name = model.name,
            description = model.description,
            storeLimit = model.storeLimit,
            userLimit = model.userLimit,
            productLimit = model.productLimit,
            monthlyPrice = model.monthlyPrice,
            yearlyPrice = model.yearlyPrice,
            displayOrder = model.displayOrder,
            isActive = model.isActive
        )
    }
}
