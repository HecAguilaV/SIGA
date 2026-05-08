package com.siga.sales.domain.model

import java.util.UUID

/**
 * Available payment method for POS transactions.
 *
 * Examples: Efectivo, Tarjeta Débito, Tarjeta Crédito, Transferencia.
 */
data class PaymentMethod(
    val id: UUID,
    val name: String,
    val isActive: Boolean
)
