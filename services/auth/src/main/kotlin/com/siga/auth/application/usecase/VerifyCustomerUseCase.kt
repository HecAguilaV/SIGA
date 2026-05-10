package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Use case for email verification.
 * Validates the verification token, checks expiry, and activates the customer.
 */
@Service
class VerifyCustomerUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort
) {

    fun verify(token: String): Customer {
        val customer = customerRepositoryPort.findByVerificationToken(token)
            ?: throw NoSuchElementException("Invalid verification token")

        val now = Instant.now()
        if (customer.verificationTokenExpiresAt != null && now.isAfter(customer.verificationTokenExpiresAt)) {
            throw IllegalStateException("Verification token has expired")
        }

        val activated = customer.copy(
            isActive = true,
            emailVerified = true,
            verificationToken = null,
            verificationTokenExpiresAt = null
        )

        return customerRepositoryPort.save(activated)
    }
}
