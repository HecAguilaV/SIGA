package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.StockRepositoryPort
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.ProcessedEventRepositoryPort
import com.siga.inventory.event.StockEvent
import com.siga.inventory.event.StockEventType
import org.slf4j.LoggerFactory

/**
 * Application Use Case: Orchestrates stock reservation for a Sale (SAGA Step 2).
 *
 * WHY HEXAGONAL: This is the "Application Layer". It contains the business logic
 * previously trapped in the Kafka Consumer (`SaleEventConsumer`).
 * By moving it here, we can test stock reservation WITHOUT Kafka or a Database.
 *
 * WHY SAGA: This implements the "Reservation" pattern. It attempts an all-or-nothing
 * deduction. If any item fails, it emits STOCK_FAILED.
 */
class ReserveStockUseCase(
    private val stockPort: StockRepositoryPort,
    private val movementPort: MovementRepositoryPort,
    private val processedEventPort: ProcessedEventRepositoryPort,
    private val stockEventProducer: com.siga.inventory.event.StockEventProducer
) {
    private val log = LoggerFactory.getLogger(ReserveStockUseCase::class.java)

    /**
     * Handles SALE_INITIATED event.
     * Returns true if stock was reserved, false if failed.
     */
    fun handleSaleInitiated(
        eventId: java.util.UUID,
        saleId: java.util.UUID,
        tenantId: java.util.UUID,
        userId: java.util.UUID?,
        items: List<com.siga.inventory.event.SaleItem>
    ): Boolean {
        // 1. Idempotency Check
        if (processedEventPort.existsById(eventId)) {
            log.warn("Duplicate event={} for sale={}, skipping", eventId, saleId)
            return false
        }

        // 2. Validation Phase (All-or-nothing)
        for (item in items) {
            val stock: Stock? = stockPort.findByProductIdAndStoreId(item.productId, tenantId)
            if (stock == null || stock.quantity < item.quantity) {
                val reason = if (stock == null) {
                    "Product ${item.productId} not found in store $tenantId"
                } else {
                    "Insufficient stock for product ${item.productId}: available=${stock.quantity}, requested=${item.quantity}"
                }
                log.warn("Stock reservation failed for sale={}: {}", saleId, reason)
                stockEventProducer.publish(
                    StockEvent(
                        eventType = StockEventType.STOCK_FAILED,
                        saleId = saleId,
                        tenantId = tenantId,
                        reason = reason
                    )
                )
                return false
            }
        }

        // 3. Execution Phase (Deduct stock)
        for (item in items) {
            val stock = stockPort.findByProductIdAndStoreId(item.productId, tenantId)!!
            val previousQuantity = stock.quantity
            val newQuantity = previousQuantity - item.quantity

            stockPort.save(stock.copy(quantity = newQuantity))

            movementPort.save(
                Movement(
                    id = java.util.UUID.randomUUID(),
                    productId = item.productId,
                    storeId = tenantId,
                    type = MovementType.SALE,
                    quantity = item.quantity,
                    previousQuantity = previousQuantity,
                    newQuantity = newQuantity,
                    userId = userId,
                    saleId = saleId,
                    observations = "SAGA: stock reserved for sale $saleId"
                )
            )
        }

        log.info("Stock reserved for sale={}", saleId)
        stockEventProducer.publish(
            StockEvent(
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = tenantId
            )
        )

        // Mark as processed
        processedEventPort.save(eventId, com.siga.inventory.event.SaleEventType.SALE_INITIATED.name)
        return true
    }

    /**
     * Handles SALE_CANCELLED event (Compensating Transaction).
     */
    fun handleSaleCancelled(
        eventId: java.util.UUID,
        saleId: java.util.UUID,
        tenantId: java.util.UUID,
        userId: java.util.UUID?
    ) {
        if (processedEventPort.existsById(eventId)) {
            log.warn("Duplicate event={} for sale={}, skipping", eventId, saleId)
            return
        }

        val movements = movementPort.findBySaleId(saleId)
        if (movements.isEmpty()) {
            log.info("No stock movements to compensate for sale={}", saleId)
            return
        }

        for (movement in movements) {
            val stock = stockPort.findByProductIdAndStoreId(movement.productId, movement.storeId)
            if (stock != null) {
                val previousQuantity = stock.quantity
                val newQuantity = previousQuantity + movement.quantity
                stockPort.save(stock.copy(quantity = newQuantity))

                movementPort.save(
                    movement.copy(
                        id = java.util.UUID.randomUUID(),
                        type = MovementType.ADJUSTMENT,
                        quantity = movement.quantity,
                        previousQuantity = previousQuantity,
                        newQuantity = newQuantity,
                        observations = "SAGA COMPENSATE: stock restored for cancelled sale $saleId"
                    )
                )
            }
        }

        log.info("Stock compensated for cancelled sale={}", saleId)
        processedEventPort.save(eventId, com.siga.inventory.event.SaleEventType.SALE_CANCELLED.name)
    }
}
