package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.entity.SaleEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Tests for [SaleMapper].
 * Verifies domain ↔ entity conversion and the UUID-zero boundary.
 */
class SaleMapperTest : DescribeSpec({

    val mapper = SaleMapper()
    val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    describe("toDomain") {

        it("maps all fields from entity with non-null id") {
            val id = UUID.randomUUID()
            val now = Instant.now()
            val entity = SaleEntity(
                id = id,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                commercialUserId = 42,
                createdAt = now,
                total = BigDecimal("250.00"),
                status = SaleStatus.PENDING,
                observations = "Some notes"
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldBe id
            domain.storeId shouldBe entity.storeId
            domain.userId shouldBe entity.userId
            domain.commercialUserId shouldBe 42
            domain.createdAt shouldBe now
            domain.total shouldBe BigDecimal("250.00")
            domain.status shouldBe SaleStatus.PENDING
            domain.observations shouldBe "Some notes"
        }

        it("generates random UUID when entity id is null") {
            val entity = SaleEntity(
                storeId = UUID.randomUUID(),
                total = BigDecimal("100.00")
            )
            entity.id shouldBe null

            val domain = mapper.toDomain(entity)

            domain.id shouldNotBe null
            domain.id shouldNotBe ZERO_UUID
        }

        it("maps nullable fields as null") {
            val entity = SaleEntity(
                storeId = UUID.randomUUID(),
                total = BigDecimal("100.00")
            )

            val domain = mapper.toDomain(entity)

            domain.userId shouldBe null
            domain.commercialUserId shouldBe null
            domain.observations shouldBe null
        }
    }

    describe("toEntity") {

        it("maps all fields from domain with normal UUID") {
            val id = UUID.randomUUID()
            val now = Instant.now()
            val domain = Sale(
                id = id,
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                commercialUserId = 42,
                createdAt = now,
                total = BigDecimal("250.00"),
                status = SaleStatus.COMPLETED,
                observations = "Completed sale"
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe id
            entity.storeId shouldBe domain.storeId
            entity.userId shouldBe domain.userId
            entity.commercialUserId shouldBe 42
            entity.total shouldBe BigDecimal("250.00")
            entity.status shouldBe SaleStatus.COMPLETED
            entity.observations shouldBe "Completed sale"
        }

        it("sets id to null when domain id is ZERO_UUID") {
            val domain = Sale(
                id = ZERO_UUID,
                storeId = UUID.randomUUID(),
                userId = null,
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("100.00"),
                status = SaleStatus.PENDING,
                observations = null
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe null
        }

        it("maps nullable fields as null") {
            val domain = Sale(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = null,
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("100.00"),
                status = SaleStatus.PENDING,
                observations = null
            )

            val entity = mapper.toEntity(domain)

            entity.userId shouldBe null
            entity.commercialUserId shouldBe null
            entity.observations shouldBe null
        }
    }

    describe("roundtrip") {

        it("domain -> entity -> domain preserves all fields (normal UUID)") {
            val original = Sale(
                id = UUID.randomUUID(),
                storeId = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                commercialUserId = 7,
                createdAt = Instant.now(),
                total = BigDecimal("999.99"),
                status = SaleStatus.COMPLETED,
                observations = "Roundtrip test"
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result shouldBe original
        }

        it("domain with ZERO_UUID -> entity -> domain generates valid UUID") {
            val original = Sale(
                id = ZERO_UUID,
                storeId = UUID.randomUUID(),
                userId = null,
                commercialUserId = null,
                createdAt = Instant.now(),
                total = BigDecimal("500.00"),
                status = SaleStatus.PENDING,
                observations = null
            )

            val entity = mapper.toEntity(original)
            entity.id shouldBe null  // JPA will generate it

            val result = mapper.toDomain(entity)

            result.id shouldNotBe ZERO_UUID
            result.storeId shouldBe original.storeId
            result.total shouldBe original.total
        }
    }
})
