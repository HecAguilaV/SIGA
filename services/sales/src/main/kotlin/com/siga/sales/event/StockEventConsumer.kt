package com.siga.sales.event

import com.siga.sales.entity.ProcessedEvent
import com.siga.sales.entity.SaleStatus
import com.siga.sales.repository.ProcessedEventRepository
import com.siga.sales.repository.SaleRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Consumes stock response events from the Inventory service (SAGA step 3).
 *
 * Listens on the `stock-events` topic. When stock is reserved, the sale
 * transitions to COMPLETED. When stock fails, the sale is CANCELLED
 * (compensating transaction).
 *
 * Idempotent: each event is processed at most once via [ProcessedEvent] check.
 */
@Component
class StockEventConsumer(
    private val saleRepository: SaleRepository,
    private val processedEventRepository: ProcessedEventRepository
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

        val sale = saleRepository.findById(event.saleId).orElse(null)
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
                sale.status = SaleStatus.COMPLETED
                log.info("Sale {} confirmed — stock reserved", sale.id)
            }
            StockEventType.STOCK_FAILED -> {
                sale.status = SaleStatus.CANCELLED
                log.info("Sale {} cancelled — stock failed: {}", sale.id, event.reason)
            }
        }

        saleRepository.save(sale)
        processedEventRepository.save(
            ProcessedEvent(eventId = event.eventId, eventType = event.eventType.name)
        )
    }
}
