package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus
import com.siga.billing.domain.port.PaymentRepositoryPort
import com.siga.billing.entity.PaymentEntity
import com.siga.billing.infrastructure.mapper.PaymentMapper
import com.siga.billing.repository.PaymentRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing the PaymentRepositoryPort.
 */
@Component
class PaymentJpaAdapter(
    private val paymentRepository: PaymentRepository
) : PaymentRepositoryPort {

    override fun findById(id: UUID): Payment? {
        val entity = paymentRepository.findById(id)
        return if (entity.isPresent) PaymentMapper.toDomain(entity.get()) else null
    }

    override fun save(payment: Payment): Payment {
        val entity = PaymentMapper.toEntity(payment)
        val savedEntity = paymentRepository.save(entity)
        return PaymentMapper.toDomain(savedEntity)
    }

    override fun findByCustomerId(customerId: UUID): List<Payment> {
        return paymentRepository.findByCustomerId(customerId).map { PaymentMapper.toDomain(it) }
    }

    override fun findBySubscriptionId(subscriptionId: UUID): List<Payment> {
        return paymentRepository.findBySubscriptionId(subscriptionId).map { PaymentMapper.toDomain(it) }
    }

    override fun findByStatus(status: PaymentStatus): List<Payment> {
        val entityStatus = com.siga.billing.entity.PaymentStatus.valueOf(status.name)
        return paymentRepository.findByStatus(entityStatus).map { PaymentMapper.toDomain(it) }
    }
}
