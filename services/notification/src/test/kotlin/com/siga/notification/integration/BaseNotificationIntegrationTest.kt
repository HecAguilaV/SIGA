package com.siga.notification.integration

import com.siga.notification.NotificationApplication
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

/**
 * Base class for Notification Service integration tests.
 *
 * Starts an Embedded Kafka broker for testing the consumer flow.
 * Uses H2 in-memory database (configured in application-test.yml).
 */
@SpringBootTest(classes = [NotificationApplication::class])
@EmbeddedKafka(
    topics = ["email-events"],
    partitions = 1
)
@ActiveProfiles("test")
@Import(BaseNotificationIntegrationTest.KafkaTestConfig::class)
abstract class BaseNotificationIntegrationTest {

    @TestConfiguration
    class KafkaTestConfig {
        @Bean
        fun kafkaTemplate(broker: EmbeddedKafkaBroker): KafkaTemplate<String, Any> {
            val producerProps = HashMap<String, Any>()
            producerProps["bootstrap.servers"] = broker.brokersAsString
            producerProps["key.serializer"] = StringSerializer::class.java
            producerProps["value.serializer"] = JsonSerializer::class.java
            producerProps["spring.json.add.type.headers"] = false
            val producerFactory = DefaultKafkaProducerFactory<String, Any>(producerProps)
            return KafkaTemplate(producerFactory)
        }
    }
}
