package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "pos_transactions", schema = "sales")
class PosTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "sale_id", nullable = false)
    val saleId: Int,

    @Column(name = "cash_shift_id", nullable = false)
    val cashShiftId: Int,

    @Column(name = "payment_method_id", nullable = false)
    val paymentMethodId: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal,

    @Column(precision = 10, scale = 2)
    val change: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: TransactionStatus = TransactionStatus.COMPLETED
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PosTransaction) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "PosTransaction(id=$id, saleId=$saleId, amount=$amount, status=$status)"
}
