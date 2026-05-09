package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.SaleInvoice as DomainSaleInvoice
import com.siga.billing.entity.SaleInvoiceEntity as SaleInvoiceEntity

/**
 * Mapper between SaleInvoice JPA entity and SaleInvoice domain model.
 */
object SaleInvoiceMapper {

    fun toDomain(entity: SaleInvoiceEntity): DomainSaleInvoice {
        return DomainSaleInvoice(
            id = entity.id,
            saleId = entity.saleId,
            storeId = entity.storeId,
            userId = entity.userId,
            total = entity.total,
            items = entity.items,
            status = when (entity.status) {
                com.siga.billing.entity.SaleInvoiceStatus.COMPLETED -> com.siga.billing.domain.model.SaleInvoiceStatus.COMPLETED
                com.siga.billing.entity.SaleInvoiceStatus.CANCELLED -> com.siga.billing.domain.model.SaleInvoiceStatus.CANCELLED
            },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: DomainSaleInvoice): SaleInvoiceEntity {
        return SaleInvoiceEntity(
            id = domain.id,
            saleId = domain.saleId,
            storeId = domain.storeId,
            userId = domain.userId,
            total = domain.total,
            items = domain.items,
            status = when (domain.status) {
                com.siga.billing.domain.model.SaleInvoiceStatus.COMPLETED -> com.siga.billing.entity.SaleInvoiceStatus.COMPLETED
                com.siga.billing.domain.model.SaleInvoiceStatus.CANCELLED -> com.siga.billing.entity.SaleInvoiceStatus.CANCELLED
            },
            createdAt = domain.createdAt ?: java.time.Instant.now(),
            updatedAt = java.time.Instant.now()
        )
    }
}
