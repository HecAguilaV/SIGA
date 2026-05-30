package com.siga.notification.domain

import java.time.Instant
import java.util.UUID

/**
 * Event published by Auth to the `email-events` Kafka topic.
 *
 * The Notification service consumes these events, renders the appropriate
 * HTML template, and sends the email via JavaMailSender.
 *
 * @property eventId unique identifier for idempotency checking
 * @property email recipient email address
 * @property type type of email to send (WELCOME, PASSWORD_RESET)
 * @property name recipient's display name
 * @property token optional verification/reset token
 * @property timestamp when the event was created
 */
data class EmailEvent(
    val eventId: UUID = UUID.randomUUID(),
    val email: String,
    val type: EmailType,
    val name: String,
    val token: String? = null,
    val timestamp: Instant = Instant.now()
)
