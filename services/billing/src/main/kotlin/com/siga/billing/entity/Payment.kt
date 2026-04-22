package com.siga.billing.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

/**
 * Record of a payment made by the customer.
 */
@Entity
@Table(name = "payments", schema = "commercial")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "subscription_id", nullable = false)
    var subscriptionId: Int,

    @Column(name = "customer_id", nullable = false)
    var customerId: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal,

    @Column(name = "payment_method", length = 50)
    var paymentMethod: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.PENDING,

    @Column(length = 100)
    var reference: String? = null,

    @Column(name = "paid_at")
    var paidAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Payment) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Payment(id=$id, amount=$amount, status=$status)"
}

/**
 * Payment statuses.
 */
enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}