package com.siga.billing.event

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [SaleCompletedEvent] and [SaleCompletedItem] data classes.
 */
class SaleCompletedEventTest {

    @Test
    @DisplayName("Create event with all fields populated")
    fun createEvent_withAllFields() {
        val saleId = UUID.randomUUID()
        val storeId = UUID.randomUUID()
        val userId = UUID.randomUUID()

        val event = SaleCompletedEvent(
            saleId = saleId,
            storeId = storeId,
            userId = userId,
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

        assertEquals(saleId, event.saleId)
        assertEquals(storeId, event.storeId)
        assertEquals(userId, event.userId)
        assertEquals(BigDecimal("15000"), event.total)
        assertEquals(1, event.items.size)
        assertEquals("Premium Plan", event.items[0].productName)
        assertNotNull(event.eventId, "eventId should be auto-generated")
        assertNotNull(event.timestamp, "timestamp should be auto-generated")
    }

    @Test
    @DisplayName("Default values are applied correctly")
    fun createEvent_withDefaults() {
        val saleId = UUID.randomUUID()
        val storeId = UUID.randomUUID()
        val total = BigDecimal("99.99")

        val event = SaleCompletedEvent(
            saleId = saleId,
            storeId = storeId,
            total = total
        )

        assertNotNull(event.eventId, "eventId should be auto-generated")
        assertEquals(saleId, event.saleId)
        assertEquals(storeId, event.storeId)
        assertNull(event.userId, "userId should default to null")
        assertEquals(total, event.total)
        assertTrue(event.items.isEmpty(), "items should default to empty list")
        assertNotNull(event.timestamp, "timestamp should default to Instant.now()")
    }

    @Test
    @DisplayName("Equality checks work correctly")
    fun equality() {
        val fixedEventId = UUID.randomUUID()
        val fixedSaleId = UUID.randomUUID()
        val fixedStoreId = UUID.randomUUID()
        val fixedTimestamp = Instant.now()

        val event1 = SaleCompletedEvent(
            eventId = fixedEventId,
            saleId = fixedSaleId,
            storeId = fixedStoreId,
            userId = null,
            total = BigDecimal("100"),
            timestamp = fixedTimestamp
        )
        val event2 = event1.copy()

        assertEquals(event1, event2, "Two identical events should be equal")
        assertEquals(event1.hashCode(), event2.hashCode(), "Hash codes should match")

        val event3 = event1.copy(total = BigDecimal("200"))
        assertNotEquals(event1, event3, "Events with different totals should not be equal")
    }

    @Test
    @DisplayName("SaleCompletedItem is correctly constructed")
    fun saleCompletedItem() {
        val productId = UUID.randomUUID()
        val item = SaleCompletedItem(
            productId = productId,
            productName = "Test Product",
            quantity = 3,
            unitPrice = BigDecimal("5000"),
            subtotal = BigDecimal("15000")
        )

        assertEquals(productId, item.productId)
        assertEquals("Test Product", item.productName)
        assertEquals(3, item.quantity)
        assertEquals(BigDecimal("5000"), item.unitPrice)
        assertEquals(BigDecimal("15000"), item.subtotal)
    }

    @Test
    @DisplayName("SaleCompletedItem defaults productName to null")
    fun saleCompletedItem_defaultProductName() {
        val item = SaleCompletedItem(
            productId = UUID.randomUUID(),
            quantity = 1,
            unitPrice = BigDecimal("100"),
            subtotal = BigDecimal("100")
        )

        assertNull(item.productName)
    }
}
