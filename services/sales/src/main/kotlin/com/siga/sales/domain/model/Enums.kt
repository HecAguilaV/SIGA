package com.siga.sales.domain.model

/**
 * Possible statuses of a sale throughout its lifecycle.
 *
 * - PENDING: Sale created, waiting for stock reservation (SAGA).
 * - COMPLETED: Stock reserved and sale confirmed.
 * - CANCELLED: Stock reservation failed — compensated (SAGA).
 * - TIMEOUT: No response from Inventory within the configured window.
 */
enum class SaleStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    TIMEOUT
}

/**
 * Statuses of a cash shift (opening/closing).
 */
enum class ShiftStatus {
    OPEN,
    CLOSED
}

/**
 * Statuses of a transaction at the point of sale.
 */
enum class TransactionStatus {
    COMPLETED,
    CANCELLED,
    REFUNDED
}

/**
 * Types of legal tax documents issued per Chilean SII regulations.
 *
 * - BOLETA: For end consumers (no customer reference required).
 * - FACTURA: For businesses (customer reference mandatory).
 */
enum class DocumentType {
    BOLETA,
    FACTURA
}

/**
 * Statuses of a legal tax document.
 */
enum class DocumentStatus {
    EMITTED,
    ANNULLED,
    ERROR
}
