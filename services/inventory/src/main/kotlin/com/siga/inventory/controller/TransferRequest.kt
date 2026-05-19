package com.siga.inventory.controller

import java.util.UUID

/**
 * Request DTO for stock transfer between stores.
 */
data class TransferRequest(
    val productId: UUID,
    val originStoreId: UUID,
    val destinationStoreId: UUID,
    val quantity: Int
)
