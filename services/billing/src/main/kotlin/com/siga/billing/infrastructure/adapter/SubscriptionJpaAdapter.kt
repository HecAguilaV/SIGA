package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import com.siga.billing.domain.port.SubscriptionRepositoryPort
import com.siga.billing.entity.SubscriptionEntity
import com.siga.billing.infrastructure.mapper.SubscriptionMapper
import com.siga.billing.repository.SubscriptionRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing the SubscriptionRepositoryPort.
 */
@Component
class SubscriptionJpaAdapter(
    private val subscriptionRepository: SubscriptionRepository
) : SubscriptionRepositoryPort {

    override fun findById(id: UUID): Subscription? {
        val entity = subscriptionRepository.findById(id)
        return if (entity.isPresent) SubscriptionMapper.toDomain(entity.get()) else null
    }

    override fun save(subscription: Subscription): Subscription {
        val entity = SubscriptionMapper.toEntity(subscription)
        val savedEntity = subscriptionRepository.save(entity)
        return SubscriptionMapper.toDomain(savedEntity)
    }

    override fun findByCustomerId(customerId: UUID): List<Subscription> {
        return subscriptionRepository.findByCustomerId(customerId).map { SubscriptionMapper.toDomain(it) }
    }

    override fun findByCustomerIdAndStatus(customerId: UUID, status: SubscriptionStatus): List<Subscription> {
        return subscriptionRepository.findByCustomerIdAndStatus(customerId, 
            com.siga.billing.entity.SubscriptionStatus.valueOf(status.name)
        ).map { SubscriptionMapper.toDomain(it) }
    }

    override fun findByCustomerIdAndStatusIn(customerId: UUID, statuses: List<SubscriptionStatus>): List<Subscription> {
        val entityStatuses = statuses.map { com.siga.billing.entity.SubscriptionStatus.valueOf(it.name) }
        return subscriptionRepository.findByCustomerIdAndStatusIn(customerId, entityStatuses).map { SubscriptionMapper.toDomain(it) }
    }
}
