package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.ProcessedEventRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import com.siga.inventory.event.SaleItemEvent
import com.siga.inventory.event.StockEventType
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.UUID

class ReserveStockUseCaseTest : DescribeSpec({

    val stockPort = mockk<StockRepositoryPort>()
    val movementPort = mockk<MovementRepositoryPort>()
    val processedEventPort = mockk<ProcessedEventRepositoryPort>()
    val stockEventProducer = mockk<StockEventProducer>()
    val useCase = ReserveStockUseCase(stockPort, movementPort, processedEventPort, stockEventProducer)

    val tenantId = UUID.randomUUID()
    val saleId = UUID.randomUUID()
    val eventId = UUID.randomUUID()
    val userId = UUID.randomUUID()
    val productId = UUID.randomUUID()

    beforeEach {
        clearAllMocks()
        every { processedEventPort.save(any(), any()) } just Runs
    }

    describe("handleSaleInitiated") {

        it("given sufficient stock when handling sale initiated then should reserve stock and emit STOCK_RESERVED") {
            val stock = Stock(productId = productId, storeId = tenantId, quantity = 10)
            every { processedEventPort.existsById(eventId) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }
            every { stockEventProducer.publish(any()) } just Runs

            val result = useCase.handleSaleInitiated(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 3))
            )

            result shouldBe true
            verify { stockPort.save(match { it.quantity == 7 }) }
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_RESERVED && it.saleId == saleId
                })
            }
            verify { processedEventPort.save(eventId, any()) }
        }

        it("given insufficient stock when handling sale initiated then should emit STOCK_FAILED") {
            val stock = Stock(productId = productId, storeId = tenantId, quantity = 2)
            every { processedEventPort.existsById(eventId) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockEventProducer.publish(any()) } just Runs

            val result = useCase.handleSaleInitiated(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 5))
            )

            result shouldBe false
            verify(exactly = 0) { stockPort.save(any()) }
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_FAILED && it.reason!!.contains("Insufficient")
                })
            }
        }

        it("given product not found when handling sale initiated then should emit STOCK_FAILED") {
            every { processedEventPort.existsById(eventId) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns null
            every { stockEventProducer.publish(any()) } just Runs

            val result = useCase.handleSaleInitiated(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 1))
            )

            result shouldBe false
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_FAILED && it.reason!!.contains("not found")
                })
            }
        }

        it("given duplicate event when handling sale initiated then should skip processing") {
            every { processedEventPort.existsById(eventId) } returns true

            val result = useCase.handleSaleInitiated(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId,
                items = listOf(SaleItemEvent(productId = productId, quantity = 1))
            )

            result shouldBe false
            verify(exactly = 0) { stockPort.findByProductIdAndStoreId(any(), any()) }
            verify(exactly = 0) { stockEventProducer.publish(any()) }
        }

        it("given multiple items when handling sale initiated then should reserve stock for all") {
            val productId2 = UUID.randomUUID()
            val stock1 = Stock(productId = productId, storeId = tenantId, quantity = 10)
            val stock2 = Stock(productId = productId2, storeId = tenantId, quantity = 20)

            every { processedEventPort.existsById(eventId) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock1
            every { stockPort.findByProductIdAndStoreId(productId2, tenantId) } returns stock2
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }
            every { stockEventProducer.publish(any()) } just Runs

            val result = useCase.handleSaleInitiated(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId,
                items = listOf(
                    SaleItemEvent(productId = productId, quantity = 3),
                    SaleItemEvent(productId = productId2, quantity = 5)
                )
            )

            result shouldBe true
            verify(exactly = 2) { stockPort.save(any()) }
            verify(exactly = 2) { movementPort.save(any()) }
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_RESERVED
                })
            }
        }

        it("given one item fails validation when handling multiple items then should emit STOCK_FAILED without deducting") {
            val productId2 = UUID.randomUUID()
            val stock1 = Stock(productId = productId, storeId = tenantId, quantity = 10)

            every { processedEventPort.existsById(eventId) } returns false
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock1
            every { stockPort.findByProductIdAndStoreId(productId2, tenantId) } returns null
            every { stockEventProducer.publish(any()) } just Runs

            val result = useCase.handleSaleInitiated(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId,
                items = listOf(
                    SaleItemEvent(productId = productId, quantity = 3),
                    SaleItemEvent(productId = productId2, quantity = 1)
                )
            )

            result shouldBe false
            verify(exactly = 0) { stockPort.save(any()) }
            verify {
                stockEventProducer.publish(match {
                    it.eventType == StockEventType.STOCK_FAILED
                })
            }
        }
    }

    describe("handleSaleCancelled") {

        val movement = Movement(
            id = UUID.randomUUID(),
            productId = productId,
            storeId = tenantId,
            type = MovementType.SALE,
            quantity = 3,
            previousQuantity = 7,
            newQuantity = 4,
            userId = userId,
            saleId = saleId,
            observations = null
        )

        it("given sale cancelled when movements exist then should restore stock") {
            val stock = Stock(productId = productId, storeId = tenantId, quantity = 4)
            every { processedEventPort.existsById(eventId) } returns false
            every { movementPort.findBySaleId(saleId) } returns listOf(movement)
            every { stockPort.findByProductIdAndStoreId(productId, tenantId) } returns stock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }

            useCase.handleSaleCancelled(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId
            )

            verify { stockPort.save(match { it.quantity == 7 }) }
            verify {
                movementPort.save(match {
                    it.type == MovementType.ADJUSTMENT && it.observations!!.contains("COMPENSATE")
                })
            }
            verify { processedEventPort.save(eventId, any()) }
        }

        it("given sale cancelled with duplicate event then should skip compensation") {
            every { processedEventPort.existsById(eventId) } returns true

            useCase.handleSaleCancelled(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId
            )

            verify(exactly = 0) { movementPort.findBySaleId(any()) }
            verify(exactly = 0) { stockPort.save(any()) }
        }

        it("given sale cancelled with no movements then should skip compensation gracefully") {
            every { processedEventPort.existsById(eventId) } returns false
            every { movementPort.findBySaleId(saleId) } returns emptyList()

            useCase.handleSaleCancelled(
                eventId = eventId,
                saleId = saleId,
                tenantId = tenantId,
                userId = userId
            )

            verify(exactly = 0) { stockPort.save(any()) }
            verify(exactly = 0) { processedEventPort.save(any(), any()) }
        }
    }
})
