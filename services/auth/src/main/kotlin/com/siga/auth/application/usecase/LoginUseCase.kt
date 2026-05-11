package com.siga.auth.application.usecase

import com.siga.auth.domain.model.User
import com.siga.auth.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Use case for dual-principal login.
 * Tries Customer first, then User. Returns JWT on success.
 */
@Service
class LoginUseCase(
    private val manageCustomerUseCase: ManageCustomerUseCase,
    private val manageUserUseCase: ManageUserUseCase,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun login(email: String, rawPassword: String): LoginResult {
        // 1. Try Customer first
        val customer = manageCustomerUseCase.findByEmail(email)
        if (customer != null) {
            return authenticateCustomer(customer, rawPassword)
        }

        // 2. If not a Customer, try User
        val user = manageUserUseCase.findByEmail(email)
        if (user != null) {
            return authenticateUser(user, rawPassword)
        }

        // 3. Neither found
        throw NoSuchElementException("Invalid credentials")
    }

    private fun authenticateCustomer(customer: com.siga.auth.domain.model.Customer, rawPassword: String): LoginResult {
        if (!customer.isActive) {
            throw IllegalStateException("Account is not active")
        }

        if (!passwordEncoder.matches(rawPassword, customer.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val token = jwtService.generateToken(
            email = customer.email,
            rol = customer.role,
            tenantId = customer.id,
            principalType = "customer"
        )

        return LoginResult(
            token = token,
            email = customer.email,
            tenantId = customer.id,
            role = customer.role,
            principalType = "customer"
        )
    }

    private fun authenticateUser(user: User, rawPassword: String): LoginResult {
        if (!user.isActive) {
            throw IllegalStateException("Account is not active")
        }

        if (!passwordEncoder.matches(rawPassword, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val token = jwtService.generateToken(
            email = user.email,
            rol = user.role.name,
            tenantId = null,
            principalType = "user"
        )

        return LoginResult(
            token = token,
            email = user.email,
            tenantId = null,
            role = user.role.name,
            principalType = "user",
            userId = user.id
        )
    }
}
