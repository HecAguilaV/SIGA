package com.siga.inventory.event

import com.siga.inventory.application.usecase.ReserveStockUseCase
import com.siga.inventory.entity.ProcessedEvent
import com.siga.inventory.repository.ProcessedEventRepository
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Kafka Consumer (Infrastructure Layer).
 *
 * WHY HEXAGONAL: This class is a "Adapter" in the Infrastructure layer.
 * It ONLY handles Kafka plumbing. The business logic (stock reservation, SAGA flow)
 * lives in [ReserveStockUseCase].
 *
 * If we switch from Kafka to RabbitMQ, we delete this file and create a new Adapter.
 * The Domain/Application layers remain UNTOUCHED.
 */
@Component
class SaleEventConsumer(
    private val reserveStockUseCase: ReserveStockUseCase,
    private val processedEventRepository: ProcessedEventRepository
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

        when (event.eventType) {
            SaleEventType.SALE_INITIATED -> {
                reserveStockUseCase.handleSaleInitiated(
                    eventId = event.eventId,
                    saleId = event.saleId,
                    tenantId = event.tenantId,
                    userId = event.userId,
                    items = event.items
                )
            }
            SaleEventType.SALE_CANCELLED -> {
                reserveStockUseCase.handleSaleCancelled(
                    eventId = event.eventId,
                    saleId = event.saleId,
                    tenantId = event.tenantId,
                    userId = event.userId
                )
            }
        }
    }
}
