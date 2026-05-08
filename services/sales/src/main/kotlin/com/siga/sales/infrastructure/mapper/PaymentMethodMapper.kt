package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.PaymentMethod
import com.siga.sales.entity.PaymentMethodEntity
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Mapper between PaymentMethod domain model and PaymentMethodEntity JPA entity.
 */
@Component
class PaymentMethodMapper {

    fun toDomain(entity: PaymentMethodEntity): PaymentMethod {
        return PaymentMethod(
            id = entity.id ?: UUID.randomUUID(),
            name = entity.name,
            isActive = entity.isActive
        )
    }

    fun toEntity(domain: PaymentMethod): PaymentMethodEntity {
        return PaymentMethodEntity(
            id = if (domain.id == UUID.fromString("00000000-0000-0000-0000-000000000000")) null else domain.id,
            name = domain.name,
            isActive = domain.isActive
        )
    }
}