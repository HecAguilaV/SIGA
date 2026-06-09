package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.SaleInvoice as DomainSaleInvoice
import com.siga.billing.domain.model.SaleInvoiceStatus as DomainSaleInvoiceStatus
import com.siga.billing.entity.SaleInvoiceEntity
import com.siga.billing.entity.SaleInvoiceStatus as EntitySaleInvoiceStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class SaleInvoiceMapperTest {

    @Test
    fun `toDomain maps entity to domain model`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val entity = SaleInvoiceEntity(
            id = id,
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("25000"),
            items = """[{"product":"Laptop","qty":1,"price":25000}]""",
            status = EntitySaleInvoiceStatus.COMPLETED,
            createdAt = now,
            updatedAt = now
        )

        val domain = SaleInvoiceMapper.toDomain(entity)

        assertEquals(id, domain.id)
        assertEquals(entity.saleId, domain.saleId)
        assertEquals(entity.storeId, domain.storeId)
        assertEquals(entity.userId, domain.userId)
        assertEquals(entity.total, domain.total)
        assertEquals(entity.items, domain.items)
        assertEquals(DomainSaleInvoiceStatus.COMPLETED, domain.status)
        assertEquals(now, domain.createdAt)
        assertEquals(now, domain.updatedAt)
    }

    @Test
    fun `toDomain maps entity with null optional fields`() {
        val entity = SaleInvoiceEntity(
            id = null,
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = null,
            total = BigDecimal.ZERO,
            items = null,
            status = EntitySaleInvoiceStatus.CANCELLED,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val domain = SaleInvoiceMapper.toDomain(entity)

        assertNull(domain.id)
        assertNull(domain.userId)
        assertNull(domain.items)
        assertEquals(DomainSaleInvoiceStatus.CANCELLED, domain.status)
    }

    @Test
    fun `toEntity maps domain model to entity`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val domain = DomainSaleInvoice(
            id = id,
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("15000"),
            items = """[{"product":"Mouse","qty":2,"price":7500}]""",
            status = DomainSaleInvoiceStatus.COMPLETED,
            createdAt = now,
            updatedAt = now
        )

        val entity = SaleInvoiceMapper.toEntity(domain)

        assertEquals(id, entity.id)
        assertEquals(domain.saleId, entity.saleId)
        assertEquals(domain.storeId, entity.storeId)
        assertEquals(domain.userId, entity.userId)
        assertEquals(domain.total, entity.total)
        assertEquals(domain.items, entity.items)
        assertEquals(EntitySaleInvoiceStatus.COMPLETED, entity.status)
        assertEquals(now, entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `toEntity maps domain with null optional fields`() {
        val domain = DomainSaleInvoice(
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal("5000"),
            status = DomainSaleInvoiceStatus.CANCELLED
        )

        val entity = SaleInvoiceMapper.toEntity(domain)

        assertNull(entity.id)
        assertNull(entity.userId)
        assertNull(entity.items)
        assertEquals(EntitySaleInvoiceStatus.CANCELLED, entity.status)
        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }

    @Test
    fun `toEntity handles null createdAt by providing default`() {
        val domain = DomainSaleInvoice(
            id = UUID.randomUUID(),
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            total = BigDecimal.ONE,
            createdAt = null
        )

        val entity = SaleInvoiceMapper.toEntity(domain)

        assertNotNull(entity.createdAt)
    }

    @Test
    fun `maps all sale invoice statuses correctly`() {
        val statusMappings = listOf(
            Pair(EntitySaleInvoiceStatus.COMPLETED, DomainSaleInvoiceStatus.COMPLETED),
            Pair(EntitySaleInvoiceStatus.CANCELLED, DomainSaleInvoiceStatus.CANCELLED)
        )

        for ((entityStatus, domainStatus) in statusMappings) {
            val entity = SaleInvoiceEntity(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                total = BigDecimal.TEN,
                status = entityStatus,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            val domain = SaleInvoiceMapper.toDomain(entity)
            assertEquals(domainStatus, domain.status)

            val backToEntity = SaleInvoiceMapper.toEntity(domain)
            assertEquals(entityStatus, backToEntity.status)
        }
    }

    @Test
    fun `roundtrip domain to entity to domain`() {
        val now = Instant.now()
        val original = DomainSaleInvoice(
            id = UUID.randomUUID(),
            saleId = UUID.randomUUID(),
            storeId = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            total = BigDecimal("99999"),
            items = """[{"product":"Server","qty":1,"price":99999}]""",
            status = DomainSaleInvoiceStatus.COMPLETED,
            createdAt = now,
            updatedAt = now
        )

        val entity = SaleInvoiceMapper.toEntity(original)
        val domain = SaleInvoiceMapper.toDomain(entity)

        assertEquals(original.id, domain.id)
        assertEquals(original.saleId, domain.saleId)
        assertEquals(original.storeId, domain.storeId)
        assertEquals(original.userId, domain.userId)
        assertEquals(original.total, domain.total)
        assertEquals(original.items, domain.items)
        assertEquals(original.status, domain.status)
        assertEquals(original.createdAt, domain.createdAt)
        // updatedAt is regenerated by toEntity, skip equality check
    }
}
