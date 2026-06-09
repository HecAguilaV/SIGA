package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SaleInvoiceStatusTest {

    @Test
    fun `sale invoice status enum has expected values`() {
        val values = SaleInvoiceStatus.entries
        assertEquals(2, values.size)
        assertTrue(values.contains(SaleInvoiceStatus.COMPLETED))
        assertTrue(values.contains(SaleInvoiceStatus.CANCELLED))
    }

    @Test
    fun `sale invoice status from string`() {
        assertEquals(SaleInvoiceStatus.COMPLETED, SaleInvoiceStatus.valueOf("COMPLETED"))
        assertEquals(SaleInvoiceStatus.CANCELLED, SaleInvoiceStatus.valueOf("CANCELLED"))
    }

    @Test
    fun `sale invoice status order is preserved`() {
        val values = SaleInvoiceStatus.entries
        assertEquals(SaleInvoiceStatus.COMPLETED, values[0])
        assertEquals(SaleInvoiceStatus.CANCELLED, values[1])
    }
}
