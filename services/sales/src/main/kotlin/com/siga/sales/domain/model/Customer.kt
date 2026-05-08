package com.siga.sales.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Customer of a SME (PyME) that purchases goods or services.
 *
 * Represents the end client of our SME customers. Used primarily
 * for Factura generation where a customer reference is mandatory
 * per Chilean tax law (SII).
 *
 * @see SaleDocument the tax document that references this customer
 */
data class Customer(
    val id: UUID,
    val taxId: String?,
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    val createdAt: Instant
)
