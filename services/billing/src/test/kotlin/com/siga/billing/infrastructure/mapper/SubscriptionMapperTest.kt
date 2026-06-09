package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.BillingPeriod as DomainBillingPeriod
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus as DomainSubscriptionStatus
import com.siga.billing.entity.BillingPeriod as EntityBillingPeriod
import com.siga.billing.entity.SubscriptionEntity
import com.siga.billing.entity.SubscriptionStatus as EntitySubscriptionStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class SubscriptionMapperTest {

    @Test
    fun `toDomain maps entity to domain model`() {
        val id = UUID.randomUUID()
        val startsAt = Instant.now()
        val endsAt = startsAt.plusSeconds(365 * 86400)

        val entity = SubscriptionEntity(
            id = id,
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = EntityBillingPeriod.ANNUAL,
            status = EntitySubscriptionStatus.ACTIVE,
            startsAt = startsAt,
            endsAt = endsAt,
            updatedAt = Instant.now()
        )

        val domain = SubscriptionMapper.toDomain(entity)

        assertEquals(id, domain.id)
        assertEquals(entity.customerId, domain.customerId)
        assertEquals(entity.planId, domain.planId)
        assertEquals(DomainBillingPeriod.ANNUAL, domain.period)
        assertEquals(DomainSubscriptionStatus.ACTIVE, domain.status)
        assertEquals(startsAt, domain.startsAt)
        assertEquals(endsAt, domain.endsAt)
    }

    @Test
    fun `toDomain maps entity with null endsAt`() {
        val entity = SubscriptionEntity(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = EntityBillingPeriod.MONTHLY,
            status = EntitySubscriptionStatus.ACTIVE,
            startsAt = Instant.now(),
            endsAt = null,
            updatedAt = Instant.now()
        )

        val domain = SubscriptionMapper.toDomain(entity)

        assertNull(domain.endsAt)
        assertEquals(DomainBillingPeriod.MONTHLY, domain.period)
    }

    @Test
    fun `toDomain assigns random id when entity id is null`() {
        val entity = SubscriptionEntity(
            id = null,
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            startsAt = Instant.now()
        )

        val domain = SubscriptionMapper.toDomain(entity)

        assertNotNull(domain.id)
    }

    @Test
    fun `toEntity maps domain model to entity`() {
        val id = UUID.randomUUID()
        val startsAt = Instant.now()
        val endsAt = startsAt.plusSeconds(30 * 86400)

        val domain = Subscription(
            id = id,
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = DomainBillingPeriod.MONTHLY,
            status = DomainSubscriptionStatus.ACTIVE,
            startsAt = startsAt,
            endsAt = endsAt
        )

        val entity = SubscriptionMapper.toEntity(domain)

        assertEquals(id, entity.id)
        assertEquals(domain.customerId, entity.customerId)
        assertEquals(domain.planId, entity.planId)
        assertEquals(EntityBillingPeriod.MONTHLY, entity.period)
        assertEquals(EntitySubscriptionStatus.ACTIVE, entity.status)
        assertEquals(startsAt, entity.startsAt)
        assertEquals(endsAt, entity.endsAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `toEntity maps domain with null endsAt and suspended status`() {
        val domain = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = DomainBillingPeriod.ANNUAL,
            status = DomainSubscriptionStatus.SUSPENDED,
            startsAt = Instant.now(),
            endsAt = null
        )

        val entity = SubscriptionMapper.toEntity(domain)

        assertNull(entity.endsAt)
        assertEquals(EntityBillingPeriod.ANNUAL, entity.period)
        assertEquals(EntitySubscriptionStatus.SUSPENDED, entity.status)
    }

    @Test
    fun `maps all billing periods correctly`() {
        val periods = listOf(
            Pair(EntityBillingPeriod.MONTHLY, DomainBillingPeriod.MONTHLY),
            Pair(EntityBillingPeriod.ANNUAL, DomainBillingPeriod.ANNUAL)
        )

        for ((entityPeriod, domainPeriod) in periods) {
            val entity = SubscriptionEntity(
                id = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                planId = UUID.randomUUID(),
                period = entityPeriod,
                status = EntitySubscriptionStatus.ACTIVE,
                startsAt = Instant.now()
            )
            val domain = SubscriptionMapper.toDomain(entity)
            assertEquals(domainPeriod, domain.period)

            val backToEntity = SubscriptionMapper.toEntity(domain)
            assertEquals(entityPeriod, backToEntity.period)
        }
    }

    @Test
    fun `maps all subscription statuses correctly`() {
        val statuses = listOf(
            Pair(EntitySubscriptionStatus.ACTIVE, DomainSubscriptionStatus.ACTIVE),
            Pair(EntitySubscriptionStatus.SUSPENDED, DomainSubscriptionStatus.SUSPENDED),
            Pair(EntitySubscriptionStatus.CANCELLED, DomainSubscriptionStatus.CANCELLED),
            Pair(EntitySubscriptionStatus.EXPIRED, DomainSubscriptionStatus.EXPIRED)
        )

        for ((entityStatus, domainStatus) in statuses) {
            val entity = SubscriptionEntity(
                id = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                planId = UUID.randomUUID(),
                period = EntityBillingPeriod.MONTHLY,
                status = entityStatus,
                startsAt = Instant.now()
            )
            val domain = SubscriptionMapper.toDomain(entity)
            assertEquals(domainStatus, domain.status)

            val backToEntity = SubscriptionMapper.toEntity(domain)
            assertEquals(entityStatus, backToEntity.status)
        }
    }

    @Test
    fun `roundtrip domain to entity to domain`() {
        val original = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = DomainBillingPeriod.ANNUAL,
            status = DomainSubscriptionStatus.CANCELLED,
            startsAt = Instant.now(),
            endsAt = Instant.now().plusSeconds(30 * 86400)
        )

        val entity = SubscriptionMapper.toEntity(original)
        val domain = SubscriptionMapper.toDomain(entity)

        assertEquals(original.id, domain.id)
        assertEquals(original.customerId, domain.customerId)
        assertEquals(original.planId, domain.planId)
        assertEquals(original.period, domain.period)
        assertEquals(original.status, domain.status)
        assertEquals(original.startsAt, domain.startsAt)
        assertEquals(original.endsAt, domain.endsAt)
    }
}
