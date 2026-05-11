package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.User
import com.siga.auth.domain.port.UserRepositoryPort
import com.siga.auth.infrastructure.mapper.UserMapper
import com.siga.auth.repository.UserRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing UserRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class UserJpaAdapter(
    private val userRepository: UserRepository
) : UserRepositoryPort {

    override fun findById(id: UUID): User? {
        return userRepository.findById(id).map { UserMapper.toDomain(it) }.orElse(null)
    }

    override fun findByEmail(email: String): User? {
        val entity = userRepository.findByEmail(email) ?: return null
        return UserMapper.toDomain(entity)
    }

    override fun findByCustomerId(customerId: Int): List<User> {
        return userRepository.findByCustomerId(customerId).map { UserMapper.toDomain(it) }
    }

    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    override fun findAll(): List<User> = userRepository.findAll().map { UserMapper.toDomain(it) }

    override fun save(user: User): User {
        val entity = UserMapper.toEntity(user)
        // Auto-generate UUID when id is null (used by ManageUserUseCase.create())
        if (entity.id == null) {
            entity.id = UUID.randomUUID()
        }
        val saved = userRepository.save(entity)
        return UserMapper.toDomain(saved)
    }
}
