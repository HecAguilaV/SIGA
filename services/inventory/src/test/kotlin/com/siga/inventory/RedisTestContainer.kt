package com.siga.inventory

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.RedisContainer
import org.testcontainers.utility.DockerImageName

/**
 * Singleton Redis container shared across the entire test suite.
 * Using the Singleton Container pattern with lazy initialization ensures
 * we only start one instance and only if needed.
 */
object RedisTestContainer {
    val container: RedisContainer by lazy {
        RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .also { it.start() }
    }

    /**
     * Initializer to be used with @ContextConfiguration(initializers = [RedisTestContainer.Initializer::class])
     * Works for both JUnit 5 and Kotest.
     */
    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.redis.host=${container.host}",
                "spring.data.redis.port=${container.firstMappedPort}"
            ).applyTo(context.environment)
        }
    }
}
