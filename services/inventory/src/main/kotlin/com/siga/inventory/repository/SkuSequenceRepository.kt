package com.siga.inventory.repository

import com.siga.inventory.entity.SkuSequence
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for [sku_sequences] table.
 * Keyed by [prefix] (VARCHAR 10).
 */
@Repository
interface SkuSequenceRepository : JpaRepository<SkuSequence, String>
