package com.siga.sales.entity

import com.siga.sales.domain.model.TransactionStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * JPA Entity for PosTransaction.
 * Records a payment transaction within a CashShift.
 *
 * @see com.siga.sales.domain.model.PosTransaction the domain model
 */
@Entity
@Table(name = "pos_transactions", schema = "sales")
class PosTransactionEntity(
    @Id
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
        if (other !is PosTransactionEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "PosTransactionEntity(id=$id, saleId=$saleId, amount=$amount, status=$status)"
}
