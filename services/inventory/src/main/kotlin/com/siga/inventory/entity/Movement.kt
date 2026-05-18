package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * Records a stock quantity change (Kardex entry).
 *
 * Every stock mutation — whether from a sale, manual adjustment,
 * transfer, or receiving — creates a Movement for full traceability.
 * The previous and new quantities allow auditing the exact delta.
 */
@Entity
@Table(name = "movements", schema = "inventory")
class Movement(
    @Id
    var id: UUID? = null,

    @Column(name = "product_id", nullable = false)
    val productId: UUID,

    @Column(name = "store_id", nullable = false)
    val storeId: UUID,

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
    val userId: UUID? = null,

    @Column(name = "sale_id")
    val saleId: UUID? = null,

    @Column(columnDefinition = "TEXT")
    val observations: String? = null,

    @Column(name = "correlation_id")
    val correlationId: UUID? = null,

    @Column(name = "destination_store_id")
    val destinationStoreId: UUID? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Movement) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Movement(id=$id, type=$type, productId=$productId, quantity=$quantity)"
}
