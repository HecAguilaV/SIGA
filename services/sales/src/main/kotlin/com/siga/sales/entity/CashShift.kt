package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Represents an open/closed cash register shift at a store.
 *
 * A shift tracks the opening and closing balances, allowing
 * reconciliation of physical cash against recorded transactions.
 */
@Entity
@Table(name = "cash_shifts", schema = "sales")
class CashShift(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "store_id", nullable = false)
    val storeId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "opened_at", nullable = false)
    val openedAt: Instant = Instant.now(),

    @Column(name = "closed_at")
    var closedAt: Instant? = null,

    @Column(name = "initial_amount", nullable = false, precision = 10, scale = 2)
    val initialAmount: BigDecimal,

    @Column(name = "final_amount", precision = 10, scale = 2)
    var finalAmount: BigDecimal? = null,

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    var status: ShiftStatus = ShiftStatus.OPEN
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CashShift) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "CashShift(id=$id, storeId=$storeId, userId=$userId, status=$status)"
}
