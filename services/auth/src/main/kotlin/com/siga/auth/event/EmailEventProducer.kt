package com.siga.auth.event

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Publishes email events to the `email-events` Kafka topic.
 *
 * Used by use cases (RegisterCustomerUseCase, ResetPasswordRequestUseCase)
 * to asynchronously trigger email delivery via the Notification service.
 *
 * The recipient email is used as the Kafka message key, guaranteeing
 * partition ordering per recipient.
 */
@Component
class EmailEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(EmailEventProducer::class.java)

    companion object {
        const val TOPIC = "email-events"
    }

    /**
     * Publishes an email event using the recipient email as the Kafka key.
     */
    fun publish(event: EmailEvent) {
        log.info("Publishing email event: type={}, email={}", event.type, event.email)
        kafkaTemplate.send(TOPIC, event.email, event)
    }
}
