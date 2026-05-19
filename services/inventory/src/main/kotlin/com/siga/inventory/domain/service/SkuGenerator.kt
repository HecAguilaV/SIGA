package com.siga.inventory.domain.service

import com.siga.inventory.domain.port.SkuSequencePort

/**
 * Domain service for generating SKUs (Stock Keeping Units).
 *
 * WHY A DOMAIN SERVICE: SKU generation follows business rules (category prefix extraction,
 * sequential numbering per tenant) that belong in the Domain layer, not in a Use Case
 * or infrastructure adapter. This service encapsulates those rules and delegates
 * the sequence increment to a port.
 *
 * Format: {PREFIX}-{SEQUENCE:04d}
 *   - PREFIX: First 3 uppercase alpha characters of the category name, or "GEN" if null/blank
 *   - SEQUENCE: Zero-padded 4-digit number from [SkuSequencePort.nextSequence]
 */
class SkuGenerator(
    private val sequencePort: SkuSequencePort
) {
    /**
     * Generates the next SKU for a given tenant and optional category name.
     *
     * @param tenantId The tenant identifier (maps to commercial_user_id).
     * @param categoryName The category name to derive the prefix from. If null or blank, "GEN" is used.
     * @return SKU string in format "{PREFIX}-{SEQUENCE:04d}".
     */
    fun nextSku(tenantId: Long, categoryName: String?): String {
        val prefix = extractPrefix(categoryName)
        val sequence = sequencePort.nextSequence(tenantId, prefix)
        return "$prefix-${sequence.toString().padStart(4, '0')}"
    }

    /**
     * Extracts the SKU prefix from a category name.
     *
     * Rules:
     * 1. If null or blank → "GEN"
     * 2. Uppercase the input
     * 3. Keep only alphabetic characters
     * 4. Take at most 3 characters
     *
     * Visible for testing.
     */
    fun extractPrefix(categoryName: String?): String {
        if (categoryName.isNullOrBlank()) return "GEN"
        return categoryName
            .uppercase()
            .filter { it.isLetter() }
            .take(3)
    }
}
