package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.SaleItem
import com.siga.sales.entity.SaleItemEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.util.UUID

/**
 * Tests for [SaleItemMapper].
 */
class SaleItemMapperTest : DescribeSpec({

    val mapper = SaleItemMapper()
    val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    describe("toDomain") {

        it("maps all fields from entity with non-null id") {
            val entity = SaleItemEntity(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                productId = UUID.randomUUID(),
                quantity = 3,
                unitPrice = BigDecimal("100.00"),
                subtotal = BigDecimal("300.00")
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldBe entity.id
            domain.saleId shouldBe entity.saleId
            domain.productId shouldBe entity.productId
            domain.quantity shouldBe 3
            domain.unitPrice shouldBe BigDecimal("100.00")
            domain.subtotal shouldBe BigDecimal("300.00")
        }

        it("generates random UUID when entity id is null") {
            val entity = SaleItemEntity(
                saleId = UUID.randomUUID(),
                productId = UUID.randomUUID(),
                quantity = 1,
                unitPrice = BigDecimal("50.00"),
                subtotal = BigDecimal("50.00")
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldNotBe null
            domain.id shouldNotBe ZERO_UUID
        }
    }

    describe("toEntity") {

        it("maps all fields from domain") {
            val domain = SaleItem(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                productId = UUID.randomUUID(),
                quantity = 2,
                unitPrice = BigDecimal("75.00"),
                subtotal = BigDecimal("150.00")
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe domain.id
            entity.saleId shouldBe domain.saleId
            entity.productId shouldBe domain.productId
            entity.quantity shouldBe 2
            entity.unitPrice shouldBe BigDecimal("75.00")
            entity.subtotal shouldBe BigDecimal("150.00")
        }

        it("sets id to null when domain id is ZERO_UUID") {
            val domain = SaleItem(
                id = ZERO_UUID,
                saleId = UUID.randomUUID(),
                productId = UUID.randomUUID(),
                quantity = 1,
                unitPrice = BigDecimal("10.00"),
                subtotal = BigDecimal("10.00")
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe null
        }
    }

    describe("roundtrip") {

        it("domain -> entity -> domain preserves all fields") {
            val original = SaleItem(
                id = UUID.randomUUID(),
                saleId = UUID.randomUUID(),
                productId = UUID.randomUUID(),
                quantity = 5,
                unitPrice = BigDecimal("200.00"),
                subtotal = BigDecimal("1000.00")
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result shouldBe original
        }
    }
})
