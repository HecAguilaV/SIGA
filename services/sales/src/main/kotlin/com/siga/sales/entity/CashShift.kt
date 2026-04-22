package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "cash_shifts", schema = "sales")
class CashShift(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "store_id", nullable = false)
    val storeId: Int,

    @Column(name = "user_id", nullable = false)
    val userId: Int,

    @Column(name = "opened_at", nullable = false)
    val openedAt: Instant = Instant.now(),

    @Column(name = "closed_at")
    var closedAt: Instant? = null,

    @Column(name = "opening_balance", nullable = false, precision = 10, scale = 2)
    val openingBalance: BigDecimal,

    @Column(name = "closing_balance", precision = 10, scale = 2)
    var closingBalance: BigDecimal? = null,

    @Column(name = "actual_balance", precision = 10, scale = 2)
    var actualBalance: BigDecimal? = null,

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    var status: ShiftStatus = ShiftStatus.OPEN
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CashShift) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "CashShift(id=$id, storeId=$storeId, userId=$userId, status=$status)"
}
