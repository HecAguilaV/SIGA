package com.siga.sales.entity

import com.siga.sales.domain.model.SaleStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * JPA Entity for Sale.
 * Represents a point-of-sale transaction in a store.
 *
 * A sale is created with status PENDING and transitions to
 * COMPLETED or CANCELLED based on the SAGA
 * choreography with the Inventory service via Kafka events.
 *
 * @see com.siga.sales.domain.model.Sale the domain model
 */
@Entity
@Table(name = "sales", schema = "sales")
class SaleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "store_id", nullable = false)
    val storeId: UUID,

    @Column(name = "user_id")
    val userId: UUID? = null,

    @Column(name = "commercial_user_id")
    val commercialUserId: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: SaleStatus = SaleStatus.PENDING,

    @Column(columnDefinition = "TEXT")
    var observations: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaleEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "SaleEntity(id=$id, storeId=$storeId, total=$total, status=$status)"
}
