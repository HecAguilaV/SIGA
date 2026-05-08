package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.SaleItem
import com.siga.sales.entity.SaleItemEntity
import java.util.UUID

/**
 * Mapper between SaleItem domain model and SaleItemEntity JPA entity.
 */
@Component
class SaleItemMapper {

    fun toDomain(entity: SaleItemEntity): SaleItem {
        return SaleItem(
            id = entity.id ?: UUID.randomUUID(),
            saleId = entity.saleId,
            productId = entity.productId,
            quantity = entity.quantity,
            unitPrice = entity.unitPrice,
            subtotal = entity.subtotal
        )
    }

    fun toEntity(domain: SaleItem): SaleItemEntity {
        return SaleItemEntity(
            id = if (domain.id == UUID.fromString("00000000-0000-0000-0000-000000000000")) null else domain.id,
            saleId = domain.saleId,
            productId = domain.productId,
            quantity = domain.quantity,
            unitPrice = domain.unitPrice,
            subtotal = domain.subtotal
        )
    }
}
