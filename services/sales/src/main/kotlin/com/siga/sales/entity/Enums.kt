package com.siga.sales.entity

/**
 * Possible statuses of a sale.
 */
enum class SaleStatus {
    COMPLETED,
    CANCELLED,
    PENDING
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
 * Statuses of a commercial portal invoice.
 */
enum class InvoiceStatus {
    PENDING,
    PAID,
    EXPIRED,
    CANCELLED
}
