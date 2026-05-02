package com.siga.inventory.event

import com.siga.inventory.entity.Movement
import com.siga.inventory.entity.MovementType
import com.siga.inventory.entity.ProcessedEvent
import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.MovementRepository
import com.siga.inventory.repository.ProcessedEventRepository
import com.siga.inventory.repository.StockRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.UUID

/**
 * Unit tests for [SaleEventConsumer] — SAGA step 2.
 * Verifies stock reservation logic, compensation, and idempotency.
 */
class SaleEventConsumerTest : DescribeSpec({

    val stockRepository = mockk<StockRepository>()
    val movementRepository = mockk<MovementRepository>()
    val processedEventRepository = mockk<ProcessedEventRepository>()
    val stockEventProducer = mockk<StockEventProducer>()
    val consumer = SaleEventConsumer(
        stockRepository, movementRepository, processedEventRepository, stockEventProducer
    )

    beforeEach {
        clearAllMocks()
        every { processedEventRepository.save(any()) } answers { firstArg() }
    }

    describe("SaleEventConsumer — SALE_INITIATED") {

        it("given_sale_initiated_when_stock_available_then_reserve_and_emit_stock_reserved") {
            val productId = UUID.randomUUID()
            val tenantId = UUID.randomUUID()
            val saleId = UUID.randomUUID()

            val stock = Stock(
                productId = productId,
                storeId = tenantId,
                quantity = 10
            )

            val event = SaleEvent(
                eventType = SaleEventType.SALE_INITIATED,
                saleId = saleId,
                tenantId = tenantId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 3))
            )

            every { processedEventRepository.existsById(any()) } returns false
            every { stockRepository.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockRepository.save(any()) } answers { firstArg() }
            every { movementRepository.save(any()) } answers { firstArg() }
            every { stockEventProducer.publish(any()) } just Runs

            consumer.consume(event)

            stock.quantity shouldBe 7
            verify { stockRepository.save(stock) }
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_RESERVED && it.saleId == saleId
                })
            }
        }

        it("given_sale_initiated_when_stock_insufficient_then_emit_stock_failed") {
            val productId = UUID.randomUUID()
            val tenantId = UUID.randomUUID()

            val stock = Stock(
                productId = productId,
                storeId = tenantId,
                quantity = 1
            )

            val event = SaleEvent(
                eventType = SaleEventType.SALE_INITIATED,
                saleId = UUID.randomUUID(),
                tenantId = tenantId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 5))
            )

            every { processedEventRepository.existsById(any()) } returns false
            every { stockRepository.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockEventProducer.publish(any()) } just Runs

            consumer.consume(event)

            stock.quantity shouldBe 1 // Not modified
            verify(exactly = 0) { stockRepository.save(any()) }
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_FAILED
                })
            }
        }

        it("given_sale_initiated_when_product_not_found_then_emit_stock_failed") {
            val productId = UUID.randomUUID()
            val tenantId = UUID.randomUUID()

            val event = SaleEvent(
                eventType = SaleEventType.SALE_INITIATED,
                saleId = UUID.randomUUID(),
                tenantId = tenantId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 1))
            )

            every { processedEventRepository.existsById(any()) } returns false
            every { stockRepository.findByProductIdAndStoreId(productId, tenantId) } returns null
            every { stockEventProducer.publish(any()) } just Runs

            consumer.consume(event)

            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_FAILED &&
                    it.reason!!.contains("not found")
                })
            }
        }
    }

    describe("SaleEventConsumer — Idempotency") {

        it("given_duplicate_event_when_consumed_then_skip_processing") {
            val eventId = UUID.randomUUID()
            val event = SaleEvent(
                eventId = eventId,
                eventType = SaleEventType.SALE_INITIATED,
                saleId = UUID.randomUUID(),
                tenantId = UUID.randomUUID()
            )

            every { processedEventRepository.existsById(eventId) } returns true

            consumer.consume(event)

            verify(exactly = 0) { stockRepository.findByProductIdAndStoreId(any(), any()) }
            verify(exactly = 0) { stockEventProducer.publish(any()) }
        }
    }

    describe("SaleEventConsumer — SALE_CANCELLED (Compensation)") {

        it("given_sale_cancelled_when_movements_exist_then_restore_stock") {
            val saleId = UUID.randomUUID()
            val productId = UUID.randomUUID()
            val storeId = UUID.randomUUID()

            val movement = Movement(
                productId = productId,
                storeId = storeId,
                type = MovementType.SALE,
                quantity = 3,
                previousQuantity = 10,
                newQuantity = 7,
                saleId = saleId
            ).apply { id = UUID.randomUUID() }

            val stock = Stock(
                productId = productId,
                storeId = storeId,
                quantity = 7
            )

            val event = SaleEvent(
                eventType = SaleEventType.SALE_CANCELLED,
                saleId = saleId,
                tenantId = storeId
            )

            every { processedEventRepository.existsById(any()) } returns false
            every { movementRepository.findBySaleId(saleId) } returns listOf(movement)
            every { stockRepository.findByProductIdAndStoreId(productId, storeId) } returns stock
            every { stockRepository.save(any()) } answers { firstArg() }
            every { movementRepository.save(any()) } answers { firstArg() }

            consumer.consume(event)

            stock.quantity shouldBe 10 // Restored
            verify { stockRepository.save(stock) }
            verify {
                movementRepository.save(match {
                    it.type == MovementType.ADJUSTMENT &&
                    it.observations!!.contains("COMPENSATE")
                })
            }
        }
    }
})
