package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
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
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [SaleJpaAdapter].
 * Verifies the persistence contract through the hexagonal port
 * using a real H2 database (no mocking).
 */
class SaleJpaAdapterTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var adapter: SaleJpaAdapter

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    init {
        describe("SaleJpaAdapter") {

            it("save and find by id") {
                val sale = Sale(
                    id = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    commercialUserId = null,
                    createdAt = Instant.now(),
                    total = BigDecimal("150.00"),
                    status = SaleStatus.PENDING,
                    observations = "Test sale"
                )

                val saved = adapter.save(sale)
                saved.id shouldBe sale.id
                saved.storeId shouldBe sale.storeId
                saved.total shouldBe sale.total
                saved.status shouldBe SaleStatus.PENDING

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.total shouldBe BigDecimal("150.00")
            }

            it("findById returns null when sale does not exist") {
                val id = UUID.randomUUID()
                val found = adapter.findById(id)
                found shouldBe null
            }

            it("findAll returns all sales") {
                val storeId = UUID.randomUUID()
                val sale1 = Sale(
                    id = UUID.randomUUID(), storeId = storeId, userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING, observations = null
                )
                val sale2 = Sale(
                    id = UUID.randomUUID(), storeId = storeId, userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("200.00"), status = SaleStatus.COMPLETED, observations = null
                )

                adapter.save(sale1)
                adapter.save(sale2)

                val all = adapter.findAll()
                all.any { it.id == sale1.id } shouldBe true
                all.any { it.id == sale2.id } shouldBe true
            }

            it("findByStoreId returns sales for a specific store") {
                val storeId = UUID.randomUUID()
                val otherStoreId = UUID.randomUUID()
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = storeId, userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("300.00"), status = SaleStatus.PENDING, observations = null
                )
                val otherSale = Sale(
                    id = UUID.randomUUID(), storeId = otherStoreId, userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("400.00"), status = SaleStatus.PENDING, observations = null
                )

                adapter.save(sale)
                adapter.save(otherSale)

                val storeSales = adapter.findByStoreId(storeId)
                storeSales.size shouldBe 1
                storeSales[0].id shouldBe sale.id
            }

            it("findByUserId returns sales for a specific user") {
                val userId = UUID.randomUUID()
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = userId,
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("500.00"), status = SaleStatus.PENDING, observations = null
                )

                adapter.save(sale)
                val userSales = adapter.findByUserId(userId)
                userSales.size shouldBe 1
                userSales[0].id shouldBe sale.id
            }

            it("findByStatus returns sales filtered by status") {
                val pendingSale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING, observations = null
                )
                val completedSale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("200.00"), status = SaleStatus.COMPLETED, observations = null
                )

                adapter.save(pendingSale)
                adapter.save(completedSale)

                val pendingSales = adapter.findByStatus(SaleStatus.PENDING)
                pendingSales.any { it.id == pendingSale.id } shouldBe true
                pendingSales.any { it.id == completedSale.id } shouldBe false
            }

            it("update sale by saving with same id") {
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING, observations = null
                )
                val saved = adapter.save(sale)

                val updated = saved.copy(
                    total = BigDecimal("250.00"),
                    status = SaleStatus.COMPLETED,
                    observations = "Updated"
                )
                adapter.save(updated)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.total shouldBe BigDecimal("250.00")
                found?.status shouldBe SaleStatus.COMPLETED
                found?.observations shouldBe "Updated"
            }

            it("save with null commercialUserId and observations") {
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = null,
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("0.00"), status = SaleStatus.PENDING, observations = null
                )
                val saved = adapter.save(sale)
                saved.userId shouldBe null
                saved.observations shouldBe null
            }
        }
    }
}
