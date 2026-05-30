package com.siga.auth.entity

import jakarta.persistence.*
import java.time.Instant

/**
 * JPA entity for password reset tokens.
 *
 * Each token is bound to a customer email, has a fixed expiry (15 minutes),
 * and is invalidated after use (one-time use only).
 */
@Entity
@Table(name = "password_reset_tokens", schema = "auth")
class PasswordResetToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, length = 255)
    var email: String,

    @Column(nullable = false, length = 255, unique = true)
    var token: String,

    @Column(name = "expires_at", nullable = false)
    var expiresAt: Instant,

    @Column(nullable = false)
    var used: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PasswordResetToken) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "PasswordResetToken(id=$id, email=$email, used=$used)"
}
