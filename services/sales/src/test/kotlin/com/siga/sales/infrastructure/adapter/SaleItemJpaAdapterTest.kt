package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.SaleItem
import com.siga.sales.event.SaleEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

/**
 * Integration test for [SaleItemJpaAdapter].
 * Verifies SaleItem persistence through the hexagonal port with H2.
 */
@SpringBootTest(properties = ["spring.kafka.bootstrap-servers=localhost:9092"])
@ActiveProfiles("test")
@Transactional
class SaleItemJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: SaleItemJpaAdapter

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    init {
        extension(SpringExtension())

        describe("SaleItemJpaAdapter") {

            it("save and find by id") {
                val item = SaleItem(
                    id = UUID.randomUUID(),
                    saleId = UUID.randomUUID(),
                    productId = UUID.randomUUID(),
                    quantity = 2,
                    unitPrice = BigDecimal("50.00"),
                    subtotal = BigDecimal("100.00")
                )

                val saved = adapter.save(item)
                saved.id shouldBe item.id
                saved.saleId shouldBe item.saleId
                saved.productId shouldBe item.productId
                saved.quantity shouldBe 2
                saved.unitPrice shouldBe BigDecimal("50.00")
                saved.subtotal shouldBe BigDecimal("100.00")

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.productId shouldBe item.productId
                found?.quantity shouldBe 2
            }

            it("findById returns null when item does not exist") {
                val found = adapter.findById(UUID.randomUUID())
                found shouldBe null
            }

            it("findBySaleId returns all items for a sale") {
                val saleId = UUID.randomUUID()
                val item1 = SaleItem(
                    id = UUID.randomUUID(), saleId = saleId,
                    productId = UUID.randomUUID(), quantity = 1,
                    unitPrice = BigDecimal("100.00"), subtotal = BigDecimal("100.00")
                )
                val item2 = SaleItem(
                    id = UUID.randomUUID(), saleId = saleId,
                    productId = UUID.randomUUID(), quantity = 3,
                    unitPrice = BigDecimal("50.00"), subtotal = BigDecimal("150.00")
                )

                adapter.save(item1)
                adapter.save(item2)

                val items = adapter.findBySaleId(saleId)
                items.size shouldBe 2
                items.any { it.id == item1.id } shouldBe true
                items.any { it.id == item2.id } shouldBe true
            }

            it("findBySaleId returns empty list when no items") {
                val items = adapter.findBySaleId(UUID.randomUUID())
                items shouldBe emptyList()
            }

            it("update item by saving with same id") {
                val item = SaleItem(
                    id = UUID.randomUUID(), saleId = UUID.randomUUID(),
                    productId = UUID.randomUUID(), quantity = 1,
                    unitPrice = BigDecimal("100.00"), subtotal = BigDecimal("100.00")
                )
                val saved = adapter.save(item)

                val updated = saved.copy(quantity = 5, subtotal = BigDecimal("500.00"))
                adapter.save(updated)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.quantity shouldBe 5
                found?.subtotal shouldBe BigDecimal("500.00")
            }
        }
    }
}
