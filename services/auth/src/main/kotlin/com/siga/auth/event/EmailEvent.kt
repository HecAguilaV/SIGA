package com.siga.auth.event

import java.time.Instant
import java.util.UUID

/**
 * Email event published by Auth to the `email-events` Kafka topic.
 *
 * This is the producer-side contract. The Notification service
 * has an identical data class for deserialization.
 *
 * @property eventId unique identifier for idempotency checking
 * @property email recipient email address
 * @property type type of email (WELCOME, PASSWORD_RESET)
 * @property name recipient's display name
 * @property token optional verification/reset token
 * @property timestamp when the event was created
 */
data class EmailEvent(
    val eventId: UUID = UUID.randomUUID(),
    val email: String,
    val type: String,
    val name: String,
    val token: String? = null,
    val timestamp: Instant = Instant.now()
)
