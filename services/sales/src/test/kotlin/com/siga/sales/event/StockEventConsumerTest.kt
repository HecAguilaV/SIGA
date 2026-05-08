package com.siga.sales.event

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import com.siga.sales.entity.ProcessedEvent
import com.siga.sales.repository.ProcessedEventRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [StockEventConsumer] — SAGA step 3.
 * Verifies that Sales correctly processes stock response events
 * through the hexagonal ports.
 */
class StockEventConsumerTest : DescribeSpec({

    val saleRepositoryPort = mockk<SaleRepositoryPort>()
    val processedEventRepository = mockk<ProcessedEventRepository>()
    val consumer = StockEventConsumer(saleRepositoryPort, processedEventRepository)

    beforeEach {
        clearAllMocks()
    }

    describe("StockEventConsumer") {

        it("given_stock_reserved_when_sale_pending_then_complete_sale") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()
            val sale = Sale(
                id = saleId,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("100.00"),
                status = SaleStatus.PENDING,
                observations = null
            )

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns false
            every { saleRepositoryPort.findById(saleId) } returns sale
            every { saleRepositoryPort.save(any()) } answers { firstArg() }
            every { processedEventRepository.save(any()) } answers { firstArg() }

            consumer.consume(event)

            // Sale must be saved with COMPLETED status
            val slot = slot<Sale>()
            verify { saleRepositoryPort.save(capture(slot)) }
            slot.captured.status shouldBe SaleStatus.COMPLETED

            // Event must be marked as processed
            verify { processedEventRepository.save(match { it.eventId == eventId }) }
        }

        it("given_stock_failed_when_sale_pending_then_cancel_sale") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()
            val sale = Sale(
                id = saleId,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("100.00"),
                status = SaleStatus.PENDING,
                observations = null
            )

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_FAILED,
                saleId = saleId,
                tenantId = UUID.randomUUID(),
                reason = "Insufficient stock"
            )

            every { processedEventRepository.existsById(eventId) } returns false
            every { saleRepositoryPort.findById(saleId) } returns sale
            every { saleRepositoryPort.save(any()) } answers { firstArg() }
            every { processedEventRepository.save(any()) } answers { firstArg() }

            consumer.consume(event)

            val slot = slot<Sale>()
            verify { saleRepositoryPort.save(capture(slot)) }
            slot.captured.status shouldBe SaleStatus.CANCELLED

            verify { processedEventRepository.save(match { it.eventId == eventId }) }
        }

        it("given_duplicate_event_when_already_processed_then_skip") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns true

            consumer.consume(event)

            verify(exactly = 0) { saleRepositoryPort.findById(any()) }
            verify(exactly = 0) { saleRepositoryPort.save(any()) }
            verify(exactly = 0) { processedEventRepository.save(any()) }
        }

        it("given_event_when_sale_not_found_then_skip") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns false
            every { saleRepositoryPort.findById(saleId) } returns null

            consumer.consume(event)

            verify(exactly = 0) { saleRepositoryPort.save(any()) }
        }

        it("given_stock_reserved_when_sale_already_completed_then_skip") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()
            val sale = Sale(
                id = saleId,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("100.00"),
                status = SaleStatus.COMPLETED,
                observations = null
            )

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns false
            every { saleRepositoryPort.findById(saleId) } returns sale

            consumer.consume(event)

            verify(exactly = 0) { saleRepositoryPort.save(any()) }
        }
    }
})
