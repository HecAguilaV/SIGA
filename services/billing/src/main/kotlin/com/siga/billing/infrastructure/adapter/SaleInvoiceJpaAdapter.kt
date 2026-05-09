package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.SaleInvoice
import com.siga.billing.domain.port.SaleInvoiceRepositoryPort
import com.siga.billing.infrastructure.mapper.SaleInvoiceMapper
import com.siga.billing.repository.SaleInvoiceRepository
import org.springframework.stereotype.Component

/**
 * JPA Adapter implementing [SaleInvoiceRepositoryPort].
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class SaleInvoiceJpaAdapter(
    private val saleInvoiceRepository: SaleInvoiceRepository
) : SaleInvoiceRepositoryPort {

    override fun save(invoice: SaleInvoice): SaleInvoice {
        val entity = SaleInvoiceMapper.toEntity(invoice)
        val saved = saleInvoiceRepository.save(entity)
        return SaleInvoiceMapper.toDomain(saved)
    }
}
