package com.siga.sales.controller

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleItem

/**
 * Request DTO for creating a sale.
 *
 * Wraps the sale and its items into a single JSON body
 * because Spring does not support multiple @RequestBody parameters.
 *
 * Expected JSON:
 * ```json
 * {
 *   "sale": { "storeId": "...", "total": 150.00 },
 *   "items": [
 *     { "productId": "...", "quantity": 2, "unitPrice": 75.00, "subtotal": 150.00 }
 *   ]
 * }
 * ```
 */
data class CreateSaleRequest(
    val sale: Sale,
    val items: List<SaleItem>
)
