package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus as DomainPaymentStatus
import com.siga.billing.entity.PaymentEntity
import com.siga.billing.entity.PaymentStatus as EntityPaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentMapperTest {

    @Test
    fun `toDomain maps entity to domain model`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val entity = PaymentEntity(
            id = id,
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("49.99"),
            paymentMethod = "VISA",
            status = EntityPaymentStatus.COMPLETED,
            reference = "TXN-001",
            paidAt = now
        )

        val domain = PaymentMapper.toDomain(entity)

        assertEquals(id, domain.id)
        assertEquals(entity.subscriptionId, domain.subscriptionId)
        assertEquals(entity.customerId, domain.customerId)
        assertEquals(BigDecimal("49.99"), domain.amount)
        assertEquals("VISA", domain.paymentMethod)
        assertEquals(DomainPaymentStatus.COMPLETED, domain.status)
        assertEquals("TXN-001", domain.reference)
        assertEquals(now, domain.paidAt)
    }

    @Test
    fun `toDomain maps entity with null optional fields`() {
        val entity = PaymentEntity(
            id = UUID.randomUUID(),
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal.TEN,
            paymentMethod = null,
            status = EntityPaymentStatus.PENDING,
            reference = null,
            paidAt = Instant.now()
        )

        val domain = PaymentMapper.toDomain(entity)

        assertNull(domain.paymentMethod)
        assertNull(domain.reference)
        assertEquals(DomainPaymentStatus.PENDING, domain.status)
    }

    @Test
    fun `toDomain assigns random id when entity id is null`() {
        val entity = PaymentEntity(
            id = null,
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal.TEN,
            status = EntityPaymentStatus.FAILED,
            paidAt = Instant.now()
        )

        val domain = PaymentMapper.toDomain(entity)

        assertNotNull(domain.id)
    }

    @Test
    fun `toEntity maps domain model to entity`() {
        val id = UUID.randomUUID()
        val now = Instant.now()

        val domain = Payment(
            id = id,
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("29.99"),
            paymentMethod = "MC",
            status = DomainPaymentStatus.COMPLETED,
            reference = "TXN-002",
            paidAt = now
        )

        val entity = PaymentMapper.toEntity(domain)

        assertEquals(id, entity.id)
        assertEquals(domain.subscriptionId, entity.subscriptionId)
        assertEquals(domain.customerId, entity.customerId)
        assertEquals(BigDecimal("29.99"), entity.amount)
        assertEquals("MC", entity.paymentMethod)
        assertEquals(EntityPaymentStatus.COMPLETED, entity.status)
        assertEquals("TXN-002", entity.reference)
        assertEquals(now, entity.paidAt)
    }

    @Test
    fun `toEntity maps domain with null optional fields`() {
        val domain = Payment(
            id = UUID.randomUUID(),
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal.ZERO,
            paymentMethod = null,
            status = DomainPaymentStatus.PENDING,
            reference = null,
            paidAt = Instant.now()
        )

        val entity = PaymentMapper.toEntity(domain)

        assertNull(entity.paymentMethod)
        assertNull(entity.reference)
        assertEquals(EntityPaymentStatus.PENDING, entity.status)
    }

    @Test
    fun `maps all payment statuses correctly`() {
        val statuses = listOf(
            Pair(EntityPaymentStatus.PENDING, DomainPaymentStatus.PENDING),
            Pair(EntityPaymentStatus.COMPLETED, DomainPaymentStatus.COMPLETED),
            Pair(EntityPaymentStatus.FAILED, DomainPaymentStatus.FAILED),
            Pair(EntityPaymentStatus.REFUNDED, DomainPaymentStatus.REFUNDED)
        )

        for ((entityStatus, domainStatus) in statuses) {
            val entity = PaymentEntity(
                id = UUID.randomUUID(),
                subscriptionId = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                amount = BigDecimal.ONE,
                status = entityStatus,
                paidAt = Instant.now()
            )

            val domain = PaymentMapper.toDomain(entity)
            assertEquals(domainStatus, domain.status)

            val backToEntity = PaymentMapper.toEntity(domain)
            assertEquals(entityStatus, backToEntity.status)
        }
    }

    @Test
    fun `roundtrip domain to entity to domain`() {
        val original = Payment(
            id = UUID.randomUUID(),
            subscriptionId = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            amount = BigDecimal("100.00"),
            paymentMethod = "AMEX",
            status = DomainPaymentStatus.REFUNDED,
            reference = "REF-001",
            paidAt = Instant.now()
        )

        val entity = PaymentMapper.toEntity(original)
        val domain = PaymentMapper.toDomain(entity)

        assertEquals(original.id, domain.id)
        assertEquals(original.subscriptionId, domain.subscriptionId)
        assertEquals(original.customerId, domain.customerId)
        assertEquals(original.amount, domain.amount)
        assertEquals(original.paymentMethod, domain.paymentMethod)
        assertEquals(original.status, domain.status)
        assertEquals(original.reference, domain.reference)
    }
}
