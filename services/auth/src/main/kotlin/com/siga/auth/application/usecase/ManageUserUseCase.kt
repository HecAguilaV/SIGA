package com.siga.auth.application.usecase

import com.siga.auth.domain.model.User
import com.siga.auth.domain.port.UserRepositoryPort
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Use case for User management operations.
 * Encapsulates domain logic (validation, uniqueness checks) above the port layer.
 */
@Service
class ManageUserUseCase(
    private val userRepositoryPort: UserRepositoryPort
) {

    fun findById(id: UUID): User? = userRepositoryPort.findById(id)

    fun findByEmail(email: String): User? = userRepositoryPort.findByEmail(email)

    fun findAll(): List<User> = userRepositoryPort.findAll()

    fun findByCustomerId(customerId: Int): List<User> = userRepositoryPort.findByCustomerId(customerId)

    fun create(user: User): User {
        require(user.email.isNotBlank()) { "Email must not be blank" }
        require(user.passwordHash.isNotBlank()) { "Password hash must not be blank" }
        require(user.firstName.isNotBlank()) { "First name must not be blank" }

        if (userRepositoryPort.existsByEmail(user.email)) {
            throw IllegalArgumentException("Email already exists: ${user.email}")
        }

        return userRepositoryPort.save(user.copy(id = null))
    }

    fun update(id: UUID, user: User): User {
        val existing = userRepositoryPort.findById(id)
            ?: throw IllegalArgumentException("User not found: $id")

        return userRepositoryPort.save(user.copy(id = id))
    }
}
