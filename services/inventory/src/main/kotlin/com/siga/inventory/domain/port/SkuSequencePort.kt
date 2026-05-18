package com.siga.inventory.domain.port

/**
 * Port for generating sequential numbers used in SKU generation.
 *
 * WHY A PORT: The sequence logic (e.g., `SELECT NEXTVAL` or incrementing
 * a counter row) is infrastructure-specific. The domain only knows
 * it can ask for the next integer in a sequence identified by [prefix].
 */
interface SkuSequencePort {
    fun nextSequence(tenantId: Long, prefix: String): Int
}
