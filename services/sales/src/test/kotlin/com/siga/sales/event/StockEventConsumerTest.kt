package com.siga.sales.event

import com.siga.sales.entity.ProcessedEvent
import com.siga.sales.entity.Sale
import com.siga.sales.entity.SaleStatus
import com.siga.sales.repository.ProcessedEventRepository
import com.siga.sales.repository.SaleRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.math.BigDecimal
import java.util.*

/**
 * Unit tests for [StockEventConsumer] — SAGA step 3.
 * Verifies that Sales correctly processes stock response events.
 */
class StockEventConsumerTest : DescribeSpec({

    val saleRepository = mockk<SaleRepository>()
    val processedEventRepository = mockk<ProcessedEventRepository>()
    val consumer = StockEventConsumer(saleRepository, processedEventRepository)

    beforeEach {
        clearAllMocks()
    }

    describe("StockEventConsumer") {

        it("given_stock_reserved_when_sale_pending_then_confirm_sale") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()
            val sale = Sale(
                storeId = UUID.randomUUID(),
                total = BigDecimal("100.00"),
                status = SaleStatus.PENDING
            ).apply { id = saleId }

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns false
            every { saleRepository.findById(saleId) } returns Optional.of(sale)
            every { saleRepository.save(any()) } answers { firstArg() }
            every { processedEventRepository.save(any()) } answers { firstArg() }

            consumer.consume(event)

            sale.status shouldBe SaleStatus.COMPLETED
            verify { saleRepository.save(sale) }
            verify { processedEventRepository.save(match { it.eventId == eventId }) }
        }

        it("given_stock_failed_when_sale_pending_then_cancel_sale") {
            val saleId = UUID.randomUUID()
            val eventId = UUID.randomUUID()
            val sale = Sale(
                storeId = UUID.randomUUID(),
                total = BigDecimal("50.00"),
                status = SaleStatus.PENDING
            ).apply { id = saleId }

            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_FAILED,
                saleId = saleId,
                tenantId = UUID.randomUUID(),
                reason = "Insufficient stock for product X"
            )

            every { processedEventRepository.existsById(eventId) } returns false
            every { saleRepository.findById(saleId) } returns Optional.of(sale)
            every { saleRepository.save(any()) } answers { firstArg() }
            every { processedEventRepository.save(any()) } answers { firstArg() }

            consumer.consume(event)

            sale.status shouldBe SaleStatus.CANCELLED
            verify { saleRepository.save(sale) }
        }

        it("given_duplicate_event_when_consumed_then_skip_processing") {
            val eventId = UUID.randomUUID()
            val event = StockEvent(
                eventId = eventId,
                eventType = StockEventType.STOCK_RESERVED,
                saleId = UUID.randomUUID(),
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns true

            consumer.consume(event)

            verify(exactly = 0) { saleRepository.findById(any()) }
            verify(exactly = 0) { saleRepository.save(any()) }
        }

        it("given_stock_reserved_when_sale_already_completed_then_skip") {
            val saleId = UUID.randomUUID()
            val sale = Sale(
                storeId = UUID.randomUUID(),
                total = BigDecimal("100.00"),
                status = SaleStatus.COMPLETED
            ).apply { id = saleId }

            val event = StockEvent(
                eventId = UUID.randomUUID(),
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(any()) } returns false
            every { saleRepository.findById(saleId) } returns Optional.of(sale)

            consumer.consume(event)

            sale.status shouldBe SaleStatus.COMPLETED
            verify(exactly = 0) { saleRepository.save(any()) }
        }
    }
})
