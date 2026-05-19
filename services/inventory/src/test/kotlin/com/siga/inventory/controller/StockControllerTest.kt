package com.siga.inventory.controller

import com.siga.inventory.application.usecase.ConsolidatedStockResponse
import com.siga.inventory.application.usecase.ConsolidatedStockUseCase
import com.siga.inventory.application.usecase.ReconcileRequest
import com.siga.inventory.application.usecase.ReconcileResponse
import com.siga.inventory.application.usecase.TransferStockUseCase
import com.siga.inventory.application.usecase.TransferMovementHistoryUseCase
import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.StockRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import java.time.Instant
import java.util.UUID

class StockControllerTest : DescribeSpec({

    val stockRepository = mockk<StockRepository>()
    val consolidatedStockUseCase = mockk<ConsolidatedStockUseCase>()
    val reconcileStockUseCase = mockk<com.siga.inventory.application.usecase.ReconcileStockUseCase>()
    val transferStockUseCase = mockk<TransferStockUseCase>()
    val transferMovementHistoryUseCase = mockk<TransferMovementHistoryUseCase>()
    val controller = StockController(
        stockRepository,
        consolidatedStockUseCase,
        reconcileStockUseCase,
        transferStockUseCase,
        transferMovementHistoryUseCase
    )

    val productId = UUID.randomUUID()
    val storeId = UUID.randomUUID()

    beforeAny {
        clearAllMocks()
    }

    describe("StockController") {

        describe("getStockByProduct") {

            it("given existing stock when getting by product then should return 200 OK with stock list") {
                val stocks = listOf(
                    Stock(productId = productId, storeId = storeId, quantity = 10)
                )
                every { stockRepository.findByProductId(productId) } returns stocks

                val response = controller.getStockByProduct(productId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
                response.body?.first()?.quantity shouldBe 10
            }

            it("given no stock when getting by product then should return 200 OK with empty list") {
                every { stockRepository.findByProductId(productId) } returns emptyList()

                val response = controller.getStockByProduct(productId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.isEmpty() shouldBe true
            }
        }

        describe("getStockByStore") {

            it("given existing stock when getting by store then should return 200 OK") {
                every { stockRepository.findByStoreId(storeId) } returns listOf(
                    Stock(productId = productId, storeId = storeId, quantity = 5)
                )

                val response = controller.getStockByStore(storeId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }
        }

        describe("getStockByProductAndStore") {

            it("given existing stock when getting by product and store then should return 200 OK") {
                val stock = Stock(productId = productId, storeId = storeId, quantity = 10)
                every { stockRepository.findByProductIdAndStoreId(productId, storeId) } returns stock

                val response = controller.getStockByProductAndStore(productId, storeId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.quantity shouldBe 10
            }

            it("given no stock when getting by product and store then should return 200 OK") {
                every { stockRepository.findByProductIdAndStoreId(productId, storeId) } returns null

                val response = controller.getStockByProductAndStore(productId, storeId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        // --- New endpoints (Phase 4.1) ---

        describe("getConsolidatedStock") {

            it("given valid storeId when requesting consolidated stock then should return 200 OK") {
                val responseDto = ConsolidatedStockResponse(
                    products = emptyList(),
                    page = 0,
                    size = 50,
                    totalElements = 0L,
                    totalPages = 0
                )
                every { consolidatedStockUseCase.execute(storeId, 0, 50) } returns responseDto

                val response = controller.getConsolidatedStock(storeId, 0, 50)
                val body = response.body as? ConsolidatedStockResponse

                response.statusCode shouldBe HttpStatus.OK
                body?.page shouldBe 0
                body?.totalElements shouldBe 0
            }

            it("given no filters when requesting consolidated stock then should return all products") {
                val responseDto = ConsolidatedStockResponse(
                    products = emptyList(),
                    page = 0,
                    size = 50,
                    totalElements = 0L,
                    totalPages = 0
                )
                every { consolidatedStockUseCase.execute(null, 0, 50) } returns responseDto

                val response = controller.getConsolidatedStock(null, 0, 50)

                response.statusCode shouldBe HttpStatus.OK
            }

            it("given pagination params when requesting consolidated stock then should pass them to use case") {
                val responseDto = ConsolidatedStockResponse(
                    products = emptyList(),
                    page = 1,
                    size = 10,
                    totalElements = 0L,
                    totalPages = 0
                )
                every { consolidatedStockUseCase.execute(null, 1, 10) } returns responseDto

                val response = controller.getConsolidatedStock(null, 1, 10)
                val body = response.body as? ConsolidatedStockResponse

                response.statusCode shouldBe HttpStatus.OK
                body?.page shouldBe 1
                body?.size shouldBe 10
            }
        }

        describe("reconcileStock") {

            it("given valid reconcile request when reconciling then should return 200 OK") {
                val req = ReconcileRequest(
                    productId = productId,
                    storeId = storeId,
                    physicalCount = 12,
                    motive = "MERMA",
                    userId = UUID.randomUUID()
                )
                val resp = ReconcileResponse(
                    reconciliationId = UUID.randomUUID(),
                    productId = productId,
                    storeId = storeId,
                    previousStock = 20,
                    newStock = 12,
                    discrepancy = -8,
                    motive = "MERMA",
                    reconciledBy = req.userId,
                    reconciledAt = Instant.now(),
                    alertCreated = false
                )
                every { reconcileStockUseCase.execute(req) } returns resp

                val response = controller.reconcileStock(req)
                val body = response.body as? ReconcileResponse

                response.statusCode shouldBe HttpStatus.OK
                body?.newStock shouldBe 12
                body?.discrepancy shouldBe -8
            }

            it("given reconcile request with invalid product when reconciling then should return 404") {
                val req = ReconcileRequest(
                    productId = productId,
                    storeId = storeId,
                    physicalCount = 5,
                    motive = "MERMA",
                    userId = UUID.randomUUID()
                )
                every { reconcileStockUseCase.execute(req) } throws IllegalArgumentException("Product not found at store")

                val response = controller.reconcileStock(req)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }

        describe("transferStock") {

            it("given valid transfer request when transferring then should return 201 Created") {
                val req = TransferRequest(
                    productId = productId,
                    originStoreId = UUID.randomUUID(),
                    destinationStoreId = UUID.randomUUID(),
                    quantity = 50
                )
                val resp = com.siga.inventory.application.usecase.TransferResponse(
                    transferId = UUID.randomUUID(),
                    correlationId = UUID.randomUUID(),
                    productId = productId,
                    originStoreId = req.originStoreId,
                    destinationStoreId = req.destinationStoreId,
                    quantity = 50,
                    originNewStock = 150,
                    destinationNewStock = 50,
                    transferredBy = null,
                    transferredAt = Instant.now()
                )
                every { transferStockUseCase.execute(req.productId, req.originStoreId, req.destinationStoreId, req.quantity, null) } returns resp

                val response = controller.transferStock(req)
                val body = response.body as? com.siga.inventory.application.usecase.TransferResponse

                response.statusCode shouldBe HttpStatus.CREATED
                body?.quantity shouldBe 50
                body?.originNewStock shouldBe 150
            }

            it("given invalid quantity when transferring then should return 400") {
                val req = TransferRequest(
                    productId = productId,
                    originStoreId = UUID.randomUUID(),
                    destinationStoreId = UUID.randomUUID(),
                    quantity = -5
                )
                every { transferStockUseCase.execute(req.productId, req.originStoreId, req.destinationStoreId, req.quantity, null) } throws IllegalArgumentException("Quantity must be positive")

                val response = controller.transferStock(req)

                response.statusCode shouldBe HttpStatus.BAD_REQUEST
            }

            it("given insufficient stock when transferring then should return 409") {
                val req = TransferRequest(
                    productId = productId,
                    originStoreId = UUID.randomUUID(),
                    destinationStoreId = UUID.randomUUID(),
                    quantity = 999
                )
                every { transferStockUseCase.execute(req.productId, req.originStoreId, req.destinationStoreId, req.quantity, null) } throws IllegalStateException("Insufficient stock")

                val response = controller.transferStock(req)

                response.statusCode shouldBe HttpStatus.CONFLICT
            }

            it("given same origin and destination when transferring then should return 400") {
                val req = TransferRequest(
                    productId = productId,
                    originStoreId = storeId,
                    destinationStoreId = storeId,
                    quantity = 10
                )
                every { transferStockUseCase.execute(req.productId, req.originStoreId, req.destinationStoreId, req.quantity, null) } throws IllegalArgumentException("Origin and destination must be different")

                val response = controller.transferStock(req)

                response.statusCode shouldBe HttpStatus.BAD_REQUEST
            }
        }

        describe("getMovements") {

            it("given filters when requesting movements then should return 200 OK with page") {
                val pageable = PageRequest.of(0, 20)
                val movement = Movement(
                    id = UUID.randomUUID(),
                    productId = productId,
                    storeId = storeId,
                    type = MovementType.TRANSFER,
                    quantity = 10,
                    previousQuantity = 100,
                    newQuantity = 90,
                    userId = null,
                    saleId = null,
                    observations = "Test movement",
                    correlationId = UUID.randomUUID(),
                    destinationStoreId = UUID.randomUUID()
                )
                val page: Page<Movement> = PageImpl(listOf(movement), pageable, 1)
                every { transferMovementHistoryUseCase.execute(storeId, MovementType.TRANSFER, null, null, pageable) } returns page

                val response = controller.getMovements(storeId, MovementType.TRANSFER, null, null, pageable)

                response.statusCode shouldBe HttpStatus.OK
            }

            it("given empty filters when requesting movements then should return all movements") {
                val pageable = PageRequest.of(0, 20)
                val page: Page<Movement> = PageImpl(emptyList(), pageable, 0)
                every { transferMovementHistoryUseCase.execute(null, null, null, null, pageable) } returns page

                val response = controller.getMovements(null, null, null, null, pageable)

                response.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})
