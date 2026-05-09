package com.siga.billing.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * JPA Entity for invoices generated from POS sales (SAGA step 4).
 *
 * Distinguished from subscription [Invoice] by the presence of a `sale_id`
 * referencing the originating sale in the Sales service.
 */
@Entity
@Table(name = "sale_invoices", schema = "billing")
class SaleInvoiceEntity(
    @Id
    var id: UUID? = null,

    @Column(name = "sale_id", nullable = false)
    var saleId: UUID,

    @Column(name = "store_id", nullable = false)
    var storeId: UUID,

    @Column(name = "user_id")
    var userId: UUID? = null,

    @Column(nullable = false, precision = 12, scale = 2)
    var total: BigDecimal,

    @Column(columnDefinition = "jsonb")
    var items: String? = null,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: SaleInvoiceStatus = SaleInvoiceStatus.COMPLETED,

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
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SaleInvoiceEntity) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "SaleInvoiceEntity(id=$id, saleId=$saleId, status=$status)"
}

enum class SaleInvoiceStatus {
    COMPLETED, CANCELLED
}
