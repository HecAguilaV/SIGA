package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import com.siga.sales.infrastructure.mapper.SaleMapper
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter for Sale.
 * Implements the domain port using Spring Data JPA.
 *
 * Uses the same pattern as other adapters in the project
 * (billing, inventory) — delegates persistence to Spring Data's
 * built-in transaction management via [save] on the repository.
 */
@Component
class SaleJpaAdapter(
    private val saleRepository: com.siga.sales.repository.SaleRepository,
    private val saleMapper: SaleMapper
) : SaleRepositoryPort {

    override fun findById(id: UUID): Sale? {
        return saleRepository.findById(id).orElse(null)?.let { saleMapper.toDomain(it) }
    }

    override fun save(sale: Sale): Sale {
        val entity = saleMapper.toEntity(sale)
        val saved = saleRepository.save(entity)
        return saleMapper.toDomain(saved)
    }

    override fun findAll(): List<Sale> {
        return saleRepository.findAll().map { saleMapper.toDomain(it) }
    }

    override fun findByStoreId(storeId: UUID): List<Sale> {
        return saleRepository.findByStoreId(storeId).map { saleMapper.toDomain(it) }
    }

    override fun findByUserId(userId: UUID): List<Sale> {
        return saleRepository.findByUserId(userId).map { saleMapper.toDomain(it) }
    }

    override fun findByStatus(status: SaleStatus): List<Sale> {
        return saleRepository.findByStatus(status).map { saleMapper.toDomain(it) }
    }

    override fun aggregateSalesByDay(): List<com.siga.sales.repository.DailySalesProjection> {
        return saleRepository.aggregateSalesByDay()
    }
}
