package com.siga.sales.controller

import com.siga.sales.application.usecase.CreateSaleUseCase
import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleItem
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [SaleController].
 * Pure hexagonal: controller depends only on ports — test with mocks, no Spring.
 */
class SaleControllerTest : DescribeSpec({

    val saleRepositoryPort = mockk<SaleRepositoryPort>()
    val createSaleUseCase = mockk<CreateSaleUseCase>()
    val controller = SaleController(saleRepositoryPort, createSaleUseCase)

    val sampleSale = Sale(
        id = UUID.randomUUID(),
        storeId = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        commercialUserId = null,
        createdAt = Instant.now(),
        total = BigDecimal("150.00"),
        status = SaleStatus.PENDING,
        observations = null
    )

    beforeEach {
        clearAllMocks()
    }

    describe("GET /api/v1/sales") {

        it("returns all sales") {
            val sales = listOf(sampleSale)
            every { saleRepositoryPort.findAll() } returns sales

            val response: ResponseEntity<List<Sale>> = controller.getAllSales()

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sales
        }

        it("returns empty list when no sales") {
            every { saleRepositoryPort.findAll() } returns emptyList()

            val response: ResponseEntity<List<Sale>> = controller.getAllSales()

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe emptyList()
        }
    }

    describe("GET /api/v1/sales/{id}") {

        it("returns 200 when sale exists") {
            every { saleRepositoryPort.findById(sampleSale.id) } returns sampleSale

            val response: ResponseEntity<Sale> = controller.getSaleById(sampleSale.id)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sampleSale
        }

        it("returns 404 when sale not found") {
            val id = UUID.randomUUID()
            every { saleRepositoryPort.findById(id) } returns null

            val response: ResponseEntity<Sale> = controller.getSaleById(id)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
        }
    }

    describe("GET /api/v1/sales/store/{storeId}") {

        it("returns sales for given store") {
            val storeId = UUID.randomUUID()
            val sales = listOf(sampleSale)
            every { saleRepositoryPort.findByStoreId(storeId) } returns sales

            val response: ResponseEntity<List<Sale>> = controller.getSalesByStore(storeId)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sales
        }
    }

    describe("GET /api/v1/sales/user/{userId}") {

        it("returns sales for given user") {
            val userId = UUID.randomUUID()
            val sales = listOf(sampleSale)
            every { saleRepositoryPort.findByUserId(userId) } returns sales

            val response: ResponseEntity<List<Sale>> = controller.getSalesByUser(userId)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sales
        }
    }

    describe("GET /api/v1/sales/status/{status}") {

        it("returns 200 for valid status") {
            val sales = listOf(sampleSale)
            every { saleRepositoryPort.findByStatus(SaleStatus.PENDING) } returns sales

            val response: ResponseEntity<List<Sale>> = controller.getSalesByStatus("PENDING")

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sales
        }

        it("returns 200 for lowercase status") {
            val sales = listOf(sampleSale)
            every { saleRepositoryPort.findByStatus(SaleStatus.COMPLETED) } returns sales

            val response: ResponseEntity<List<Sale>> = controller.getSalesByStatus("completed")

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sales
        }

        it("returns 400 for invalid status string") {
            val response: ResponseEntity<List<Sale>> = controller.getSalesByStatus("INVALID_STATUS")

            response.statusCode shouldBe HttpStatus.BAD_REQUEST
        }
    }

    describe("POST /api/v1/sales") {

        it("calls use case and returns created sale") {
            val items = listOf(
                SaleItem(
                    id = UUID.randomUUID(),
                    saleId = sampleSale.id,
                    productId = UUID.randomUUID(),
                    quantity = 2,
                    unitPrice = BigDecimal("75.00"),
                    subtotal = BigDecimal("150.00")
                )
            )
            val request = CreateSaleRequest(sale = sampleSale, items = items)

            every { createSaleUseCase.createSale(sampleSale, items) } returns sampleSale

            val response: ResponseEntity<Sale> = controller.createSale(request)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldBe sampleSale
            verify(exactly = 1) { createSaleUseCase.createSale(sampleSale, items) }
        }

        it("calls use case with empty items list") {
            val request = CreateSaleRequest(sale = sampleSale, items = emptyList())

            every { createSaleUseCase.createSale(sampleSale, emptyList()) } returns sampleSale

            val response: ResponseEntity<Sale> = controller.createSale(request)

            response.statusCode shouldBe HttpStatus.OK
            verify(exactly = 1) { createSaleUseCase.createSale(sampleSale, emptyList()) }
        }
    }

    describe("PUT /api/v1/sales/{id}") {

        it("returns 200 and updates sale when exists") {
            val existingSale = sampleSale.copy(status = SaleStatus.PENDING)
            val updateData = sampleSale.copy(
                total = BigDecimal("200.00"),
                status = SaleStatus.COMPLETED,
                observations = "Updated"
            )

            every { saleRepositoryPort.findById(sampleSale.id) } returns existingSale
            every { saleRepositoryPort.save(any()) } answers { firstArg() }

            val response: ResponseEntity<Sale> = controller.updateSale(sampleSale.id, updateData)

            response.statusCode shouldBe HttpStatus.OK
            response.body shouldNotBe null
            response.body?.total shouldBe BigDecimal("200.00")
            response.body?.status shouldBe SaleStatus.COMPLETED
            response.body?.observations shouldBe "Updated"
        }

        it("returns 404 when sale not found for update") {
            val id = UUID.randomUUID()
            every { saleRepositoryPort.findById(id) } returns null

            val response: ResponseEntity<Sale> = controller.updateSale(id, sampleSale)

            response.statusCode shouldBe HttpStatus.NOT_FOUND
            verify(exactly = 0) { saleRepositoryPort.save(any()) }
        }
    }
})
