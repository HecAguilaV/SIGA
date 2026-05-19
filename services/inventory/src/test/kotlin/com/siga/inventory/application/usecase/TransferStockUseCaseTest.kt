package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class TransferStockUseCaseTest : DescribeSpec({

    val stockPort = mockk<StockRepositoryPort>()
    val movementPort = mockk<MovementRepositoryPort>()
    val useCase = TransferStockUseCase(stockPort, movementPort)

    val productId = UUID.randomUUID()
    val originStoreId = UUID.randomUUID()
    val destStoreId = UUID.randomUUID()
    val userId = UUID.randomUUID()

    describe("execute") {

        it("should transfer stock from origin to destination successfully") {
            val originStock = Stock(productId, originStoreId, 200)
            val destStock = Stock(productId, destStoreId, 0)

            every { stockPort.findByProductIdAndStoreId(productId, originStoreId) } returns originStock
            every { stockPort.findByProductIdAndStoreId(productId, destStoreId) } returns destStock
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(
                productId = productId,
                originStoreId = originStoreId,
                destinationStoreId = destStoreId,
                quantity = 50,
                userId = userId
            )

            result.originNewStock shouldBe 150
            result.destinationNewStock shouldBe 50
            result.quantity shouldBe 50
            result.correlationId shouldNotBe null

            // Verify origin was debited
            verify { stockPort.save(match { it.storeId == originStoreId && it.quantity == 150 }) }
            // Verify destination was credited
            verify { stockPort.save(match { it.storeId == destStoreId && it.quantity == 50 }) }
            // Verify OUT movement was created with correlationId
            verify {
                movementPort.save(match {
                    it.type == MovementType.TRANSFER &&
                        it.storeId == originStoreId &&
                        it.quantity == 50 &&
                        it.destinationStoreId == destStoreId &&
                        it.correlationId == result.correlationId
                })
            }
            // Verify IN movement was created with same correlationId
            verify {
                movementPort.save(match {
                    it.type == MovementType.TRANSFER &&
                        it.storeId == destStoreId &&
                        it.quantity == 50 &&
                        it.correlationId == result.correlationId
                })
            }
        }

        it("should throw exception when origin and destination are the same store") {
            val exception = shouldThrow<IllegalArgumentException> {
                useCase.execute(
                    productId = productId,
                    originStoreId = originStoreId,
                    destinationStoreId = originStoreId,
                    quantity = 10,
                    userId = userId
                )
            }
            exception.message shouldBe "Origin and destination must be different"
        }

        it("should throw exception when quantity is zero or negative") {
            val exception = shouldThrow<IllegalArgumentException> {
                useCase.execute(
                    productId = productId,
                    originStoreId = originStoreId,
                    destinationStoreId = destStoreId,
                    quantity = 0,
                    userId = userId
                )
            }
            exception.message shouldBe "Quantity must be positive"

            val exception2 = shouldThrow<IllegalArgumentException> {
                useCase.execute(
                    productId = productId,
                    originStoreId = originStoreId,
                    destinationStoreId = destStoreId,
                    quantity = -5,
                    userId = userId
                )
            }
            exception2.message shouldBe "Quantity must be positive"
        }

        it("should throw exception when origin stock is insufficient") {
            val originStock = Stock(productId, originStoreId, 10)

            every { stockPort.findByProductIdAndStoreId(productId, originStoreId) } returns originStock

            val exception = shouldThrow<IllegalStateException> {
                useCase.execute(
                    productId = productId,
                    originStoreId = originStoreId,
                    destinationStoreId = destStoreId,
                    quantity = 20,
                    userId = userId
                )
            }
            exception.message shouldBe "Insufficient stock: available=10, requested=20"
        }

        it("should throw exception when origin stock not found") {
            every { stockPort.findByProductIdAndStoreId(productId, originStoreId) } returns null

            val exception = shouldThrow<IllegalArgumentException> {
                useCase.execute(
                    productId = productId,
                    originStoreId = originStoreId,
                    destinationStoreId = destStoreId,
                    quantity = 10,
                    userId = userId
                )
            }
            exception.message shouldBe "Product not found at origin store"
        }

        it("should create destination stock when it does not exist") {
            val originStock = Stock(productId, originStoreId, 100)

            every { stockPort.findByProductIdAndStoreId(productId, originStoreId) } returns originStock
            every { stockPort.findByProductIdAndStoreId(productId, destStoreId) } returns null
            every { stockPort.save(any()) } answers { firstArg() }
            every { movementPort.save(any()) } answers { firstArg() }

            val result = useCase.execute(
                productId = productId,
                originStoreId = originStoreId,
                destinationStoreId = destStoreId,
                quantity = 30,
                userId = userId
            )

            result.originNewStock shouldBe 70
            result.destinationNewStock shouldBe 30
        }
    }
})
