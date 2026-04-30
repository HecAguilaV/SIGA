package com.siga.billing.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Invoice issued for a subscription purchase.
 */
@Entity
@Table(name = "invoices", schema = "commercial")
class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    var invoiceNumber: String,

    @Column(name = "customer_id", nullable = false)
    var customerId: UUID,

    @Column(name = "user_name", nullable = false, length = 255)
    var userName: String,

    @Column(name = "user_email", nullable = false, length = 255)
    var userEmail: String,

    @Column(name = "plan_id", nullable = false)
    var planId: UUID,

    @Column(name = "plan_name", nullable = false, length = 255)
    var planName: String,

    @Column(name = "price_uf", nullable = false, precision = 10, scale = 2)
    var priceUF: BigDecimal,

    @Column(name = "price_clp", precision = 12, scale = 2)
    var priceCLP: BigDecimal? = null,

    @Column(nullable = false, length = 10)
    var unit: String = "UF",

    @Column(name = "purchased_at", nullable = false)
    var purchasedAt: Instant = Instant.now(),

    @Column(name = "due_date")
    var dueDate: Instant? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: InvoiceStatus = InvoiceStatus.PAID,

    @Column(name = "payment_method", length = 100)
    var paymentMethod: String? = null,

    @Column(name = "last_4_digits", length = 4)
    var last4Digits: String? = null,

    @Column(name = "subscription_id")
    var subscriptionId: UUID? = null,

    @Column(name = "payment_id")
    var paymentId: UUID? = null,

    @Column(precision = 10, scale = 2)
    var tax: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PrePersist
    fun onPrePersist() {
        val now = Instant.now()
        if (createdAt == null) createdAt = now
        updatedAt = now
        if (purchasedAt == null) purchasedAt = now
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Invoice) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Invoice(id=$id, invoiceNumber=$invoiceNumber, status=$status)"
}

/**
 * Possible invoice statuses.
 */
enum class InvoiceStatus {
    PENDING, PAID, EXPIRED, CANCELLED
}