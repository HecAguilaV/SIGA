package com.siga.billing.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class SaleInvoiceTest {

    @Test
    fun `create sale invoice with all fields`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val invoice = SaleInvoice(
            id = id,
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("25000"),
            items = """[{"product":"Laptop","qty":1,"price":25000}]""",
            status = SaleInvoiceStatus.COMPLETED,
            createdAt = now,
            updatedAt = now
        )

        assertEquals(id, invoice.id)
        assertNotNull(invoice.saleId)
        assertNotNull(invoice.storeId)
        assertNotNull(invoice.userId)
        assertEquals(BigDecimal("25000"), invoice.total)
        assertNotNull(invoice.items)
        assertEquals(SaleInvoiceStatus.COMPLETED, invoice.status)
        assertEquals(now, invoice.createdAt)
        assertEquals(now, invoice.updatedAt)
    }

    @Test
    fun `create sale invoice with default values`() {
        val invoice = SaleInvoice(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal("10000")
        )

        assertNull(invoice.id)
        assertNull(invoice.userId)
        assertNull(invoice.items)
        assertEquals(SaleInvoiceStatus.COMPLETED, invoice.status)
        assertNull(invoice.createdAt)
        assertNull(invoice.updatedAt)
    }

    @Test
    fun `create cancelled sale invoice`() {
        val invoice = SaleInvoice(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal("5000"),
            status = SaleInvoiceStatus.CANCELLED
        )

        assertEquals(SaleInvoiceStatus.CANCELLED, invoice.status)
    }

    @Test
    fun `sale invoice data class equality`() {
        val id = UUID.randomUUID()
        val invoice1 = SaleInvoice(id, UUID.randomUUID(), UUID.randomUUID(), null, BigDecimal.TEN, null, SaleInvoiceStatus.COMPLETED, null, null)
        val invoice2 = invoice1.copy()

        assertEquals(invoice1, invoice2)
        assertEquals(invoice1.hashCode(), invoice2.hashCode())
    }

    @Test
    fun `sale invoice with zero total`() {
        val invoice = SaleInvoice(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal.ZERO
        )

        assertEquals(BigDecimal.ZERO, invoice.total)
    }

    @Test
    fun `sale invoice toString contains fields`() {
        val invoice = SaleInvoice(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal("15000")
        )
        val toString = invoice.toString()

        assertTrue(toString.contains("SaleInvoice("))
        assertTrue(toString.contains("COMPLETED"))
    }
}
