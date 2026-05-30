package com.siga.auth.application.usecase

import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.entity.PasswordResetToken
import com.siga.auth.event.EmailEvent
import com.siga.auth.event.EmailEventProducer
import com.siga.auth.repository.PasswordResetTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Use case for requesting a password reset.
 *
 * Generates a scoped reset token (15-minute expiry), persists it,
 * and publishes a PASSWORD_RESET email event.
 *
 * SECURITY: Always returns success (200) regardless of whether the email
 * exists, to prevent user enumeration attacks.
 */
@Service
class ResetPasswordRequestUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val emailEventProducer: EmailEventProducer
) {
    private val log = LoggerFactory.getLogger(ResetPasswordRequestUseCase::class.java)

    @Transactional
    fun request(email: String) {
        val customer = customerRepositoryPort.findByEmail(email)

        if (customer == null) {
            // Don't reveal whether the email exists — always return 200
            log.info("Password reset requested for unknown email: {}", email)
            return
        }

        // Invalidate any existing tokens for this email
        passwordResetTokenRepository.deleteByEmail(email)

        // Generate new token with 15-minute expiry
        val token = UUID.randomUUID().toString()
        val expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES)

        val resetToken = PasswordResetToken(
            email = email,
            token = token,
            expiresAt = expiresAt
        )
        passwordResetTokenRepository.save(resetToken)

        // Publish email event
        emailEventProducer.publish(
            EmailEvent(
                email = email,
                type = "PASSWORD_RESET",
                name = customer.name,
                token = token
            )
        )

        log.info("Password reset token created for email: {}", email)
    }
}
