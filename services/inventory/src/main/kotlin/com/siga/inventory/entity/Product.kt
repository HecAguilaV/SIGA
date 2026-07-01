package com.siga.inventory.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "products", schema = "inventory")
class Product(
    @Id
    var id: UUID? = null,

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "category_id")
    var categoryId: UUID? = null,

    @Column(name = "barcode", unique = true, length = 100)
    var barcode: String? = null,

    @Column(length = 50)
    var sku: String? = null,

    @Column(name = "unit_type", length = 20)
    var unitType: String? = null,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    var unitPrice: BigDecimal,

    @Column(name = "is_active", nullable = false)
    @field:JsonProperty("isActive")
    var isActive: Boolean = true,

    @Column(name = "commercial_user_id")
    var commercialUserId: UUID? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null
) {
    @PrePersist
    fun onPrePersist() {
        if (id == null) id = UUID.randomUUID()
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
        if (other !is Product) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Product(id=$id, name=$name, barcode=$barcode)"
}
