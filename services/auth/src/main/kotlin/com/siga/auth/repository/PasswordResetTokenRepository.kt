package com.siga.auth.repository

import com.siga.auth.entity.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for password reset tokens.
 */
@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Int> {
    fun findByToken(token: String): PasswordResetToken?
    fun deleteByEmail(email: String)
}
