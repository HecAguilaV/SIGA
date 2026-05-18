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
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Instant
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

            it("findByFilters with storeId filter") {
                val storeA = UUID.randomUUID()
                val storeB = UUID.randomUUID()

                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = UUID.randomUUID(), storeId = storeA,
                    type = MovementType.ADJUSTMENT, quantity = 10, previousQuantity = 20,
                    newQuantity = 10, userId = null, saleId = null, observations = null
                ))
                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = UUID.randomUUID(), storeId = storeB,
                    type = MovementType.ADJUSTMENT, quantity = 5, previousQuantity = 15,
                    newQuantity = 10, userId = null, saleId = null, observations = null
                ))

                val result = adapter.findByFilters(
                    storeId = storeA, type = null, from = null, to = null,
                    pageable = PageRequest.of(0, 20)
                )
                result.totalElements shouldBe 1
                result.content[0].storeId shouldBe storeA
            }

            it("findByFilters with movement type filter") {
                // Use a unique storeId so we don't pick up data from other tests
                val uniqueStoreId = UUID.randomUUID()
                val uniqueProductId = UUID.randomUUID()
                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = uniqueProductId, storeId = uniqueStoreId,
                    type = MovementType.SALE, quantity = 3, previousQuantity = 10,
                    newQuantity = 7, userId = null, saleId = null, observations = null
                ))
                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = UUID.randomUUID(), storeId = uniqueStoreId,
                    type = MovementType.ADJUSTMENT, quantity = 5, previousQuantity = 10,
                    newQuantity = 15, userId = null, saleId = null, observations = null
                ))

                // Add storeId filter to isolate test data
                val result = adapter.findByFilters(
                    storeId = uniqueStoreId, type = MovementType.SALE, from = null, to = null,
                    pageable = PageRequest.of(0, 20)
                )
                result.totalElements shouldBe 1
                result.content[0].type shouldBe MovementType.SALE
                result.content[0].productId shouldBe uniqueProductId
            }

            it("findByFilters with date range filter") {
                // Use a unique storeId so we don't pick up data from other tests
                val uniqueStoreId = UUID.randomUUID()
                val uniqueProductId = UUID.randomUUID()
                val now = Instant.now()

                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = uniqueProductId, storeId = uniqueStoreId,
                    type = MovementType.ENTRY, quantity = 50, previousQuantity = 0,
                    newQuantity = 50, userId = null, saleId = null, observations = null
                ))

                // Add storeId filter to isolate test data
                val result = adapter.findByFilters(
                    storeId = uniqueStoreId, type = null, from = now.minusSeconds(3600), to = now.plusSeconds(3600),
                    pageable = PageRequest.of(0, 20)
                )
                result.totalElements shouldBe 1
                result.content[0].productId shouldBe uniqueProductId
            }

            it("findByFilters with all filters combined") {
                val storeId = UUID.randomUUID()
                val now = Instant.now()

                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = UUID.randomUUID(), storeId = storeId,
                    type = MovementType.TRANSFER, quantity = 10, previousQuantity = 30,
                    newQuantity = 20, userId = UUID.randomUUID(), saleId = null, observations = "Transfer OUT"
                ))
                // Different store — should not match
                adapter.save(Movement(
                    id = UUID.randomUUID(), productId = UUID.randomUUID(), storeId = UUID.randomUUID(),
                    type = MovementType.TRANSFER, quantity = 5, previousQuantity = 10,
                    newQuantity = 15, userId = UUID.randomUUID(), saleId = null, observations = "Transfer elsewhere"
                ))

                val result = adapter.findByFilters(
                    storeId = storeId, type = MovementType.TRANSFER,
                    from = now.minusSeconds(3600), to = now.plusSeconds(3600),
                    pageable = PageRequest.of(0, 20)
                )
                result.totalElements shouldBe 1
                result.content[0].storeId shouldBe storeId
                result.content[0].type shouldBe MovementType.TRANSFER
            }

            it("findByFilters returns empty page when no matches") {
                val result = adapter.findByFilters(
                    storeId = UUID.randomUUID(), type = null, from = null, to = null,
                    pageable = PageRequest.of(0, 20)
                )
                result.totalElements shouldBe 0
                result.content shouldBe emptyList()
            }
        }
    }
}
