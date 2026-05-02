package com.siga.inventory.event

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Publishes stock response events to the `stock-events` Kafka topic.
 *
 * This is SAGA step 2b: after Inventory attempts stock reservation,
 * it emits either [StockEventType.STOCK_RESERVED] or [StockEventType.STOCK_FAILED]
 * so Sales can finalize the transaction.
 */
@Component
class StockEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, StockEvent>
) {
    private val log = LoggerFactory.getLogger(StockEventProducer::class.java)

    companion object {
        const val TOPIC = "stock-events"
    }

    /**
     * Publishes a stock event using the saleId as the Kafka message key.
     */
    fun publish(event: StockEvent) {
        log.info("Publishing {} for sale={}", event.eventType, event.saleId)
        kafkaTemplate.send(TOPIC, event.saleId.toString(), event)
    }
}
