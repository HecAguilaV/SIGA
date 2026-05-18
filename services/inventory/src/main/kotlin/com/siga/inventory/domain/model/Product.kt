package com.siga.inventory.domain.model

import java.math.BigDecimal
import java.util.UUID

/**
 * Pure domain model representing a Product in the inventory.
 *
 * WHY HEXAGONAL: In Hexagonal Architecture, the Domain layer must be "Framework Agnostic".
 * This class has ZERO dependencies on Spring, JPA, or PostgreSQL. It is the "Heart of the Business".
 * If we switch from JPA to gRPC or REST, this class remains UNTOUCHED.
 *
 * WHY THIS EXISTS: The previous [com.siga.inventory.entity.Product] was an "Anemic Model"
 * (just data + JPA annotations). By separating Domain from Infrastructure, we allow business
 * logic (e.g., checking stock levels) to live HERE, making it testable without a database.
 *
 * @property id Unique identifier (UUID v4 as per Ley 21.719).
 * @property name Commercial name of the product.
 * @property unitPrice Price per unit (using BigDecimal for financial precision).
 */
data class Product(
    val id: UUID,
    val name: String,
    val description: String?,
    val categoryId: UUID?,
    val barcode: String?,
    val unitPrice: BigDecimal,
    val isActive: Boolean,
    val commercialUserId: UUID?,
    val sku: String? = null,
    val unitType: String? = null,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant
)
