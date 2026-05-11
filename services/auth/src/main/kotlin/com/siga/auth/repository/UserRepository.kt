package com.siga.auth.repository

import com.siga.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for SaaS users (employees).
 */
@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByCustomerId(customerId: Int): List<User>
    fun existsByEmail(email: String): Boolean
}
