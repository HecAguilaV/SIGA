package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.entity.SaleEntity
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Mapper between Sale domain model and SaleEntity JPA entity.
 */
@Component
class SaleMapper {

    fun toDomain(entity: SaleEntity): Sale {
        return Sale(
            id = entity.id ?: UUID.randomUUID(),
            storeId = entity.storeId,
            userId = entity.userId,
            commercialUserId = entity.commercialUserId,
            createdAt = entity.createdAt,
            total = entity.total,
            status = entity.status,
            observations = entity.observations
        )
    }

    fun toEntity(domain: Sale): SaleEntity {
        return SaleEntity(
            id = if (domain.id == UUID.fromString("00000000-0000-0000-0000-000000000000")) null else domain.id,
            storeId = domain.storeId,
            userId = domain.userId,
            commercialUserId = domain.commercialUserId,
            total = domain.total,
            status = domain.status,
            observations = domain.observations
        )
    }
}
