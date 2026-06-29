package com.siga.notification.infrastructure.consumer

import com.siga.notification.domain.EmailEvent
import com.siga.notification.domain.EmailType
import com.siga.notification.infrastructure.entity.ProcessedEvent
import com.siga.notification.infrastructure.repository.ProcessedEventRepository
import com.siga.notification.infrastructure.service.EmailSenderService
import com.siga.notification.infrastructure.service.TemplateRenderer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Kafka consumer for the `email-events` topic.
 *
 * Receives [EmailEvent] messages from Auth, performs idempotency
 * checking via [ProcessedEventRepository], renders the appropriate
 * HTML template, and dispatches via [EmailSenderService].
 *
 * Design: This is a thin orchestrator — no application layer needed
 * since the logic is purely infrastructure (deserialize → check → render → send).
 */
@Component
class EmailEventConsumer(
    private val processedEventRepository: ProcessedEventRepository,
    private val templateRenderer: TemplateRenderer,
    private val emailSenderService: EmailSenderService,
    @Value("\${app.verify-base-url:}")
    private val verifyBaseUrl: String = ""
) {
    private val log = LoggerFactory.getLogger(EmailEventConsumer::class.java)

    @KafkaListener(
        topics = ["email-events"],
        groupId = "siga-notification",
        properties = [
            "spring.json.value.default.type=com.siga.notification.domain.EmailEvent"
        ]
    )
    fun consume(event: EmailEvent) {
        log.info("Received email event: type={}, email={}, eventId={}", event.type, event.email, event.eventId)

        // Idempotency check — skip if already processed
        if (processedEventRepository.existsById(event.eventId)) {
            log.info("Duplicate event skipped: eventId={}", event.eventId)
            return
        }

        val maxAttempts = 4 // 1 initial + 3 retries
        var lastException: Exception? = null

        for (attempt in 1..maxAttempts) {
            try {
                when (event.type) {
                    EmailType.WELCOME -> handleWelcome(event)
                    EmailType.PASSWORD_RESET -> handlePasswordReset(event)
                }

                // Mark as processed ONLY on success
                processedEventRepository.save(
                    ProcessedEvent(
                        eventId = event.eventId,
                        eventType = event.type.name
                    )
                )
                log.info("Email event processed: eventId={}, type={}", event.eventId, event.type)
                return
            } catch (e: Exception) {
                lastException = e
                if (attempt < maxAttempts) {
                    val backoffMs = 2000L * (1L shl (attempt - 1)) // 2s, 4s, 8s
                    log.warn(
                        "Failed to process event (attempt {}/{}): eventId={}, type={}, retrying in {}ms",
                        attempt, maxAttempts, event.eventId, event.type, backoffMs, e
                    )
                    Thread.sleep(backoffMs)
                }
            }
        }

        // All retries exhausted — do NOT save ProcessedEvent (replayable later)
        log.error(
            "Email event moved to dead-letter after retries exhausted: " +
                "eventId={}, type={}, email={}",
            event.eventId, event.type, event.email, lastException
        )
    }

    private fun handleWelcome(event: EmailEvent) {
        val verificationLink = buildVerificationLink(event.token)
        val body = templateRenderer.render("welcome.html", event.name, verificationLink)
        emailSenderService.send(
            to = event.email,
            subject = "Verifica tu cuenta de SIGA",
            body = body
        )
    }

    private fun handlePasswordReset(event: EmailEvent) {
        val resetLink = buildResetLink(event.token)
        val body = templateRenderer.render("password-reset.html", event.name, resetLink)
        emailSenderService.send(
            to = event.email,
            subject = "Restablece tu contraseña de SIGA",
            body = body
        )
    }

    private fun buildVerificationLink(token: String?): String {
        val base = if (verifyBaseUrl.isNotBlank()) verifyBaseUrl else "/api/v1/auth"
        return if (token != null) "$base/verify?token=$token" else "$base/login"
    }

    private fun buildResetLink(token: String?): String {
        val base = if (verifyBaseUrl.isNotBlank()) verifyBaseUrl else "/api/v1/auth"
        return if (token != null) "$base/reset-password/confirm?token=$token" else "$base/login"
    }

}
