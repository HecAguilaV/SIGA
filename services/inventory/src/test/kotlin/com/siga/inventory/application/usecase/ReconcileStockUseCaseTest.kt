package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Alert
import com.siga.inventory.domain.model.AlertType
import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.AlertRepositoryPort
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID

class ReconcileStockUseCaseTest : DescribeSpec({

    val stockPort = mockk<StockRepositoryPort>()
    val movementPort = mockk<MovementRepositoryPort>()
    val alertPort = mockk<AlertRepositoryPort>()
    val useCase = ReconcileStockUseCase(stockPort, movementPort, alertPort)

    val now = Instant.now()

    beforeEach {
        clearAllMocks()
    }
    val productId = UUID.randomUUID()
    val storeId = UUID.randomUUID()
    val userId = UUID.randomUUID()

    describe("execute") {

        it("should adjust stock and create reconciliation movement when discrepancy exists") {
            val currentStock = Stock(productId, storeId, 45, now)
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 12,
                motive = "MERMA",
                userId = userId
            )

            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns currentStock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }
            every { alertPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request)

            result.productId shouldBe productId
            result.storeId shouldBe storeId
            result.previousStock shouldBe 45
            result.newStock shouldBe 12
            result.discrepancy shouldBe -33
            result.motive shouldBe "MERMA"
            result.alertCreated shouldBe true

            verify { stockPort.save(match { it.quantity == 12 }) }
            verify {
                movementPort.save(match {
                    it.type == MovementType.RECONCILIATION &&
                        it.productId == productId &&
                        it.storeId == storeId &&
                        it.previousQuantity == 45 &&
                        it.newQuantity == 12
                })
            }
            verify { alertPort.save(any()) }
        }

        it("should create alert when discrepancy exceeds 10% of system stock") {
            val currentStock = Stock(productId, storeId, 45, now)
            val alertId = UUID.randomUUID()
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 12,
                motive = "MERMA",
                userId = userId
            )

            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns currentStock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }
            every { alertPort.save(any()) } answers {
                firstArg<Alert>().copy(id = alertId)
            }

            val result = useCase.execute(request)

            result.alertCreated shouldBe true
            verify { alertPort.save(match { it.type == AlertType.SUSPICIOUS_MOVEMENT }) }
        }

        it("should NOT create alert when discrepancy is 10% or less of system stock") {
            val currentStock = Stock(productId, storeId, 100, now)
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 95,
                motive = "ERROR_INGRESO",
                userId = userId
            )

            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns currentStock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request)

            result.alertCreated shouldBe false
            verify(exactly = 0) { alertPort.save(any()) }
        }

        it("should handle zero discrepancy gracefully") {
            val currentStock = Stock(productId, storeId, 30, now)
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 30,
                motive = "OTRO",
                userId = userId
            )

            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns currentStock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request)

            result.previousStock shouldBe 30
            result.newStock shouldBe 30
            result.discrepancy shouldBe 0
            result.alertCreated shouldBe false
            verify(exactly = 0) { alertPort.save(any()) }
        }

        it("should throw exception when product stock not found at store") {
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 10,
                motive = "OTRO",
                userId = userId
            )

            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                useCase.execute(request)
            }
            exception.message shouldBe "Product not found at store"
        }

        it("should throw exception when physical count is negative") {
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = -5,
                motive = "OTRO",
                userId = userId
            )

            val exception = shouldThrow<IllegalArgumentException> {
                useCase.execute(request)
            }
            exception.message shouldBe "Physical count must be >= 0"
        }

        it("should calculate discrepancy correctly when physical count is higher than system") {
            val currentStock = Stock(productId, storeId, 10, now)
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 20,
                motive = "ERROR_INGRESO",
                userId = userId
            )

            every { stockPort.findByProductIdAndStoreId(productId, storeId) } returns currentStock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }
            every { alertPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(request)

            result.discrepancy shouldBe 10
            result.previousStock shouldBe 10
            result.newStock shouldBe 20
        }
    }
})
