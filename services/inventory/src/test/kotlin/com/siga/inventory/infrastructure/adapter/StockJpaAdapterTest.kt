package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.Stock
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
 * Integration test for [StockJpaAdapter].
 * Verifies Stock persistence through the hexagonal port with H2.
 *
 * Stock is identified by productId + storeId (no id in domain model).
 * The adapter uses fetch-and-update: finds existing stock, updates quantity,
 * or creates a new record if none exists.
 */
@SpringBootTest
@ActiveProfiles("test")
class StockJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: StockJpaAdapter

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("StockJpaAdapter") {

            it("save new stock and find by productId and storeId") {
                val productId = UUID.randomUUID()
                val storeId = UUID.randomUUID()
                val stock = Stock(
                    productId = productId,
                    storeId = storeId,
                    quantity = 100
                )

                val saved = adapter.save(stock)
                saved.productId shouldBe productId
                saved.storeId shouldBe storeId
                saved.quantity shouldBe 100

                val found = adapter.findByProductIdAndStoreId(productId, storeId)
                found shouldNotBe null
                found?.quantity shouldBe 100
            }

            it("findByProductIdAndStoreId returns null when stock does not exist") {
                val found = adapter.findByProductIdAndStoreId(UUID.randomUUID(), UUID.randomUUID())
                found shouldBe null
            }

            it("save updates existing stock quantity") {
                val productId = UUID.randomUUID()
                val storeId = UUID.randomUUID()

                // First save: create
                adapter.save(Stock(productId = productId, storeId = storeId, quantity = 50))

                // Second save: update (increment)
                adapter.save(Stock(productId = productId, storeId = storeId, quantity = 30))

                val found = adapter.findByProductIdAndStoreId(productId, storeId)
                found shouldNotBe null
                found?.quantity shouldBe 30
            }

            it("handles independent stock records for different stores") {
                val productId = UUID.randomUUID()
                val storeA = UUID.randomUUID()
                val storeB = UUID.randomUUID()

                adapter.save(Stock(productId = productId, storeId = storeA, quantity = 100))
                adapter.save(Stock(productId = productId, storeId = storeB, quantity = 200))

                val foundA = adapter.findByProductIdAndStoreId(productId, storeA)
                val foundB = adapter.findByProductIdAndStoreId(productId, storeB)

                foundA?.quantity shouldBe 100
                foundB?.quantity shouldBe 200
            }

            it("save sets zero quantity correctly") {
                val productId = UUID.randomUUID()
                val storeId = UUID.randomUUID()

                adapter.save(Stock(productId = productId, storeId = storeId, quantity = 0))

                val found = adapter.findByProductIdAndStoreId(productId, storeId)
                found shouldNotBe null
                found?.quantity shouldBe 0
            }
        }
    }
}
