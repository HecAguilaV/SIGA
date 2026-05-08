package com.siga.sales.application.usecase

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleItem
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import com.siga.sales.domain.port.SaleItemRepositoryPort
import com.siga.sales.event.SaleEvent
import com.siga.sales.event.SaleEventProducer
import com.siga.sales.event.SaleEventType
import com.siga.sales.event.SaleItemEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [CreateSaleUseCase].
 * Verifies sale creation orchestrates persistence and event emission.
 */
class CreateSaleUseCaseTest : DescribeSpec({

    val saleRepositoryPort = mockk<SaleRepositoryPort>()
    val saleItemRepositoryPort = mockk<SaleItemRepositoryPort>()
    val saleEventProducer = mockk<SaleEventProducer>()
    val useCase = CreateSaleUseCase(saleRepositoryPort, saleItemRepositoryPort, saleEventProducer)

    beforeEach {
        clearAllMocks()
    }

    describe("CreateSaleUseCase") {

        it("given_valid_sale_with_items_when_create_then_save_sale_items_and_publish_event") {
            val saleId = UUID.randomUUID()
            val storeId = UUID.randomUUID()
            val sale = Sale(
                id = saleId,
                storeId = storeId,
                userId = UUID.randomUUID(),
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("150.00"),
                status = SaleStatus.PENDING,
                observations = null
            )
            val items = listOf(
                SaleItem(
                    id = UUID.randomUUID(),
                    saleId = saleId,
                    productId = UUID.randomUUID(),
                    quantity = 2,
                    unitPrice = BigDecimal("50.00"),
                    subtotal = BigDecimal("100.00")
                ),
                SaleItem(
                    id = UUID.randomUUID(),
                    saleId = saleId,
                    productId = UUID.randomUUID(),
                    quantity = 1,
                    unitPrice = BigDecimal("50.00"),
                    subtotal = BigDecimal("50.00")
                )
            )

            every { saleRepositoryPort.save(sale) } returns sale
            every { saleItemRepositoryPort.save(any()) } answers { firstArg() }
            every { saleEventProducer.publish(any()) } just Runs

            val result = useCase.createSale(sale, items)

            // Sale must be saved
            verify(exactly = 1) { saleRepositoryPort.save(sale) }

            // Each item must be saved with the saleId assigned
            val itemSlots = mutableListOf<SaleItem>()
            verify(exactly = 2) { saleItemRepositoryPort.save(capture(itemSlots)) }
            itemSlots.forEach { it.saleId shouldBe saleId }

            // SAGA event must be published
            val eventSlot = slot<SaleEvent>()
            verify(exactly = 1) { saleEventProducer.publish(capture(eventSlot)) }
            eventSlot.captured.eventType shouldBe SaleEventType.SALE_INITIATED
            eventSlot.captured.saleId shouldBe saleId
            eventSlot.captured.tenantId shouldBe storeId
            eventSlot.captured.items shouldBe items.map { SaleItemEvent(it.productId, it.quantity) }

            // Result must be the saved sale
            result shouldBe sale
        }

        it("given_sale_without_items_when_create_then_save_sale_and_publish_event_with_empty_items") {
            val saleId = UUID.randomUUID()
            val storeId = UUID.randomUUID()
            val sale = Sale(
                id = saleId,
                storeId = storeId,
                userId = UUID.randomUUID(),
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("0.00"),
                status = SaleStatus.PENDING,
                observations = null
            )

            every { saleRepositoryPort.save(sale) } returns sale
            every { saleEventProducer.publish(any()) } just Runs

            val result = useCase.createSale(sale, emptyList())

            verify(exactly = 1) { saleRepositoryPort.save(sale) }
            verify(exactly = 0) { saleItemRepositoryPort.save(any()) }

            val eventSlot = slot<SaleEvent>()
            verify(exactly = 1) { saleEventProducer.publish(capture(eventSlot)) }
            eventSlot.captured.items shouldBe emptyList()

            result shouldBe sale
        }
    }
})
