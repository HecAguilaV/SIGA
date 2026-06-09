package com.siga.billing.config

import org.apache.kafka.clients.consumer.Consumer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.test.context.ActiveProfiles

/**
 * Config test for [KafkaConfig].
 *
 * Verifies that the Kafka configuration beans are created correctly
 * with properties from the "test" profile.
 *
 * The test profile sets kafka.bootstrap-servers=localhost:0 and
 * kafka.listener.auto-startup=false, so no actual Kafka connection is made.
 */
@SpringBootTest(classes = [KafkaConfig::class])
@ActiveProfiles("test")
class KafkaConfigTest {

    @Autowired
    private lateinit var kafkaConfig: KafkaConfig

    @Autowired
    private lateinit var consumerFactory: ConsumerFactory<String, Any>

    @Autowired
    private lateinit var kafkaListenerContainerFactory: ConcurrentKafkaListenerContainerFactory<String, Any>

    @Test
    @DisplayName("KafkaConfig beans are created in the application context")
    fun kafkaConfig_beansAreCreated() {
        assertNotNull(kafkaConfig, "KafkaConfig bean should be created")
        assertNotNull(consumerFactory, "ConsumerFactory bean should be created")
        assertNotNull(kafkaListenerContainerFactory, "KafkaListenerContainerFactory bean should be created")
    }
}
