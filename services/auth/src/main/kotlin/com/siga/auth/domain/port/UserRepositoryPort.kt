package com.siga.auth.domain.port

import com.siga.auth.domain.model.User
import java.util.UUID

/**
 * Port for User persistence (hexagonal architecture).
 */
interface UserRepositoryPort {
    fun findById(id: UUID): User?
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findAll(): List<User>
    fun save(user: User): User
}
