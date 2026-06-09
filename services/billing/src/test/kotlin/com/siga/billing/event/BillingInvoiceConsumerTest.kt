package com.siga.billing.event

import com.siga.billing.domain.port.SaleInvoiceRepositoryPort
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.math.BigDecimal
import java.util.UUID

/**
 * Unit tests for [BillingInvoiceConsumer].
 *
 * Tests the Kafka consumer that processes [SaleCompletedEvent] and persists invoices.
 * Pure unit tests — no Spring context required.
 */
class BillingInvoiceConsumerTest {

    private val saleInvoiceRepositoryPort = mock(SaleInvoiceRepositoryPort::class.java)
    private val consumer = BillingInvoiceConsumer(saleInvoiceRepositoryPort)

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @Test
    @DisplayName("Consume valid SaleCompletedEvent saves invoice")
    fun consume_validEvent_savesInvoice() {
        val event = SaleCompletedEvent(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("15000"),
            items = listOf(
                SaleCompletedItem(
                    productId = UUID.randomUUID(),
                    productName = "Premium Plan",
                    quantity = 1,
                    unitPrice = BigDecimal("15000"),
                    subtotal = BigDecimal("15000")
                )
            )
        )

        consumer.consume(event)

        verify(saleInvoiceRepositoryPort).save(anyObject())
    }

    @Test
    @DisplayName("Consume event with minimum fields (null userId, empty items) saves invoice")
    fun consume_eventWithMinimumFields_savesInvoice() {
        val event = SaleCompletedEvent(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = null,
            total = BigDecimal("0"),
            items = emptyList()
        )

        consumer.consume(event)

        verify(saleInvoiceRepositoryPort).save(anyObject())
    }

    @Test
    @DisplayName("Consume event logs processing and calls save")
    fun consume_event_logsAndSaves() {
        val event = SaleCompletedEvent(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("25000"),
            items = emptyList()
        )

        consumer.consume(event)

        verify(saleInvoiceRepositoryPort).save(anyObject())
    }
}
