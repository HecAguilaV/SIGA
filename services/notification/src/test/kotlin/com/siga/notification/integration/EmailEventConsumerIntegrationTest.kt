package com.siga.notification.integration

import com.siga.notification.domain.EmailEvent
import com.siga.notification.domain.EmailType
import com.siga.notification.infrastructure.entity.ProcessedEvent
import com.siga.notification.infrastructure.repository.ProcessedEventRepository
import com.siga.notification.infrastructure.service.EmailSenderService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Integration test for the email consumer flow.
 *
 * Publishes an [EmailEvent] to the Embedded Kafka `email-events` topic,
 * then verifies that the consumer processes it and records the event
 * in the [ProcessedEventRepository].
 */
class EmailEventConsumerIntegrationTest : BaseNotificationIntegrationTest() {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var processedEventRepository: ProcessedEventRepository

    @Autowired
    private lateinit var emailSenderService: EmailSenderService

    @BeforeEach
    fun setUp() {
        processedEventRepository.deleteAll()
    }

    @Test
    fun `consumer processes WELCOME event and persists to processed_events`() {
        val eventId = UUID.randomUUID()
        val event = EmailEvent(
            eventId = eventId,
            email = "integration@test.com",
            type = EmailType.WELCOME,
            name = "Integration Test",
            token = "test-token"
        )

        kafkaTemplate.send("email-events", event.email, event)

        // Give the consumer time to process
        Thread.sleep(2000)

        // Verify the event was marked as processed
        val processed = processedEventRepository.findById(eventId)
        assert(processed.isPresent) { "Event $eventId should have been processed" }
        assert(processed.get().eventType == "WELCOME")
    }

    @Test
    fun `consumer processes PASSWORD_RESET event and persists to processed_events`() {
        val eventId = UUID.randomUUID()
        val event = EmailEvent(
            eventId = eventId,
            email = "reset-integration@test.com",
            type = EmailType.PASSWORD_RESET,
            name = "Reset User",
            token = "reset-token"
        )

        kafkaTemplate.send("email-events", event.email, event)

        // Give the consumer time to process
        Thread.sleep(2000)

        // Verify the event was marked as processed
        val processed = processedEventRepository.findById(eventId)
        assert(processed.isPresent) { "Event $eventId should have been processed" }
        assert(processed.get().eventType == "PASSWORD_RESET")
    }

    @Test
    fun `consumer skips duplicate event and only processes once`() {
        val eventId = UUID.randomUUID()
        val event = EmailEvent(
            eventId = eventId,
            email = "duplicate@test.com",
            type = EmailType.WELCOME,
            name = "Duplicate Test"
        )

        // Send the same event twice
        kafkaTemplate.send("email-events", event.email, event)
        kafkaTemplate.send("email-events", event.email, event)

        // Give the consumer time to process both
        Thread.sleep(3000)

        // Verify the event was processed only once
        val processed = processedEventRepository.findById(eventId)
        assert(processed.isPresent) { "Event $eventId should have been processed once" }

        // There should be exactly one record for this eventId
        val allProcessed = processedEventRepository.findAll()
        val matches = allProcessed.filter { it.eventId == eventId }
        assert(matches.size == 1) { "Event $eventId should only appear once, but found ${matches.size}" }
    }
}
