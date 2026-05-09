package com.siga.billing.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Pure domain model for a sale-generated invoice.
 *
 * Created when Sales emits a [SaleCompletedEvent] after stock is reserved.
 * No JPA, no Spring dependencies.
 */
data class SaleInvoice(
    val id: UUID? = null,
    val saleId: UUID,
    val storeId: UUID,
    val userId: UUID? = null,
    val total: BigDecimal,
    val items: String? = null,
    val status: SaleInvoiceStatus = SaleInvoiceStatus.COMPLETED,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null
)

enum class SaleInvoiceStatus {
    COMPLETED, CANCELLED
}
