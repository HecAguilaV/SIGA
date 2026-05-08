package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Product
import com.siga.inventory.entity.Product as ProductEntity

/**
 * Maps between Domain Model (Pure) and JPA Entity (Infrastructure).
 *
 * WHY: In Hexagonal Architecture, the Domain doesn't know about JPA annotations.
 * This mapper bridges that gap. If we switch to MongoDB, we create a new mapper.
 */
object ProductMapper {
    fun toDomain(entity: ProductEntity): Product {
        return Product(
            id = entity.id ?: throw IllegalStateException("Product ID cannot be null"),
            name = entity.name,
            description = entity.description,
            categoryId = entity.categoryId,
            barcode = entity.barcode,
            unitPrice = entity.unitPrice,
            isActive = entity.isActive,
            commercialUserId = entity.commercialUserId,
            createdAt = entity.createdAt ?: throw IllegalStateException("createdAt cannot be null"),
            updatedAt = entity.updatedAt ?: throw IllegalStateException("updatedAt cannot be null")
        )
    }

    fun toEntity(model: Product): ProductEntity {
        return ProductEntity(
            id = model.id,
            name = model.name,
            description = model.description,
            categoryId = model.categoryId,
            barcode = model.barcode,
            unitPrice = model.unitPrice,
            isActive = model.isActive,
            commercialUserId = model.commercialUserId,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        )
    }
}
