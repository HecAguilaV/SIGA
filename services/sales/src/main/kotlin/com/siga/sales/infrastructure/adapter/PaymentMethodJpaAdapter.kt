package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.PaymentMethod
import com.siga.sales.domain.port.PaymentMethodRepositoryPort
import com.siga.sales.entity.PaymentMethodEntity
import com.siga.sales.infrastructure.mapper.PaymentMethodMapper
import org.springframework.stereotype.Component

/**
 * JPA Adapter for PaymentMethod.
 */
@Component
class PaymentMethodJpaAdapter(
    private val paymentMethodRepository: com.siga.sales.repository.PaymentMethodRepository,
    private val paymentMethodMapper: PaymentMethodMapper
) : PaymentMethodRepositoryPort {

    override fun findById(id: UUID): PaymentMethod? {
        return paymentMethodRepository.findById(id).orElse(null)?.let { paymentMethodMapper.toDomain(it) }
    }

    override fun save(paymentMethod: PaymentMethod): PaymentMethod {
        val entity = paymentMethodMapper.toEntity(paymentMethod)
        return paymentMethodMapper.toDomain(paymentMethodRepository.save(entity))
    }

    override fun findAll(): List<PaymentMethod> {
        return paymentMethodRepository.findAll().map { paymentMethodMapper.toDomain(it) }
    }
}
