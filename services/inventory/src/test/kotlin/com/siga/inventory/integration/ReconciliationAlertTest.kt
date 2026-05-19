package com.siga.inventory.integration

import com.siga.inventory.application.usecase.ReconcileRequest
import com.siga.inventory.application.usecase.ReconcileStockUseCase
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.AlertRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.UUID

/**
 * Integration test for [ReconcileStockUseCase] alert threshold.
 *
 * Verifies that when a physical count discrepancy exceeds 10% of the
 * system stock, an alert of type [AlertType.SUSPICIOUS_MOVEMENT] is created.
 * Also verifies that discrepancies under 10% do NOT create alerts.
 *
 * @see ReconcileStockUseCase
 * @see com.siga.inventory.domain.model.Alert
 */
@SpringBootTest
@ActiveProfiles("test")
class ReconciliationAlertTest : DescribeSpec() {

    @Autowired
    private lateinit var reconcileUseCase: ReconcileStockUseCase

    @Autowired
    private lateinit var stockPort: StockRepositoryPort

    @Autowired
    private lateinit var alertPort: AlertRepositoryPort

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("ReconcileStockUseCase alert creation") {

            val productId = UUID.randomUUID()
            val storeId = UUID.randomUUID()
            val userId = UUID.randomUUID()

            it("creates alert when discrepancy exceeds 10%") {
                // Given: 100 units in system
                stockPort.save(Stock(productId = productId, storeId = storeId, quantity = 100))

                // When: physical count is 80 (20% discrepancy > 10%)
                val request = ReconcileRequest(
                    productId = productId,
                    storeId = storeId,
                    physicalCount = 80,
                    motive = "MERMA",
                    userId = userId
                )
                val response = reconcileUseCase.execute(request)

                // Then: response indicates alert was created
                response.alertCreated shouldBe true
                response.previousStock shouldBe 100
                response.newStock shouldBe 80
                response.discrepancy shouldBe -20

                // And: stock was adjusted to physical count
                val adjustedStock = stockPort.findByProductIdAndStoreId(productId, storeId)
                adjustedStock.shouldNotBeNull()
                adjustedStock!!.quantity shouldBe 80

                // And: alert was persisted
                val alerts = alertPort.findByStoreId(storeId)
                alerts.size shouldBe 1
                alerts[0].type shouldBe com.siga.inventory.domain.model.AlertType.SUSPICIOUS_MOVEMENT
                alerts[0].productId shouldBe productId
                alerts[0].storeId shouldBe storeId
                alerts[0].isRead shouldBe false
                alerts[0].message shouldContain "20%"
                alerts[0].message shouldContain "MERMA"
            }

            it("does NOT create alert when discrepancy is under 10%") {
                val productIdB = UUID.randomUUID()
                val storeIdB = UUID.randomUUID()

                // Given: 100 units in system
                stockPort.save(Stock(productId = productIdB, storeId = storeIdB, quantity = 100))

                // When: physical count is 95 (5% discrepancy < 10%)
                val request = ReconcileRequest(
                    productId = productIdB,
                    storeId = storeIdB,
                    physicalCount = 95,
                    motive = "AJUSTE",
                    userId = userId
                )
                val response = reconcileUseCase.execute(request)

                // Then: no alert created
                response.alertCreated shouldBe false
                response.newStock shouldBe 95
                response.discrepancy shouldBe -5

                // And: no alerts for this store
                val alerts = alertPort.findByStoreId(storeIdB)
                alerts.size shouldBe 0
            }

            it("creates alert when previous stock was zero and physical count > 0") {
                val productIdC = UUID.randomUUID()
                val storeIdC = UUID.randomUUID()

                // Given: 0 units in system
                stockPort.save(Stock(productId = productIdC, storeId = storeIdC, quantity = 0))

                // When: physical count is 15 (infinite % discrepancy > 10%)
                val request = ReconcileRequest(
                    productId = productIdC,
                    storeId = storeIdC,
                    physicalCount = 15,
                    motive = "INGRESO_NO_REGISTRADO",
                    userId = userId
                )
                val response = reconcileUseCase.execute(request)

                // Then: alert created (previous=0, any positive discrepancy > 10%)
                response.alertCreated shouldBe true
                response.newStock shouldBe 15
                response.discrepancy shouldBe 15
            }

            it("adjusts stock to exact physical count for exact match (no alert)") {
                val productIdD = UUID.randomUUID()
                val storeIdD = UUID.randomUUID()

                // Given: 100 units in system
                stockPort.save(Stock(productId = productIdD, storeId = storeIdD, quantity = 100))

                // When: physical count matches exactly
                val request = ReconcileRequest(
                    productId = productIdD,
                    storeId = storeIdD,
                    physicalCount = 100,
                    motive = "CONTEO",
                    userId = userId
                )
                val response = reconcileUseCase.execute(request)

                // Then: no alert, no change
                response.alertCreated shouldBe false
                response.discrepancy shouldBe 0
                response.previousStock shouldBe 100
                response.newStock shouldBe 100

                val stock = stockPort.findByProductIdAndStoreId(productIdD, storeIdD)
                stock?.quantity shouldBe 100
            }

            it("throws exception for negative physical count") {
                val request = ReconcileRequest(
                    productId = productId,
                    storeId = storeId,
                    physicalCount = -1,
                    motive = "ERROR",
                    userId = userId
                )
                val ex = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                    reconcileUseCase.execute(request)
                }
                ex.message shouldBe "Physical count must be >= 0"
            }

            it("throws exception when product not found at store") {
                val request = ReconcileRequest(
                    productId = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    physicalCount = 10,
                    motive = "TEST",
                    userId = userId
                )
                val ex = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
                    reconcileUseCase.execute(request)
                }
                ex.message shouldBe "Product not found at store"
            }
        }
    }
}

/**
 * Custom matcher extension to check string containment.
 */
private infix fun String.shouldContain(substring: String) {
    org.junit.jupiter.api.Assertions.assertTrue(this.contains(substring)) {
        "Expected string to contain '$substring' but was '$this'"
    }
}
