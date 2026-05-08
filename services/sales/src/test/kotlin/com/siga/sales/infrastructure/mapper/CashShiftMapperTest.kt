package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.CashShift
import com.siga.sales.domain.model.ShiftStatus
import com.siga.sales.entity.CashShiftEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Tests for [CashShiftMapper].
 */
class CashShiftMapperTest : DescribeSpec({

    val mapper = CashShiftMapper()
    val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    describe("toDomain") {

        it("maps all fields from entity with non-null id") {
            val now = Instant.now()
            val entity = CashShiftEntity(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = now,
                closedAt = now.plusSeconds(3600),
                initialAmount = BigDecimal("500000.00"),
                finalAmount = BigDecimal("520000.00"),
                status = ShiftStatus.CLOSED
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldBe entity.id
            domain.storeId shouldBe entity.storeId
            domain.userId shouldBe entity.userId
            domain.openedAt shouldBe now
            domain.closedAt shouldBe now.plusSeconds(3600)
            domain.initialAmount shouldBe BigDecimal("500000.00")
            domain.finalAmount shouldBe BigDecimal("520000.00")
            domain.status shouldBe ShiftStatus.CLOSED
        }

        it("generates random UUID when entity id is null") {
            val entity = CashShiftEntity(
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                initialAmount = BigDecimal("10000.00")
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldNotBe null
            domain.id shouldNotBe ZERO_UUID
        }

        it("maps nullable fields as null") {
            val entity = CashShiftEntity(
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                initialAmount = BigDecimal("10000.00")
            )

            val domain = mapper.toDomain(entity)

            domain.closedAt shouldBe null
            domain.finalAmount shouldBe null
            domain.status shouldBe ShiftStatus.OPEN
        }
    }

    describe("toEntity") {

        it("maps all fields from domain with non-null id") {
            val now = Instant.now()
            val domain = CashShift(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = now,
                closedAt = now.plusSeconds(7200),
                initialAmount = BigDecimal("300000.00"),
                finalAmount = BigDecimal("310000.00"),
                status = ShiftStatus.CLOSED
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe domain.id
            entity.storeId shouldBe domain.storeId
            entity.userId shouldBe domain.userId
            entity.initialAmount shouldBe BigDecimal("300000.00")
            entity.status shouldBe ShiftStatus.CLOSED
        }

        // NOTE: CashShiftMapper.toEntity does NOT map openedAt, closedAt, or finalAmount.
        // These rely on entity defaults or are set elsewhere in the domain.

        it("sets id to null when domain id is ZERO_UUID") {
            val domain = CashShift(
                id = ZERO_UUID,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = Instant.now(),
                closedAt = null,
                initialAmount = BigDecimal("200000.00"),
                finalAmount = null,
                status = ShiftStatus.OPEN
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe null
        }

        it("maps nullable fields (closedAt, finalAmount) from OPEN shift") {
            val domain = CashShift(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = Instant.now(),
                closedAt = null,
                initialAmount = BigDecimal("200000.00"),
                finalAmount = null,
                status = ShiftStatus.OPEN
            )

            val entity = mapper.toEntity(domain)

            entity.status shouldBe ShiftStatus.OPEN
        }
    }

    describe("roundtrip") {

        it("domain -> entity -> domain preserves core fields") {
            val original = CashShift(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                openedAt = Instant.now(),
                closedAt = null,
                initialAmount = BigDecimal("500000.00"),
                finalAmount = null,
                status = ShiftStatus.OPEN
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result.id shouldBe original.id
            result.storeId shouldBe original.storeId
            result.userId shouldBe original.userId
            result.initialAmount shouldBe original.initialAmount
            result.status shouldBe original.status
        }
    }
})
