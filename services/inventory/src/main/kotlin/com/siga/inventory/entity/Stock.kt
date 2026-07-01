package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "stock", schema = "inventory",
    uniqueConstraints = [UniqueConstraint(columnNames = ["product_id", "store_id"])]
)
class Stock(
    @Id
    var id: UUID? = null,

    @Column(name = "product_id", nullable = false)
    var productId: UUID,

    @Column(name = "store_id", nullable = false)
    var storeId: UUID,

    @Column(nullable = false)
    var quantity: Int = 0,

    @Column(name = "minimum_quantity", nullable = false)
    var minimumQuantity: Int = 0,

    @Column(name = "last_movement_at")
    var lastMovementAt: Instant? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null
) {
    @PrePersist
    @PreUpdate
    fun onUpdate() {
        if (id == null) id = UUID.randomUUID()
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stock) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Stock(id=$id, productId=$productId, storeId=$storeId, quantity=$quantity)"
}
