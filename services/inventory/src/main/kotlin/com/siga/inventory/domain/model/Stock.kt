package com.siga.inventory.domain.model

import java.util.UUID

/**
 * Pure domain model representing the stock quantity of a product in a specific store.
 *
 * WHY SAGA: In our distributed transaction (SAGA Choreography), stock reservation
 * is the "Resource" being locked. This model represents the state of that resource.
 * It doesn't know it's stored in a table `stocks` or that it's part of a SAGA.
 */
data class Stock(
    val productId: UUID,
    val storeId: UUID,
    val quantity: Int
)
