package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

/**
 * Commercial portal invoice (commercial schema).
 * Managed by the sales service for SaaS billing records.
 */
@Entity
@Table(name = "invoices", schema = "billing")
class Invoice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    val invoiceNumber: String,

    @Column(name = "user_id", nullable = false)
    val userId: Int,

    @Column(name = "user_name", nullable = false, length = 255)
    val userName: String,

    @Column(name = "user_email", nullable = false, length = 255)
    val userEmail: String,

    @Column(name = "plan_id", nullable = false)
    val planId: Int,

    @Column(name = "plan_name", nullable = false, length = 255)
    val planName: String,

    @Column(name = "price_uf", nullable = false, precision = 10, scale = 2)
    val priceUF: BigDecimal,

    @Column(name = "price_clp", precision = 12, scale = 2)
    val priceCLP: BigDecimal? = null,

    @Column(nullable = false, length = 10)
    val unit: String = "UF",

    @Column(name = "purchased_at", nullable = false)
    val purchasedAt: Instant,

    @Column(name = "due_date")
    val dueDate: Instant? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: InvoiceStatus = InvoiceStatus.PAID,

    @Column(name = "payment_method", length = 100)
    val paymentMethod: String? = null,

    @Column(name = "last_4_digits", length = 4)
    val last4Digits: String? = null,

    @Column(name = "subscription_id")
    val subscriptionId: Int? = null,

    @Column(name = "payment_id")
    val paymentId: Int? = null,

    @Column(precision = 10, scale = 2)
    val tax: BigDecimal? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Invoice) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Invoice(id=$id, number=$invoiceNumber, status=$status)"
}
