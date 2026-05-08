package com.siga.billing.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * JPA Entity for Customer.
 * This is an infrastructure detail. Use Customer (Domain Model) in business logic.
 */
@Entity
@Table(name = "customers", schema = "billing")
class CustomerEntity(
    @Id
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    @Column(name = "last_name", length = 100)
    var lastName: String? = null,

    @Column(name = "tax_id", length = 20)
    var taxId: String? = null,

    @Column(length = 20)
    var phone: String? = null,

    @Column(name = "company_name", length = 255)
    var companyName: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "is_on_trial", nullable = false)
    var isOnTrial: Boolean = false,

    @Column(name = "trial_start_at")
    var trialStartAt: Instant? = null,

    @Column(name = "trial_end_at")
    var trialEndAt: Instant? = null,

    @Column(length = 20)
    var role: String = "customer",

    @Column(name = "plan_id")
    var planId: UUID? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
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
        if (other !is CustomerEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "CustomerEntity(id=$id, email=$email, companyName=$companyName)"
}
