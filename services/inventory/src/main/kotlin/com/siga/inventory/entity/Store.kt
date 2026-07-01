package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "stores", schema = "inventory")
class Store(
    @Id
    var id: UUID? = null,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var address: String? = null,

    @Column(length = 100)
    var city: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "commercial_user_id")
    var commercialUserId: UUID? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null
) {
    @PrePersist
    fun onPrePersist() {
        if (id == null) id = UUID.randomUUID()
        if (createdAt == null) createdAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Store) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Store(id=$id, name=$name, city=$city)"
}
