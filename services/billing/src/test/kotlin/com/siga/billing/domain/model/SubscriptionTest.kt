package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class SubscriptionTest {

    @Test
    fun `create subscription with all fields`() {
        val id = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val planId = UUID.randomUUID()
        val now = Instant.now()

        val subscription = Subscription(
            id = id,
            customerId = customerId,
            planId = planId,
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = now,
            endsAt = null
        )

        assertEquals(id, subscription.id)
        assertEquals(customerId, subscription.customerId)
        assertEquals(planId, subscription.planId)
        assertEquals(BillingPeriod.MONTHLY, subscription.period)
        assertEquals(SubscriptionStatus.ACTIVE, subscription.status)
        assertEquals(now, subscription.startsAt)
        assertNull(subscription.endsAt)
    }

    @Test
    fun `create subscription with annual period and end date`() {
        val startsAt = Instant.now()
        val endsAt = startsAt.plusSeconds(365 * 86400)

        val subscription = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.ANNUAL,
            status = SubscriptionStatus.ACTIVE,
            startsAt = startsAt,
            endsAt = endsAt
        )

        assertEquals(BillingPeriod.ANNUAL, subscription.period)
        assertEquals(endsAt, subscription.endsAt)
    }

    @Test
    fun `create subscription with cancelled status`() {
        val subscription = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.CANCELLED,
            startsAt = Instant.now(),
            endsAt = Instant.now()
        )

        assertEquals(SubscriptionStatus.CANCELLED, subscription.status)
        assertNotNull(subscription.endsAt)
    }

    @Test
    fun `subscription data class equality`() {
        val id = UUID.randomUUID()
        val now = Instant.now()
        val sub1 = Subscription(id, UUID.randomUUID(), UUID.randomUUID(), BillingPeriod.MONTHLY, SubscriptionStatus.ACTIVE, now, null)
        val sub2 = sub1.copy()

        assertEquals(sub1, sub2)
        assertEquals(sub1.hashCode(), sub2.hashCode())
    }
}
