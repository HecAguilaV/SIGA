package com.siga.sales.event

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Publishes sale lifecycle events to the `sale-events` Kafka topic.
 *
 * This is the entry point of the SAGA choreography: when a sale is
 * created, a [SaleEvent] with type [SaleEventType.SALE_INITIATED] is
 * emitted, triggering the Inventory service to attempt stock reservation.
 */
@Component
class SaleEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(SaleEventProducer::class.java)

    companion object {
        const val TOPIC = "sale-events"
    }

    /**
     * Publishes a sale event using the saleId as the Kafka message key.
     *
     * Using the saleId as key guarantees that all events for the same sale
     * land on the same partition, preserving ordering per sale.
     */
    fun publish(event: SaleEvent) {
        log.info("Publishing {} for sale={}", event.eventType, event.saleId)
        kafkaTemplate.send(TOPIC, event.saleId.toString(), event)
    }
}
