package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Stock
import com.siga.inventory.entity.Stock as EntityStock

/**
 * Mapper for Stock entities.
 */
object StockMapper {
    fun toDomain(entity: EntityStock): Stock {
        return Stock(
            productId = entity.productId,
            storeId = entity.storeId,
            quantity = entity.quantity,
            lastMovementAt = entity.lastMovementAt
        )
    }

    fun toEntity(model: Stock): EntityStock {
        // Note: ID handling might be needed depending on update strategy
        return EntityStock(
            productId = model.productId,
            storeId = model.storeId,
            quantity = model.quantity,
            lastMovementAt = model.lastMovementAt
        )
    }
}
