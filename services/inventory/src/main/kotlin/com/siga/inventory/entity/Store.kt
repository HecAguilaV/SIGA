package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "stores", schema = "inventory")
class Store(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var address: String? = null,

    @Column(length = 100)
    var city: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "commercial_user_id")
    val commercialUserId: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Store) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Store(id=$id, name=$name, city=$city)"
}
