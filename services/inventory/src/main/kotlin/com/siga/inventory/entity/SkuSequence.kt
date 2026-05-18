package com.siga.inventory.entity

import jakarta.persistence.*

/**
 * JPA Entity for the [sku_sequences] table.
 *
 * Stores auto-incrementing counters per prefix for SKU generation.
 * The [prefix] is the primary key (e.g., "CAF" for Café category).
 */
@Entity
@Table(name = "sku_sequences", schema = "inventory")
class SkuSequence(
    @Id
    @Column(length = 10)
    var prefix: String = "",

    @Column(name = "current_value", nullable = false)
    var currentValue: Long = 0,

    @Column(name = "tenant_id", nullable = false)
    var tenantId: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SkuSequence) return false
        return prefix == other.prefix
    }

    override fun hashCode(): Int = prefix.hashCode()

    override fun toString(): String = "SkuSequence(prefix=$prefix, currentValue=$currentValue)"
}
