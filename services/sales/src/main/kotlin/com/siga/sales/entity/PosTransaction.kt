package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Records a payment transaction within a [CashShift].
 *
 * Each POS transaction links a [Sale] to a [PaymentMethod] and tracks
 * the amount paid. Multiple transactions can exist per sale
 * (e.g., split payments).
 */
@Entity
@Table(name = "pos_transactions", schema = "sales")
class PosTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "sale_id", nullable = false)
    val saleId: UUID,

    @Column(name = "shift_id", nullable = false)
    val shiftId: UUID,

    @Column(name = "payment_method_id", nullable = false)
    val paymentMethodId: UUID,

    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal,

    @Column(name = "last_4_digits", length = 4)
    val last4Digits: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: TransactionStatus = TransactionStatus.COMPLETED
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PosTransaction) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "PosTransaction(id=$id, saleId=$saleId, amount=$amount, status=$status)"
}
