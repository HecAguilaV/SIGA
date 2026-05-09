package com.siga.sales.event

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Publishes sale-completed events to the `sale-completed` Kafka topic.
 *
 * This is SAGA step 4: after Inventory confirms stock reservation and the
 * sale transitions to COMPLETED, this producer notifies Billing so it can
 * generate the corresponding sale invoice.
 */
@Component
class SaleCompletedEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(SaleCompletedEventProducer::class.java)

    companion object {
        const val TOPIC = "sale-completed"
    }

    /**
     * Publishes a sale-completed event using the saleId as the Kafka message key.
     */
    fun publish(event: SaleCompletedEvent) {
        log.info("Publishing sale-completed for sale={}, total={}", event.saleId, event.total)
        kafkaTemplate.send(TOPIC, event.saleId.toString(), event)
    }
}
