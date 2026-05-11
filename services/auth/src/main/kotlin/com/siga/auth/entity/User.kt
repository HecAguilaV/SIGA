package com.siga.auth.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users", schema = "auth")
class User(
    @Id
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Column(name = "first_name", nullable = false, length = 100)
    var firstName: String,

    @Column(name = "last_name", length = 100)
    var lastName: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var role: UserRole,

    @Column(name = "commercial_user_id")
    val commercialUserId: Int? = null,

    @Column(name = "customer_id")
    var customerId: Int? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null
) {
    @PrePersist
    fun onPrePersist() {
        val now = Instant.now()
        if (createdAt == null) createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "User(id=$id, email=$email, role=$role)"
}
