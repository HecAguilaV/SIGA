package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class SubscriptionEntityTest {

    @Test
    fun `create subscription entity with all fields`() {
        val id = UUID.randomUUID()
        val startsAt = Instant.now()
        val endsAt = startsAt.plusSeconds(365 * 86400)

        val entity = SubscriptionEntity(
            id = id,
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.ANNUAL,
            status = SubscriptionStatus.ACTIVE,
            startsAt = startsAt,
            endsAt = endsAt,
            updatedAt = Instant.now()
        )

        assertEquals(id, entity.id)
        assertNotNull(entity.customerId)
        assertNotNull(entity.planId)
        assertEquals(BillingPeriod.ANNUAL, entity.period)
        assertEquals(SubscriptionStatus.ACTIVE, entity.status)
        assertEquals(startsAt, entity.startsAt)
        assertEquals(endsAt, entity.endsAt)
    }

    @Test
    fun `create subscription entity with defaults`() {
        val entity = SubscriptionEntity(
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID()
        )

        assertNull(entity.id)
        assertEquals(BillingPeriod.MONTHLY, entity.period)
        assertEquals(SubscriptionStatus.ACTIVE, entity.status)
        assertNull(entity.endsAt)
        assertNotNull(entity.startsAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `subscription entity with suspended status`() {
        val entity = SubscriptionEntity(
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            status = SubscriptionStatus.SUSPENDED
        )

        assertEquals(SubscriptionStatus.SUSPENDED, entity.status)
    }

    @Test
    fun `subscription entity with expired status`() {
        val entity = SubscriptionEntity(
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            status = SubscriptionStatus.EXPIRED
        )

        assertEquals(SubscriptionStatus.EXPIRED, entity.status)
    }

    @Test
    fun `subscription entity equals by id`() {
        val id = UUID.randomUUID()
        val entity1 = SubscriptionEntity(id = id, customerId = UUID.randomUUID(), planId = UUID.randomUUID())
        val entity2 = SubscriptionEntity(id = id, customerId = UUID.randomUUID(), planId = UUID.randomUUID())

        assertEquals(entity1, entity2)
        assertEquals(entity1.hashCode(), entity2.hashCode())
    }

    @Test
    fun `subscription entity inequality on different id`() {
        val entity1 = SubscriptionEntity(id = UUID.randomUUID(), customerId = UUID.randomUUID(), planId = UUID.randomUUID())
        val entity2 = SubscriptionEntity(id = UUID.randomUUID(), customerId = UUID.randomUUID(), planId = UUID.randomUUID())

        assertNotEquals(entity1, entity2)
    }

    @Test
    fun `subscription entity toString`() {
        val entity = SubscriptionEntity(id = UUID.randomUUID(), customerId = UUID.randomUUID(), planId = UUID.randomUUID())
        val toString = entity.toString()

        assertTrue(toString.contains("SubscriptionEntity"))
        assertTrue(toString.contains("ACTIVE"))
        assertTrue(toString.contains("MONTHLY"))
    }

    @Test
    fun `subscription entity onPrePersist preserves existing startsAt`() {
        val now = Instant.now()
        val entity = SubscriptionEntity(
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            startsAt = now
        )

        entity.onPrePersist()

        assertEquals(now, entity.startsAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `subscription entity onPreUpdate updates updatedAt`() {
        val entity = SubscriptionEntity(
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID()
        )
        val originalUpdatedAt = entity.updatedAt

        entity.onPreUpdate()

        assertTrue(entity.updatedAt >= originalUpdatedAt)
    }
}
