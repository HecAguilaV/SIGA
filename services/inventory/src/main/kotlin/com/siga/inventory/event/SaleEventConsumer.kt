package com.siga.inventory.event

import com.siga.inventory.entity.Movement
import com.siga.inventory.entity.MovementType
import com.siga.inventory.entity.ProcessedEvent
import com.siga.inventory.repository.MovementRepository
import com.siga.inventory.repository.ProcessedEventRepository
import com.siga.inventory.repository.StockRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Consumes sale events from the Sales service (SAGA step 2).
 *
 * Listens on the `sale-events` topic. On SALE_INITIATED, attempts to
 * reserve stock for every item in the sale. If ALL items have sufficient
 * stock, emits STOCK_RESERVED. If ANY item fails, emits STOCK_FAILED
 * and rolls back any partial reservations (all-or-nothing).
 *
 * Idempotent: each event is processed at most once via [ProcessedEvent] check.
 */
@Component
class SaleEventConsumer(
    private val stockRepository: StockRepository,
    private val movementRepository: MovementRepository,
    private val processedEventRepository: ProcessedEventRepository,
    private val stockEventProducer: StockEventProducer
) {
    private val log = LoggerFactory.getLogger(SaleEventConsumer::class.java)

    @KafkaListener(
        topics = ["sale-events"],
        groupId = "siga-inventory",
        properties = [
            "spring.json.value.default.type=com.siga.inventory.event.SaleEvent"
        ]
    )
    @Transactional
    fun consume(event: SaleEvent) {
        log.info("Received {} for sale={}", event.eventType, event.saleId)

        // Idempotency check
        if (processedEventRepository.existsById(event.eventId)) {
            log.warn("Duplicate event={} for sale={}, skipping", event.eventId, event.saleId)
            return
        }

        when (event.eventType) {
            SaleEventType.SALE_INITIATED -> handleSaleInitiated(event)
            SaleEventType.SALE_CANCELLED -> handleSaleCancelled(event)
        }

        processedEventRepository.save(
            ProcessedEvent(eventId = event.eventId, eventType = event.eventType.name)
        )
    }

    /**
     * Attempts to reserve stock for all items in the sale.
     *
     * Uses an all-or-nothing strategy: if any item has insufficient stock,
     * no stock is deducted and a STOCK_FAILED event is emitted.
     */
    private fun handleSaleInitiated(event: SaleEvent) {
        // Phase 1: Validate all items have sufficient stock
        for (item in event.items) {
            val stock = stockRepository.findByProductIdAndStoreId(item.productId, event.tenantId)
            if (stock == null || stock.quantity < item.quantity) {
                val reason = if (stock == null) {
                    "Product ${item.productId} not found in store ${event.tenantId}"
                } else {
                    "Insufficient stock for product ${item.productId}: available=${stock.quantity}, requested=${item.quantity}"
                }

                log.warn("Stock reservation failed for sale={}: {}", event.saleId, reason)

                stockEventProducer.publish(
                    StockEvent(
                        eventType = StockEventType.STOCK_FAILED,
                        saleId = event.saleId,
                        tenantId = event.tenantId,
                        reason = reason
                    )
                )
                return
            }
        }

        // Phase 2: Deduct stock for all items (all validated, safe to proceed)
        for (item in event.items) {
            val stock = stockRepository.findByProductIdAndStoreId(item.productId, event.tenantId)!!
            val previousQuantity = stock.quantity

            stock.quantity -= item.quantity
            stockRepository.save(stock)

            movementRepository.save(
                Movement(
                    productId = item.productId,
                    storeId = event.tenantId,
                    type = MovementType.SALE,
                    quantity = item.quantity,
                    previousQuantity = previousQuantity,
                    newQuantity = stock.quantity,
                    userId = event.userId,
                    saleId = event.saleId,
                    observations = "SAGA: stock reserved for sale ${event.saleId}"
                )
            )
        }

        log.info("Stock reserved for sale={}", event.saleId)

        stockEventProducer.publish(
            StockEvent(
                eventType = StockEventType.STOCK_RESERVED,
                saleId = event.saleId,
                tenantId = event.tenantId
            )
        )
    }

    /**
     * Handles explicit sale cancellation — restores stock if previously reserved.
     *
     * This is the compensating transaction: if Sales cancels a previously
     * initiated sale, Inventory must restore the deducted quantities.
     */
    private fun handleSaleCancelled(event: SaleEvent) {
        val movements = movementRepository.findBySaleId(event.saleId)
        if (movements.isEmpty()) {
            log.info("No stock movements to compensate for sale={}", event.saleId)
            return
        }

        for (movement in movements) {
            val stock = stockRepository.findByProductIdAndStoreId(
                movement.productId, movement.storeId
            )
            if (stock != null) {
                val previousQuantity = stock.quantity
                stock.quantity += movement.quantity
                stockRepository.save(stock)

                movementRepository.save(
                    Movement(
                        productId = movement.productId,
                        storeId = movement.storeId,
                        type = MovementType.ADJUSTMENT,
                        quantity = movement.quantity,
                        previousQuantity = previousQuantity,
                        newQuantity = stock.quantity,
                        userId = event.userId,
                        saleId = event.saleId,
                        observations = "SAGA COMPENSATE: stock restored for cancelled sale ${event.saleId}"
                    )
                )
            }
        }

        log.info("Stock compensated for cancelled sale={}", event.saleId)
    }
}
