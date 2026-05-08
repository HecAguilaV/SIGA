package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.entity.Movement as EntityMovement
import com.siga.inventory.entity.MovementType as EntityMovementType

/**
 * Mapper for Movement audit trail.
 *
 * WHY EXPLICIT MAPPING: The entity enum [EntityMovementType] has values (IN, OUT, SALE, ADJUSTMENT, TRANSFER)
 * while the domain enum [MovementType] has values (SALE, ADJUSTMENT, ENTRY).
 * Using `valueOf` would crash because the sets don't overlap exactly.
 */
object MovementMapper {

    fun toDomain(entity: EntityMovement): Movement {
        return Movement(
            id = entity.id,
            productId = entity.productId,
            storeId = entity.storeId,
            type = mapToDomainType(entity.type),
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
            type = mapToEntityType(model.type),
            quantity = model.quantity,
            previousQuantity = model.previousQuantity,
            newQuantity = model.newQuantity,
            userId = model.userId,
            saleId = model.saleId,
            observations = model.observations
        )
    }

    private fun mapToDomainType(entityType: EntityMovementType): MovementType {
        return when (entityType) {
            EntityMovementType.IN -> MovementType.ENTRY
            EntityMovementType.SALE -> MovementType.SALE
            EntityMovementType.ADJUSTMENT -> MovementType.ADJUSTMENT
            EntityMovementType.OUT -> throw IllegalArgumentException("OUT movement type is not supported in domain")
            EntityMovementType.TRANSFER -> throw IllegalArgumentException("TRANSFER movement type is not supported in domain")
        }
    }

    private fun mapToEntityType(domainType: MovementType): EntityMovementType {
        return when (domainType) {
            MovementType.ENTRY -> EntityMovementType.IN
            MovementType.SALE -> EntityMovementType.SALE
            MovementType.ADJUSTMENT -> EntityMovementType.ADJUSTMENT
        }
    }
}
