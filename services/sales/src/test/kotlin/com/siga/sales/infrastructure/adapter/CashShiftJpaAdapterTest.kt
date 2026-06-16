package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.model.ShiftStatus
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
 * Integration test for [CashShiftJpaAdapter].
 * Verifies CashShift persistence through the hexagonal port with H2.
 */
class CashShiftJpaAdapterTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var adapter: CashShiftJpaAdapter

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    init {
        describe("CashShiftJpaAdapter") {

            it("save and find by id") {
                val shift = CashShift(
                    id = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    openedAt = Instant.now(),
                    closedAt = null,
                    initialAmount = BigDecimal("500000.00"),
                    finalAmount = null,
                    status = ShiftStatus.OPEN
                )

                val saved = adapter.save(shift)
                saved.id shouldBe shift.id
                saved.storeId shouldBe shift.storeId
                saved.initialAmount shouldBe BigDecimal("500000.00")
                saved.status shouldBe ShiftStatus.OPEN
                saved.closedAt shouldBe null
                saved.finalAmount shouldBe null

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.status shouldBe ShiftStatus.OPEN
                found?.initialAmount shouldBe BigDecimal("500000.00")
            }

            it("findById returns null when shift does not exist") {
                val found = adapter.findById(UUID.randomUUID())
                found shouldBe null
            }

            it("findAll returns all shifts") {
                val shift1 = CashShift(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    openedAt = Instant.now(), closedAt = null,
                    initialAmount = BigDecimal("100000.00"), finalAmount = null, status = ShiftStatus.OPEN
                )
                val shift2 = CashShift(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    openedAt = Instant.now(), closedAt = Instant.now(),
                    initialAmount = BigDecimal("200000.00"), finalAmount = BigDecimal("250000.00"),
                    status = ShiftStatus.CLOSED
                )

                adapter.save(shift1)
                adapter.save(shift2)

                val all = adapter.findAll()
                all.size shouldBe 2
                all.any { it.id == shift1.id } shouldBe true
                all.any { it.id == shift2.id } shouldBe true
            }

            it("findByStoreId returns shifts for a store") {
                val storeId = UUID.randomUUID()
                val shift = CashShift(
                    id = UUID.randomUUID(), storeId = storeId, userId = UUID.randomUUID(),
                    openedAt = Instant.now(), closedAt = null,
                    initialAmount = BigDecimal("300000.00"), finalAmount = null, status = ShiftStatus.OPEN
                )
                adapter.save(shift)

                val storeShifts = adapter.findByStoreId(storeId)
                storeShifts.size shouldBe 1
                storeShifts[0].id shouldBe shift.id
            }

            it("findByUserId returns open shift for a user") {
                val userId = UUID.randomUUID()
                val shift = CashShift(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = userId,
                    openedAt = Instant.now(), closedAt = null,
                    initialAmount = BigDecimal("400000.00"), finalAmount = null, status = ShiftStatus.OPEN
                )
                adapter.save(shift)

                val found = adapter.findByUserId(userId)
                found shouldNotBe null
                found?.id shouldBe shift.id
                found?.status shouldBe ShiftStatus.OPEN
            }

            it("findByUserId returns null when user has no open shift") {
                val found = adapter.findByUserId(UUID.randomUUID())
                found shouldBe null
            }

            it("update shift — close an open shift") {
                val shift = CashShift(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    openedAt = Instant.now(), closedAt = null,
                    initialAmount = BigDecimal("500000.00"), finalAmount = null, status = ShiftStatus.OPEN
                )
                val saved = adapter.save(shift)

                val closed = saved.copy(
                    closedAt = Instant.now(),
                    finalAmount = BigDecimal("550000.00"),
                    status = ShiftStatus.CLOSED
                )
                adapter.save(closed)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.status shouldBe ShiftStatus.CLOSED
                found?.finalAmount shouldBe BigDecimal("550000.00")
                found?.closedAt shouldNotBe null
            }
        }
    }
}
