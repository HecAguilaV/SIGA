package com.siga.inventory.event

import com.siga.inventory.application.usecase.ReserveStockUseCase
import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.ProcessedEventRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import com.siga.inventory.repository.ProcessedEventRepository
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.*
import java.util.UUID

/**
 * Unit tests for [SaleEventConsumer] — SAGA step 2.
 * Verifies stock reservation logic, compensation, and idempotency.
 *
 * WHY PORTS: The consumer now delegates to [ReserveStockUseCase], which
 * depends on domain ports (not JPA repos). We mock the ports to test
 * the full flow: Kafka event → Use Case → Port calls.
 */
class SaleEventConsumerTest : DescribeSpec({

    val stockPort = mockk<StockRepositoryPort>()
    val movementPort = mockk<MovementRepositoryPort>()
    val processedEventPort = mockk<ProcessedEventRepositoryPort>()
    val stockEventProducer = mockk<StockEventProducer>()

    val useCase = ReserveStockUseCase(stockPort, movementPort, processedEventPort, stockEventProducer)
    val processedEventRepo = mockk<ProcessedEventRepository>()
    val consumer = SaleEventConsumer(useCase, processedEventRepo)

    beforeEach {
        clearAllMocks()
        every { processedEventPort.save(any(), any()) } just Runs
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

            every { processedEventPort.existsById(any()) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }
            every { stockEventProducer.publish(any()) } just Runs

            consumer.consume(event)

            verify { stockPort.save(match { it.quantity == 7 && it.productId == productId }) }
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

            every { processedEventPort.existsById(any()) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockEventProducer.publish(any()) } just Runs

            consumer.consume(event)

            verify(exactly = 0) { stockPort.save(any()) }
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

            every { processedEventPort.existsById(any()) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns null
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

            every { processedEventPort.existsById(eventId) } returns true

            consumer.consume(event)

            verify(exactly = 0) { stockPort.findByProductIdAndStoreId(any(), any()) }
            verify(exactly = 0) { stockEventProducer.publish(any()) }
        }
    }

    describe("SaleEventConsumer — SALE_CANCELLED (Compensation)") {

        it("given_sale_cancelled_when_movements_exist_then_restore_stock") {
            val saleId = UUID.randomUUID()
            val productId = UUID.randomUUID()
            val storeId = UUID.randomUUID()

            val movement = Movement(
                id = UUID.randomUUID(),
                productId = productId,
                storeId = storeId,
                type = MovementType.SALE,
                quantity = 3,
                previousQuantity = 10,
                newQuantity = 7,
                userId = null,
                saleId = saleId,
                observations = null
            )

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

            every { processedEventPort.existsById(any()) } returns false
            every { movementPort.findBySaleId(saleId) } returns listOf(movement)
            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns stock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }

            consumer.consume(event)

            verify { stockPort.save(match { it.quantity == 10 }) }
            verify {
                movementPort.save(match {
                    it.type == MovementType.ADJUSTMENT &&
                    it.observations!!.contains("COMPENSATE")
                })
            }
        }
    }
})
