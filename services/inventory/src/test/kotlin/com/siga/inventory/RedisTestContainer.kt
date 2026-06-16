package com.siga.inventory

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

/**
 * Singleton Redis connection config shared across the entire test suite.
 *
 * Uses Redis on localhost:6379 — provided by:
 * - Local dev: `siga-redis` Docker container (always running)
 * - CI: Redis service in GitHub Actions
 *
 * This avoids coupling to Testcontainers' docker-java compatibility issues
 * while keeping the Initializer pattern for clean Spring context configuration.
 */
object RedisTestContainer {
    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(context: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.redis.host=localhost",
                "spring.data.redis.port=6379"
            ).applyTo(context.environment)
        }
    }
}
