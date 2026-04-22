package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "movements", schema = "inventory")
class Movement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "product_id", nullable = false)
    val productId: Int,

    @Column(name = "store_id", nullable = false)
    val storeId: Int,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val type: MovementType,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "previous_quantity", nullable = false)
    val previousQuantity: Int,

    @Column(name = "new_quantity", nullable = false)
    val newQuantity: Int,

    @Column(name = "user_id")
    val userId: Int? = null,

    @Column(name = "sale_id")
    val saleId: Int? = null,

    @Column(columnDefinition = "TEXT")
    val observations: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Movement) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Movement(id=$id, type=$type, productId=$productId, quantity=$quantity)"
}
