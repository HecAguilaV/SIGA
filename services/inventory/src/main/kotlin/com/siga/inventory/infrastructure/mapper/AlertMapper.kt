package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Alert
import com.siga.inventory.domain.model.AlertType
import com.siga.inventory.entity.Alert as EntityAlert
import com.siga.inventory.entity.AlertType as EntityAlertType

/**
 * Maps between Domain Model (Pure) and JPA Entity (Infrastructure) for Alerts.
 *
 * WHY EXPLICIT MAPPING: The entity enum [EntityAlertType] and domain enum [AlertType]
 * have the same values but are separate types. Using `valueOf` would work here since
 * they share values, but we keep explicit mapping for consistency with other mappers.
 */
object AlertMapper {

    fun toDomain(entity: EntityAlert): Alert {
        return Alert(
            id = entity.id,
            type = mapToDomainType(entity.type),
            productId = entity.productId,
            storeId = entity.storeId,
            message = entity.message,
            isRead = entity.isRead,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(model: Alert): EntityAlert {
        return EntityAlert(
            id = model.id,
            type = mapToEntityType(model.type),
            productId = model.productId,
            storeId = model.storeId,
            message = model.message,
            isRead = model.isRead,
            createdAt = model.createdAt
        )
    }

    private fun mapToDomainType(entityType: EntityAlertType): AlertType {
        return AlertType.valueOf(entityType.name)
    }

    private fun mapToEntityType(domainType: AlertType): EntityAlertType {
        return EntityAlertType.valueOf(domainType.name)
    }
}
