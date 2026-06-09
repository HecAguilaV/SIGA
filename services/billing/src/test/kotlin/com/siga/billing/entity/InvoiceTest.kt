package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class InvoiceTest {

    @Test
    fun `create invoice with all fields`() {
        val id = UUID.randomUUID()

        val invoice = Invoice(
            id = id,
            invoiceNumber = "INV-001",
            customerId = UUID.randomUUID(),
            userName = "John Doe",
            userEmail = "john@example.com",
            planId = UUID.randomUUID(),
            planName = "Premium",
            priceUF = BigDecimal("10.50"),
            priceCLP = BigDecimal("350000"),
            unit = "UF",
            purchasedAt = Instant.now(),
            dueDate = Instant.now().plusSeconds(86400 * 30),
            status = InvoiceStatus.PAID,
            paymentMethod = "VISA",
            last4Digits = "1234",
            subscriptionId = UUID.randomUUID(),
            paymentId = UUID.randomUUID(),
            tax = BigDecimal("66500"),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals(id, invoice.id)
        assertEquals("INV-001", invoice.invoiceNumber)
        assertEquals("John Doe", invoice.userName)
        assertEquals("john@example.com", invoice.userEmail)
        assertEquals("Premium", invoice.planName)
        assertEquals(BigDecimal("10.50"), invoice.priceUF)
        assertEquals(BigDecimal("350000"), invoice.priceCLP)
        assertEquals("UF", invoice.unit)
        assertEquals(InvoiceStatus.PAID, invoice.status)
        assertEquals("VISA", invoice.paymentMethod)
        assertEquals("1234", invoice.last4Digits)
        assertNotNull(invoice.tax)
    }

    @Test
    fun `create invoice with defaults`() {
        val invoice = Invoice(
            invoiceNumber = "INV-002",
            customerId = UUID.randomUUID(),
            userName = "Jane Doe",
            userEmail = "jane@example.com",
            planId = UUID.randomUUID(),
            planName = "Basic",
            priceUF = BigDecimal("5.00")
        )

        assertNull(invoice.id)
        assertNull(invoice.priceCLP)
        assertEquals("UF", invoice.unit)
        assertEquals(InvoiceStatus.PAID, invoice.status)
        assertNull(invoice.paymentMethod)
        assertNull(invoice.last4Digits)
        assertNull(invoice.subscriptionId)
        assertNull(invoice.paymentId)
        assertNull(invoice.tax)
        assertNull(invoice.dueDate)
        assertNotNull(invoice.purchasedAt)
        assertNotNull(invoice.createdAt)
        assertNotNull(invoice.updatedAt)
    }

    @Test
    fun `invoice with different statuses`() {
        fun makeInvoice(status: InvoiceStatus) = Invoice(
            invoiceNumber = "INV-003",
            customerId = UUID.randomUUID(),
            userName = "Status",
            userEmail = "status@test.com",
            planId = UUID.randomUUID(),
            planName = "Test",
            priceUF = BigDecimal.ONE,
            status = status
        )

        assertEquals(InvoiceStatus.PENDING, makeInvoice(InvoiceStatus.PENDING).status)
        assertEquals(InvoiceStatus.PAID, makeInvoice(InvoiceStatus.PAID).status)
        assertEquals(InvoiceStatus.EXPIRED, makeInvoice(InvoiceStatus.EXPIRED).status)
        assertEquals(InvoiceStatus.CANCELLED, makeInvoice(InvoiceStatus.CANCELLED).status)
    }

    @Test
    fun `invoice equals by id`() {
        val id = UUID.randomUUID()
        val inv1 = Invoice(id = id, invoiceNumber = "INV-001", customerId = UUID.randomUUID(), userName = "A", userEmail = "a@b.com", planId = UUID.randomUUID(), planName = "P", priceUF = BigDecimal.ONE)
        val inv2 = Invoice(id = id, invoiceNumber = "INV-001", customerId = UUID.randomUUID(), userName = "A", userEmail = "a@b.com", planId = UUID.randomUUID(), planName = "P", priceUF = BigDecimal.ONE)

        assertEquals(inv1, inv2)
        assertEquals(inv1.hashCode(), inv2.hashCode())
    }

    @Test
    fun `invoice inequality on different id`() {
        fun makeInvoice(id: UUID?) = Invoice(
            id = id,
            invoiceNumber = "INV-001",
            customerId = UUID.randomUUID(),
            userName = "A",
            userEmail = "a@b.com",
            planId = UUID.randomUUID(),
            planName = "P",
            priceUF = BigDecimal.ONE
        )

        val inv1 = makeInvoice(UUID.randomUUID())
        val inv2 = makeInvoice(UUID.randomUUID())

        assertNotEquals(inv1, inv2)
    }

    @Test
    fun `invoice toString`() {
        val invoice = Invoice(
            invoiceNumber = "INV-100",
            customerId = UUID.randomUUID(),
            userName = "Test",
            userEmail = "test@test.com",
            planId = UUID.randomUUID(),
            planName = "Pro",
            priceUF = BigDecimal("15.00")
        )
        val toString = invoice.toString()

        assertTrue(toString.contains("Invoice"))
        assertTrue(toString.contains("INV-100"))
        assertTrue(toString.contains("PAID"))
    }

    @Test
    fun `invoice onPrePersist preserves existing timestamps`() {
        val now = Instant.now()
        val invoice = Invoice(
            invoiceNumber = "INV-NEW",
            customerId = UUID.randomUUID(),
            userName = "New",
            userEmail = "new@test.com",
            planId = UUID.randomUUID(),
            planName = "Free",
            priceUF = BigDecimal.ZERO,
            createdAt = now,
            updatedAt = now,
            purchasedAt = now
        )

        invoice.onPrePersist()

        assertEquals(now, invoice.createdAt)
        assertNotNull(invoice.updatedAt)
        assertEquals(now, invoice.purchasedAt)
    }

    @Test
    fun `invoice onPreUpdate updates updatedAt`() {
        val invoice = Invoice(
            invoiceNumber = "INV-UPD",
            customerId = UUID.randomUUID(),
            userName = "Upd",
            userEmail = "upd@test.com",
            planId = UUID.randomUUID(),
            planName = "Test",
            priceUF = BigDecimal.ONE
        )
        val originalUpdatedAt = invoice.updatedAt

        invoice.onPreUpdate()

        assertTrue(invoice.updatedAt >= originalUpdatedAt)
    }
}
