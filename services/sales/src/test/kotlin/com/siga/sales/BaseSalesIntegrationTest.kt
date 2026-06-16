package com.siga.sales

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

/**
 * Base class for all Sales integration tests.
 *
 * Centralizes:
 * 1. Testcontainers initialization (Kafka)
 * 2. Active profiles (test)
 * 3. Spring context loading
 * 4. Kotest Spring extension
 * 5. Global transaction management
 *
 * This avoids "localhost traps" and ensures all tests use the same Kafka instance.
 */
@SpringBootTest
@ContextConfiguration(initializers = [KafkaTestContainer.Initializer::class])
@ActiveProfiles("test")
@Transactional
abstract class BaseSalesIntegrationTest : DescribeSpec() {
    init {
        extension(SpringExtension())
    }
}
