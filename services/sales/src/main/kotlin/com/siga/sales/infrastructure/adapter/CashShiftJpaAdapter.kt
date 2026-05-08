package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.port.CashShiftRepositoryPort
import com.siga.sales.entity.CashShiftEntity
import com.siga.sales.infrastructure.mapper.CashShiftMapper
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for CashShift.
 */
@Component
class CashShiftJpaAdapter(
    private val cashShiftRepository: com.siga.sales.repository.CashShiftRepository,
    private val cashShiftMapper: CashShiftMapper
) : CashShiftRepositoryPort {

    override fun findById(id: UUID): CashShift? {
        return cashShiftRepository.findById(id).orElse(null)?.let { cashShiftMapper.toDomain(it) }
    }

    override fun save(shift: CashShift): CashShift {
        val entity = cashShiftMapper.toEntity(shift)
        return cashShiftMapper.toDomain(cashShiftRepository.save(entity))
    }

    override fun findAll(): List<CashShift> {
        return cashShiftRepository.findAll().map { cashShiftMapper.toDomain(it) }
    }

    override fun findByStoreId(storeId: UUID): List<CashShift> {
        return cashShiftRepository.findByStoreId(storeId).map { cashShiftMapper.toDomain(it) }
    }

    override fun findByUserId(userId: UUID): CashShift? {
        return cashShiftRepository.findByUserId(userId)?.let { cashShiftMapper.toDomain(it) }
    }
}
