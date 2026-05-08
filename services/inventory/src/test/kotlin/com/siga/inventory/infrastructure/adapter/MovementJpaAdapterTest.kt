package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.UUID

/**
 * Integration test for [MovementJpaAdapter].
 * Verifies Movement audit trail persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class MovementJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: MovementJpaAdapter

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("MovementJpaAdapter") {

            it("save and find by sale id") {
                val saleId = UUID.randomUUID()
                val movement = Movement(
                    id = UUID.randomUUID(),
                    productId = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    type = MovementType.SALE,
                    quantity = 5,
                    previousQuantity = 20,
                    newQuantity = 15,
                    userId = UUID.randomUUID(),
                    saleId = saleId,
                    observations = "Sale deduction"
                )

                val saved = adapter.save(movement)
                saved.id shouldNotBe null
                saved.productId shouldBe movement.productId
                saved.quantity shouldBe 5
                saved.type shouldBe MovementType.SALE

                val found = adapter.findBySaleId(saleId)
                found.size shouldBe 1
                found[0].id shouldBe saved.id
                found[0].newQuantity shouldBe 15
            }

            it("findBySaleId returns empty list when no movements") {
                val movements = adapter.findBySaleId(UUID.randomUUID())
                movements shouldBe emptyList()
            }

            it("save ADJUSTMENT movement") {
                val movement = Movement(
                    id = UUID.randomUUID(),
                    productId = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    type = MovementType.ADJUSTMENT,
                    quantity = 10,
                    previousQuantity = 30,
                    newQuantity = 40,
                    userId = UUID.randomUUID(),
                    saleId = null,
                    observations = "Inventory adjustment"
                )

                val saved = adapter.save(movement)
                saved.type shouldBe MovementType.ADJUSTMENT

                // no saleId associated — ADJUSTMENT movement should not be found by saleId
                val movements = adapter.findBySaleId(UUID.randomUUID())
                movements.none { it.id == saved.id } shouldBe true
            }

            it("save ENTRY movement") {
                val movement = Movement(
                    id = UUID.randomUUID(),
                    productId = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    type = MovementType.ENTRY,
                    quantity = 50,
                    previousQuantity = 0,
                    newQuantity = 50,
                    userId = UUID.randomUUID(),
                    saleId = null,
                    observations = "Stock entry"
                )

                val saved = adapter.save(movement)
                saved.type shouldBe MovementType.ENTRY
                saved.quantity shouldBe 50
                saved.newQuantity shouldBe 50
            }

            it("save movement with null optional fields") {
                val movement = Movement(
                    id = UUID.randomUUID(),
                    productId = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    type = MovementType.ADJUSTMENT,
                    quantity = 5,
                    previousQuantity = 10,
                    newQuantity = 5,
                    userId = null,
                    saleId = null,
                    observations = null
                )

                val saved = adapter.save(movement)
                saved.userId shouldBe null
                saved.observations shouldBe null
            }
        }
    }
}
