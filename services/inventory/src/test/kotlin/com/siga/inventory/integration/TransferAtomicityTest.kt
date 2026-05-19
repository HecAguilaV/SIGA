package com.siga.inventory.integration

import com.siga.inventory.application.usecase.TransferStockUseCase
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.UUID

/**
 * Integration test for [TransferStockUseCase] @Transactional atomicity.
 *
 * Verifies that a stock transfer between two stores completes atomically:
 * - Origin stock decreases by the transferred quantity
 * - Destination stock increases by the transferred quantity
 * - Two Movement records (OUT + IN) are created with the same correlation UUID
 *
 * Uses [SpringBootTest] (full context) to exercise the real use case with
 * real adapters against H2. The test class itself is NOT [Transactional],
 * so changes persist in the database and can be verified with separate queries.
 *
 * @see TransferStockUseCase
 */
@SpringBootTest
@ActiveProfiles("test")
class TransferAtomicityTest : DescribeSpec() {

    @Autowired
    private lateinit var transferUseCase: TransferStockUseCase

    @Autowired
    private lateinit var stockPort: StockRepositoryPort

    @Autowired
    private lateinit var movementPort: MovementRepositoryPort

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("TransferStockUseCase atomic transfer") {

            val productId = UUID.randomUUID()
            val originStoreId = UUID.randomUUID()
            val destinationStoreId = UUID.randomUUID()

            it("completes a full transfer and persists all changes") {
                // Given: initial stock at origin and destination
                stockPort.save(Stock(productId = productId, storeId = originStoreId, quantity = 100))
                stockPort.save(Stock(productId = productId, storeId = destinationStoreId, quantity = 50))

                // When: transferring 30 units
                val response = transferUseCase.execute(
                    productId = productId,
                    originStoreId = originStoreId,
                    destinationStoreId = destinationStoreId,
                    quantity = 30,
                    userId = null
                )

                // Then: response is valid
                response.correlationId.shouldNotBeNull()
                response.quantity shouldBe 30
                response.originNewStock shouldBe 70   // 100 - 30
                response.destinationNewStock shouldBe 80 // 50 + 30

                // And: origin stock is decreased
                val originStock = stockPort.findByProductIdAndStoreId(productId, originStoreId)
                originStock.shouldNotBeNull()
                originStock!!.quantity shouldBe 70

                // And: destination stock is increased
                val destStock = stockPort.findByProductIdAndStoreId(productId, destinationStoreId)
                destStock.shouldNotBeNull()
                destStock!!.quantity shouldBe 80

                // And: two movement records created with same correlationId
                val correlationId = response.correlationId

                // Load movements from adapter - use the stock movement port to find by filters
                val movements = movementPort.findByFilters(
                    storeId = null,
                    type = MovementType.TRANSFER,
                    from = null,
                    to = null,
                    pageable = org.springframework.data.domain.PageRequest.of(0, 100)
                )

                val transferMovements = movements.content.filter { it.correlationId == correlationId }
                transferMovements.size shouldBe 2

                val outMovement = transferMovements.find { it.storeId == originStoreId }
                outMovement.shouldNotBeNull()
                outMovement!!.type shouldBe MovementType.TRANSFER
                outMovement.quantity shouldBe 30
                outMovement.previousQuantity shouldBe 100
                outMovement.newQuantity shouldBe 70
                outMovement.correlationId shouldBe correlationId
                outMovement.destinationStoreId shouldBe destinationStoreId

                val inMovement = transferMovements.find { it.storeId == destinationStoreId }
                inMovement.shouldNotBeNull()
                inMovement!!.type shouldBe MovementType.TRANSFER
                inMovement.quantity shouldBe 30
                inMovement.previousQuantity shouldBe 50
                inMovement.newQuantity shouldBe 80
                inMovement.correlationId shouldBe correlationId
                inMovement.destinationStoreId shouldBe null
            }

            it("creates destination stock record when it does not exist") {
                val newProductId = UUID.randomUUID()
                val newOrigin = UUID.randomUUID()
                val newDest = UUID.randomUUID()

                // Given: stock only at origin, none at destination
                stockPort.save(Stock(productId = newProductId, storeId = newOrigin, quantity = 50))

                // When: transferring 20 units
                val response = transferUseCase.execute(
                    productId = newProductId,
                    originStoreId = newOrigin,
                    destinationStoreId = newDest,
                    quantity = 20,
                    userId = null
                )

                // Then: origin decreased
                response.originNewStock shouldBe 30
                val originStock = stockPort.findByProductIdAndStoreId(newProductId, newOrigin)
                originStock?.quantity shouldBe 30

                // And: destination created with the transferred quantity
                response.destinationNewStock shouldBe 20
                val destStock = stockPort.findByProductIdAndStoreId(newProductId, newDest)
                destStock.shouldNotBeNull()
                destStock!!.quantity shouldBe 20
            }

            it("fails with IllegalArgumentException for same origin and destination") {
                val ex = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                    transferUseCase.execute(
                        productId = UUID.randomUUID(),
                        originStoreId = originStoreId,
                        destinationStoreId = originStoreId,
                        quantity = 10,
                        userId = null
                    )
                }
                ex.message shouldBe "Origin and destination must be different"
            }

            it("fails with IllegalArgumentException for zero quantity") {
                val ex = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                    transferUseCase.execute(
                        productId = UUID.randomUUID(),
                        originStoreId = UUID.randomUUID(),
                        destinationStoreId = UUID.randomUUID(),
                        quantity = 0,
                        userId = null
                    )
                }
                ex.message shouldBe "Quantity must be positive"
            }

            it("fails with IllegalArgumentException when origin stock does not exist") {
                val ex = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                    transferUseCase.execute(
                        productId = UUID.randomUUID(),
                        originStoreId = UUID.randomUUID(),
                        destinationStoreId = UUID.randomUUID(),
                        quantity = 5,
                        userId = null
                    )
                }
                ex.message shouldBe "Product not found at origin store"
            }

            it("fails with IllegalStateException when origin stock is insufficient") {
                val newProductId = UUID.randomUUID()
                val newOrigin = UUID.randomUUID()
                val newDest = UUID.randomUUID()

                stockPort.save(Stock(productId = newProductId, storeId = newOrigin, quantity = 3))

                val ex = org.junit.jupiter.api.assertThrows<IllegalStateException> {
                    transferUseCase.execute(
                        productId = newProductId,
                        originStoreId = newOrigin,
                        destinationStoreId = newDest,
                        quantity = 10,
                        userId = null
                    )
                }
                ex.message shouldBe "Insufficient stock: available=3, requested=10"
            }
        }
    }
}
