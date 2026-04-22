package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "stock", schema = "inventory",
    uniqueConstraints = [UniqueConstraint(columnNames = ["product_id", "store_id"])]
)
class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "product_id", nullable = false)
    val productId: Int,

    @Column(name = "store_id", nullable = false)
    val storeId: Int,

    @Column(nullable = false)
    var quantity: Int = 0,

    @Column(name = "minimum_quantity", nullable = false)
    var minimumQuantity: Int = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stock) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Stock(id=$id, productId=$productId, storeId=$storeId, quantity=$quantity)"
}
