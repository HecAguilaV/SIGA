package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.BillingPeriod
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import com.siga.billing.entity.BillingPeriod as EntityBillingPeriod
import com.siga.billing.entity.SubscriptionEntity
import com.siga.billing.entity.SubscriptionStatus as EntitySubscriptionStatus
import java.time.Instant
import java.util.UUID

object SubscriptionMapper {
    fun toDomain(entity: SubscriptionEntity): Subscription {
        return Subscription(
            id = entity.id ?: UUID.randomUUID(),
            customerId = entity.customerId,
            planId = entity.planId,
            period = mapPeriodToDomain(entity.period),
            status = mapStatusToDomain(entity.status),
            startsAt = entity.startsAt,
            endsAt = entity.endsAt
        )
    }

    fun toEntity(model: Subscription): SubscriptionEntity {
        return SubscriptionEntity(
            id = model.id,
            customerId = model.customerId,
            planId = model.planId,
            period = mapPeriodToEntity(model.period),
            status = mapStatusToEntity(model.status),
            startsAt = model.startsAt,
            endsAt = model.endsAt,
            updatedAt = Instant.now()
        )
    }

    private fun mapPeriodToDomain(period: EntityBillingPeriod): BillingPeriod {
        return BillingPeriod.valueOf(period.name)
    }

    private fun mapPeriodToEntity(period: BillingPeriod): EntityBillingPeriod {
        return EntityBillingPeriod.valueOf(period.name)
    }

    private fun mapStatusToDomain(status: EntitySubscriptionStatus): SubscriptionStatus {
        return SubscriptionStatus.valueOf(status.name)
    }

    private fun mapStatusToEntity(status: SubscriptionStatus): EntitySubscriptionStatus {
        return EntitySubscriptionStatus.valueOf(status.name)
    }
}
