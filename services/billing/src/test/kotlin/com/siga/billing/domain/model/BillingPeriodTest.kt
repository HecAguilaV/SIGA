package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BillingPeriodTest {

    @Test
    fun `billing period enum has expected values`() {
        val values = BillingPeriod.entries
        assertEquals(2, values.size)
        assertTrue(values.contains(BillingPeriod.MONTHLY))
        assertTrue(values.contains(BillingPeriod.ANNUAL))
    }

    @Test
    fun `billing period from string`() {
        assertEquals(BillingPeriod.MONTHLY, BillingPeriod.valueOf("MONTHLY"))
        assertEquals(BillingPeriod.ANNUAL, BillingPeriod.valueOf("ANNUAL"))
    }

    @Test
    fun `billing period order is preserved`() {
        val values = BillingPeriod.entries
        assertEquals(BillingPeriod.MONTHLY, values[0])
        assertEquals(BillingPeriod.ANNUAL, values[1])
    }
}
