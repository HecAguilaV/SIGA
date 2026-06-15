package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.domain.port.EmailSenderPort
import com.siga.auth.event.EmailEvent
import com.siga.auth.event.EmailEventProducer
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Use case for customer registration.
 *
 * Validates input, hashes password, creates pending Customer, and sends verification email.
 * Email sending mode is controlled by the `app.email.mode` feature flag:
 * - `async` (default): publishes a WELCOME EmailEvent to Kafka (Notification service sends it)
 * - `sync`: calls EmailSenderPort directly (legacy behavior, retained for rollback)
 */
@Service
class RegisterCustomerUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort,
    private val emailSenderPort: EmailSenderPort,
    private val passwordEncoder: PasswordEncoder,
    private val emailEventProducer: EmailEventProducer? = null,
    @Value("\${app.email.mode:async}")
    private val emailMode: String = "async"
) {

    fun register(email: String, rawPassword: String, name: String?, companyName: String?): Customer {
        require(email.isNotBlank()) { "Email must not be blank" }
        require(rawPassword.isNotBlank()) { "Password must not be blank" }
        val resolvedName = if (name.isNullOrBlank()) email.substringBefore("@") else name

        if (customerRepositoryPort.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists: $email")
        }

        val encodedPassword = passwordEncoder.encode(rawPassword)!!
        val verificationToken = UUID.randomUUID().toString()
        val tokenExpiresAt = Instant.now().plus(24, ChronoUnit.HOURS)

        val customer = Customer(
            id = null,
            email = email,
            passwordHash = encodedPassword,
            name = resolvedName,
            companyName = companyName,
            isActive = false,
            emailVerified = false,
            verificationToken = verificationToken,
            verificationTokenExpiresAt = tokenExpiresAt
        )

        val saved = customerRepositoryPort.save(customer)

        if (emailMode == "async" && emailEventProducer != null) {
            emailEventProducer.publish(
                EmailEvent(
                    email = email,
                    type = "WELCOME",
                    name = resolvedName,
                    token = verificationToken
                )
            )
        } else {
            emailSenderPort.sendVerificationEmail(email, verificationToken, resolvedName)
        }

        return saved
    }
}
