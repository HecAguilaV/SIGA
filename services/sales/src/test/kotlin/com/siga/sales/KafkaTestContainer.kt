package com.siga.sales

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

/**
 * Singleton Kafka container shared across the entire test suite.
 * Using the Singleton Container pattern with lazy initialization ensures
 * we only start one instance and only if needed.
 */
object KafkaTestContainer {
    val container: KafkaContainer by lazy {
        KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.3"))
            .withReuse(true)
            .also { it.start() }
    }

    /**
     * Initializer to be used with @ContextConfiguration(initializers = [KafkaTestContainer.Initializer::class])
     * Works for both JUnit 5 and Kotest.
     * Note: We still use Initializer instead of @ServiceConnection because @ServiceConnection
     * works best with @TestConfiguration beans, but for global Singletons, this pattern is more reliable.
     */
    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.kafka.bootstrap-servers=${container.bootstrapServers}"
            ).applyTo(context.environment)
        }
    }
}
