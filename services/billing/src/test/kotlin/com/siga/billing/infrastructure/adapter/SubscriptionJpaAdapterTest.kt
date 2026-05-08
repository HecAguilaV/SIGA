package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.BillingPeriod
import com.siga.billing.domain.model.Subscription
import com.siga.billing.domain.model.SubscriptionStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [SubscriptionJpaAdapter].
 * Verifies Subscription persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class SubscriptionJpaAdapterTest @Autowired constructor(
    private val adapter: SubscriptionJpaAdapter
) {

    @Test
    fun `save and find by id`() {
        val subscription = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = Instant.now(),
            endsAt = null
        )

        val saved = adapter.save(subscription)
        assertEquals(subscription.id, saved.id)
        assertEquals(subscription.customerId, saved.customerId)
        assertEquals(BillingPeriod.MONTHLY, saved.period)
        assertEquals(SubscriptionStatus.ACTIVE, saved.status)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals(subscription.customerId, found?.customerId)
    }

    @Test
    fun `findById returns null when subscription does not exist`() {
        val found = adapter.findById(UUID.randomUUID())
        assertNull(found)
    }

    @Test
    fun `findByCustomerId returns subscriptions for a customer`() {
        val customerId = UUID.randomUUID()
        val sub1 = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE, startsAt = Instant.now(), endsAt = null
        )
        val sub2 = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.ANNUAL,
            status = SubscriptionStatus.ACTIVE, startsAt = Instant.now(), endsAt = null
        )
        adapter.save(sub1)
        adapter.save(sub2)

        val subscriptions = adapter.findByCustomerId(customerId)
        assertTrue(subscriptions.any { it.id == sub1.id })
        assertTrue(subscriptions.any { it.id == sub2.id })
    }

    @Test
    fun `findByCustomerIdAndStatus filters by status`() {
        val customerId = UUID.randomUUID()
        val activeSub = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE, startsAt = Instant.now(), endsAt = null
        )
        val cancelledSub = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.CANCELLED, startsAt = Instant.now(), endsAt = Instant.now()
        )
        adapter.save(activeSub)
        adapter.save(cancelledSub)

        val activeSubs = adapter.findByCustomerIdAndStatus(customerId, SubscriptionStatus.ACTIVE)
        assertTrue(activeSubs.any { it.id == activeSub.id })
        assertFalse(activeSubs.any { it.id == cancelledSub.id })
    }

    @Test
    fun `findByCustomerIdAndStatusIn filters by multiple statuses`() {
        val customerId = UUID.randomUUID()
        val activeSub = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE, startsAt = Instant.now(), endsAt = null
        )
        val suspendedSub = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.SUSPENDED, startsAt = Instant.now(), endsAt = null
        )
        val cancelledSub = Subscription(
            id = UUID.randomUUID(), customerId = customerId,
            planId = UUID.randomUUID(), period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.CANCELLED, startsAt = Instant.now(), endsAt = Instant.now()
        )
        adapter.save(activeSub)
        adapter.save(suspendedSub)
        adapter.save(cancelledSub)

        val activeOrSuspended = adapter.findByCustomerIdAndStatusIn(
            customerId, listOf(SubscriptionStatus.ACTIVE, SubscriptionStatus.SUSPENDED)
        )
        assertTrue(activeOrSuspended.any { it.id == activeSub.id })
        assertTrue(activeOrSuspended.any { it.id == suspendedSub.id })
        assertFalse(activeOrSuspended.any { it.id == cancelledSub.id })
    }

    @Test
    fun `save subscription with endsAt as null`() {
        val subscription = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = Instant.now(),
            endsAt = null
        )
        val saved = adapter.save(subscription)
        assertNull(saved.endsAt)
    }

    @Test
    fun `update subscription status`() {
        val subscription = Subscription(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            planId = UUID.randomUUID(),
            period = BillingPeriod.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            startsAt = Instant.now(),
            endsAt = null
        )
        val saved = adapter.save(subscription)

        val updated = saved.copy(status = SubscriptionStatus.CANCELLED, endsAt = Instant.now())
        adapter.save(updated)

        val found = adapter.findById(saved.id)
        assertNotNull(found)
        assertEquals(SubscriptionStatus.CANCELLED, found?.status)
        assertNotNull(found?.endsAt)
    }
}
