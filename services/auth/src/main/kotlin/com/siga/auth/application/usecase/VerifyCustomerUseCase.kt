package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.domain.port.UserRepositoryPort
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Use case for email verification.
 * Validates the verification token, checks expiry, and activates the customer.
 *
 * CONSOLIDATION (Customer IS Owner):
 * When the customer is verified, the paired User (created at registration with role OWNER)
 * is also activated so the customer can log in immediately.
 */
@Service
class VerifyCustomerUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort
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

        val savedCustomer = customerRepositoryPort.save(activated)

        // Activate the paired User (created at registration). Without this the customer
        // can verify the email but still can't log in because the User stays inactive.
        val pairedUser = userRepositoryPort.findByEmail(savedCustomer.email)
        if (pairedUser != null && !pairedUser.isActive) {
            userRepositoryPort.save(pairedUser.copy(isActive = true))
        }

        return savedCustomer
    }
}
