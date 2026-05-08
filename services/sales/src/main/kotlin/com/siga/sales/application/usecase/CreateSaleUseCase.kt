package com.siga.sales.application.usecase

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleItem
import com.siga.sales.domain.port.SaleRepositoryPort
import com.siga.sales.domain.port.SaleItemRepositoryPort
import com.siga.sales.event.SaleEventProducer
import com.siga.sales.event.SaleEvent
import com.siga.sales.event.SaleEventType
import com.siga.sales.event.SaleItemEvent
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Use case for creating a new sale with SAGA pattern.
 * Orchestrates the creation and emits events to Inventory.
 */
@Component
class CreateSaleUseCase(
    private val saleRepositoryPort: SaleRepositoryPort,
    private val saleItemRepositoryPort: SaleItemRepositoryPort,
    private val saleEventProducer: SaleEventProducer
) {
    fun createSale(sale: Sale, items: List<SaleItem>): Sale {
        // 1. Save sale
        val savedSale = saleRepositoryPort.save(sale)

        // 2. Save sale items
        items.forEach { item ->
            saleItemRepositoryPort.save(item.copy(saleId = savedSale.id))
        }

        // 3. Emit SAGA event to Inventory
        saleEventProducer.publish(
            SaleEvent(
                eventType = SaleEventType.SALE_INITIATED,
                saleId = savedSale.id,
                tenantId = savedSale.storeId,
                items = items.map { SaleItemEvent(it.productId, it.quantity) }
            )
        )

        return savedSale
    }
}
