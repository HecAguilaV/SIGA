package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SubscriptionStatusTest {

    @Test
    fun `subscription status enum has expected values`() {
        val values = SubscriptionStatus.entries
        assertEquals(4, values.size)
        assertTrue(values.contains(SubscriptionStatus.ACTIVE))
        assertTrue(values.contains(SubscriptionStatus.SUSPENDED))
        assertTrue(values.contains(SubscriptionStatus.CANCELLED))
        assertTrue(values.contains(SubscriptionStatus.EXPIRED))
    }

    @Test
    fun `subscription status from string`() {
        assertEquals(SubscriptionStatus.ACTIVE, SubscriptionStatus.valueOf("ACTIVE"))
        assertEquals(SubscriptionStatus.SUSPENDED, SubscriptionStatus.valueOf("SUSPENDED"))
        assertEquals(SubscriptionStatus.CANCELLED, SubscriptionStatus.valueOf("CANCELLED"))
        assertEquals(SubscriptionStatus.EXPIRED, SubscriptionStatus.valueOf("EXPIRED"))
    }

    @Test
    fun `subscription status order is preserved`() {
        val values = SubscriptionStatus.entries
        assertEquals(SubscriptionStatus.ACTIVE, values[0])
        assertEquals(SubscriptionStatus.SUSPENDED, values[1])
        assertEquals(SubscriptionStatus.CANCELLED, values[2])
        assertEquals(SubscriptionStatus.EXPIRED, values[3])
    }
}
