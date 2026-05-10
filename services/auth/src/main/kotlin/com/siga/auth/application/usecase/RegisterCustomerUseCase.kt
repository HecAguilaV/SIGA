package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.domain.port.EmailSenderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Use case for customer registration.
 * Validates input, hashes password, creates pending Customer, and sends verification email.
 */
@Service
class RegisterCustomerUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort,
    private val emailSenderPort: EmailSenderPort,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(email: String, rawPassword: String, name: String, companyName: String): Customer {
        require(email.isNotBlank()) { "Email must not be blank" }
        require(rawPassword.isNotBlank()) { "Password must not be blank" }
        require(name.isNotBlank()) { "Name must not be blank" }
        require(companyName.isNotBlank()) { "Company name must not be blank" }

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
            name = name,
            companyName = companyName,
            isActive = false,
            emailVerified = false,
            verificationToken = verificationToken,
            verificationTokenExpiresAt = tokenExpiresAt
        )

        val saved = customerRepositoryPort.save(customer)
        emailSenderPort.sendVerificationEmail(email, verificationToken, name)
        return saved
    }
}
