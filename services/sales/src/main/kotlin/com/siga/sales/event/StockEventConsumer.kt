package com.siga.sales.event

import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import com.siga.sales.entity.ProcessedEvent
import com.siga.sales.repository.ProcessedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Consumes stock response events from the Inventory service (SAGA step 3).
 *
 * Listens on the `stock-events` topic. When stock is reserved, the sale
 * transitions to COMPLETED and a [SaleCompletedEvent] is emitted so Billing
 * can generate the invoice. When stock fails, the sale is CANCELLED
 * (compensating transaction).
 *
 * Idempotent: each event is processed at most once via [ProcessedEvent] check.
 */
@Component
class StockEventConsumer(
    private val saleRepositoryPort: SaleRepositoryPort,
    private val processedEventRepository: ProcessedEventRepository,
    private val saleCompletedEventProducer: SaleCompletedEventProducer
) {
    private val log = LoggerFactory.getLogger(StockEventConsumer::class.java)

    @KafkaListener(
        topics = ["stock-events"],
        groupId = "siga-sales",
        properties = [
            "spring.json.value.default.type=com.siga.sales.event.StockEvent"
        ]
    )
    @Transactional
    fun consume(event: StockEvent) {
        log.info("Received {} for sale={}", event.eventType, event.saleId)

        // Idempotency check
        if (processedEventRepository.existsById(event.eventId)) {
            log.warn("Duplicate event={} for sale={}, skipping", event.eventId, event.saleId)
            return
        }

        val sale = saleRepositoryPort.findById(event.saleId)
        if (sale == null) {
            log.error("Sale not found for id={}, discarding event", event.saleId)
            return
        }

        if (sale.status != SaleStatus.PENDING) {
            log.warn("Sale {} is not PENDING (status={}), skipping", sale.id, sale.status)
            return
        }

        when (event.eventType) {
            StockEventType.STOCK_RESERVED -> {
                val updatedSale = sale.copy(status = SaleStatus.COMPLETED)
                saleRepositoryPort.save(updatedSale)
                log.info("Sale {} confirmed — stock reserved", sale.id)

                // SAGA step 4: notify Billing to generate sale invoice
                saleCompletedEventProducer.publish(
                    SaleCompletedEvent(
                        saleId = sale.id,
                        storeId = sale.storeId,
                        userId = sale.userId,
                        total = sale.total
                    )
                )
                log.info("SaleCompletedEvent emitted for sale={}", sale.id)
            }
            StockEventType.STOCK_FAILED -> {
                val updatedSale = sale.copy(status = SaleStatus.CANCELLED)
                saleRepositoryPort.save(updatedSale)
                log.info("Sale {} cancelled — stock failed: {}", sale.id, event.reason)
            }
        }

        processedEventRepository.save(
            ProcessedEvent(eventId = event.eventId, eventType = event.eventType.name)
        )
    }
}
