package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.CashShift
import com.siga.sales.entity.CashShiftEntity
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Mapper between CashShift domain model and CashShiftEntity JPA entity.
 */
@Component
class CashShiftMapper {

    fun toDomain(entity: CashShiftEntity): CashShift {
        return CashShift(
            id = entity.id ?: UUID.randomUUID(),
            storeId = entity.storeId,
            userId = entity.userId,
            openedAt = entity.openedAt,
            closedAt = entity.closedAt,
            initialAmount = entity.initialAmount,
            finalAmount = entity.finalAmount,
            status = entity.status
        )
    }

    fun toEntity(domain: CashShift): CashShiftEntity {
        return CashShiftEntity(
            id = if (domain.id == UUID.fromString("00000000-0000-0000-0000-000000000000")) null else domain.id,
            storeId = domain.storeId,
            userId = domain.userId,
            initialAmount = domain.initialAmount,
            status = domain.status
        )
    }
}
