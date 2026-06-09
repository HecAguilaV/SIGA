package com.siga.billing.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class SaleInvoiceEntityTest {

    @Test
    fun `create sale invoice entity with all fields`() {
        val id = UUID.randomUUID()

        val entity = SaleInvoiceEntity(
            id = id,
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("25000"),
            items = """[{"product":"Laptop","qty":1,"price":25000}]""",
            status = SaleInvoiceStatus.COMPLETED,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals(id, entity.id)
        assertNotNull(entity.saleId)
        assertNotNull(entity.storeId)
        assertNotNull(entity.userId)
        assertEquals(BigDecimal("25000"), entity.total)
        assertNotNull(entity.items)
        assertEquals(SaleInvoiceStatus.COMPLETED, entity.status)
        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `create sale invoice entity with defaults`() {
        val entity = SaleInvoiceEntity(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal("10000")
        )

        assertNull(entity.id)
        assertNull(entity.userId)
        assertNull(entity.items)
        assertEquals(SaleInvoiceStatus.COMPLETED, entity.status)
        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `create cancelled sale invoice entity`() {
        val entity = SaleInvoiceEntity(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal.ZERO,
            status = SaleInvoiceStatus.CANCELLED
        )

        assertEquals(SaleInvoiceStatus.CANCELLED, entity.status)
    }

    @Test
    fun `sale invoice entity equals by id`() {
        val id = UUID.randomUUID()
        val entity1 = SaleInvoiceEntity(id = id, saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.TEN)
        val entity2 = SaleInvoiceEntity(id = id, saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.TEN)

        assertEquals(entity1, entity2)
        assertEquals(entity1.hashCode(), entity2.hashCode())
    }

    @Test
    fun `sale invoice entity inequality on different id`() {
        val entity1 = SaleInvoiceEntity(id = UUID.randomUUID(), saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.TEN)
        val entity2 = SaleInvoiceEntity(id = UUID.randomUUID(), saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal.TEN)

        assertNotEquals(entity1, entity2)
    }

    @Test
    fun `sale invoice entity toString`() {
        val entity = SaleInvoiceEntity(id = UUID.randomUUID(), saleId = UUID.randomUUID(), storeId = UUID.randomUUID(), total = BigDecimal("5000"))
        val toString = entity.toString()

        assertTrue(toString.contains("SaleInvoiceEntity"))
        assertTrue(toString.contains("COMPLETED"))
    }

    @Test
    fun `sale invoice entity onPrePersist preserves existing timestamps`() {
        val now = Instant.now()
        val entity = SaleInvoiceEntity(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal.ONE,
            createdAt = now,
            updatedAt = now
        )

        entity.onPrePersist()

        assertEquals(now, entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `sale invoice entity onPreUpdate updates updatedAt`() {
        val entity = SaleInvoiceEntity(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal.ONE
        )
        val originalUpdatedAt = entity.updatedAt

        entity.onPreUpdate()

        assertTrue(entity.updatedAt >= originalUpdatedAt)
    }
}
