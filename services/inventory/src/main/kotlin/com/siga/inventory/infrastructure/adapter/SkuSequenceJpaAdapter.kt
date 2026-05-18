package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.port.SkuSequencePort
import com.siga.inventory.entity.SkuSequence
import com.siga.inventory.repository.SkuSequenceRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * JPA Adapter for SKU sequence generation.
 *
 * Implements [SkuSequencePort] by reading/writing the [sku_sequences] table.
 * Uses `@Transactional` to ensure atomic read-increment-save within a single
 * database transaction. This prevents two callers from getting the same sequence
 * value when called concurrently.
 */
@Component
class SkuSequenceJpaAdapter(
    private val skuSequenceRepository: SkuSequenceRepository
) : SkuSequencePort {

    @Transactional
    override fun nextSequence(tenantId: Long, prefix: String): Int {
        val sequence = skuSequenceRepository.findById(prefix).orElse(null)
        return if (sequence != null) {
            val next = sequence.currentValue + 1
            sequence.currentValue = next
            sequence.tenantId = tenantId
            skuSequenceRepository.save(sequence)
            next.toInt()
        } else {
            val newSeq = SkuSequence(
                prefix = prefix,
                currentValue = 1,
                tenantId = tenantId
            )
            skuSequenceRepository.save(newSeq)
            1
        }
    }
}
