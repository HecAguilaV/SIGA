package com.siga.inventory.domain.model

import java.util.UUID

/**
 * Domain model for inventory movements (Sales, Adjustments, etc.).
 *
 * WHY HEXAGONAL: This is the "Audit Trail" of the domain.
 * The infrastructure layer (JPA) will persist this, but the DOMAIN defines what a movement is.
 */
data class Movement(
    val id: UUID?,
    val productId: UUID,
    val storeId: UUID,
    val type: MovementType,
    val quantity: Int,
    val previousQuantity: Int,
    val newQuantity: Int,
    val userId: UUID?,
    val saleId: UUID?,
    val observations: String?
)

enum class MovementType { SALE, ADJUSTMENT, ENTRY }
