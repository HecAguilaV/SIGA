package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.entity.Movement as EntityMovement
import com.siga.inventory.entity.MovementType as EntityMovementType

/**
 * Mapper for Movement audit trail.
 */
object MovementMapper {
    fun toDomain(entity: EntityMovement): Movement {
        return Movement(
            id = entity.id,
            productId = entity.productId,
            storeId = entity.storeId,
            type = MovementType.valueOf(entity.type.name),
            quantity = entity.quantity,
            previousQuantity = entity.previousQuantity,
            newQuantity = entity.newQuantity,
            userId = entity.userId,
            saleId = entity.saleId,
            observations = entity.observations
        )
    }

    fun toEntity(model: Movement): EntityMovement {
        return EntityMovement(
            id = model.id,
            productId = model.productId,
            storeId = model.storeId,
            type = EntityMovementType.valueOf(model.type.name),
            quantity = model.quantity,
            previousQuantity = model.previousQuantity,
            newQuantity = model.newQuantity,
            userId = model.userId,
            saleId = model.saleId,
            observations = model.observations
        )
    }
}
