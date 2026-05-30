package com.siga.auth.application.usecase

import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.repository.PasswordResetTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

/**
 * Use case for confirming a password reset.
 *
 * Validates the reset token (exists, not expired, not used),
 * hashes the new password, updates the customer record,
 * and marks the token as used (one-time use).
 */
@Service
class ResetPasswordConfirmUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val log = LoggerFactory.getLogger(ResetPasswordConfirmUseCase::class.java)

    @Transactional
    fun confirm(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw NoSuchElementException("Invalid reset token")

        if (resetToken.used) {
            throw IllegalStateException("Reset token has already been used")
        }

        if (Instant.now().isAfter(resetToken.expiresAt)) {
            throw IllegalStateException("Reset token has expired")
        }

        val customer = customerRepositoryPort.findByEmail(resetToken.email)
            ?: throw NoSuchElementException("Customer not found for email: ${resetToken.email}")

        val encodedPassword = passwordEncoder.encode(newPassword)!!

        val updated = customer.copy(passwordHash = encodedPassword)
        customerRepositoryPort.save(updated)

        resetToken.used = true
        passwordResetTokenRepository.save(resetToken)

        log.info("Password reset confirmed for email: {}", resetToken.email)
    }
}
