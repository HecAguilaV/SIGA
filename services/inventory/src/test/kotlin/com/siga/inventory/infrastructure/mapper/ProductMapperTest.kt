package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Product
import com.siga.inventory.entity.Product as EntityProduct
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldNotBe
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class ProductMapperTest : DescribeSpec({

    val now = Instant.now()
    val domainProduct = Product(
        id = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
        name = "Test Product",
        description = "A test product",
        categoryId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        barcode = "TEST-001",
        unitPrice = BigDecimal("99.99"),
        isActive = true,
        commercialUserId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
        createdAt = now,
        updatedAt = now
    )

    val entityProduct = EntityProduct(
        id = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
        name = "Test Product",
        description = "A test product",
        categoryId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        barcode = "TEST-001",
        unitPrice = BigDecimal("99.99"),
        isActive = true,
        commercialUserId = UUID.fromString("00000000-0000-0000-0000-000000000002")
    ).apply {
        createdAt = now
        updatedAt = now
    }

    describe("ProductMapper") {

        describe("toDomain") {

            it("given entity with all fields when mapping to domain then should return correct Product") {
                val result = ProductMapper.toDomain(entityProduct)

                result.id shouldBe domainProduct.id
                result.name shouldBe domainProduct.name
                result.description shouldBe domainProduct.description
                result.categoryId shouldBe domainProduct.categoryId
                result.barcode shouldBe domainProduct.barcode
                result.unitPrice shouldBe domainProduct.unitPrice
                result.isActive shouldBe domainProduct.isActive
                result.commercialUserId shouldBe domainProduct.commercialUserId
                result.createdAt shouldBe domainProduct.createdAt
                result.updatedAt shouldBe domainProduct.updatedAt
            }

            it("given entity with null id when mapping to domain then should throw") {
                val entity = EntityProduct(
                    id = null,
                    name = "No ID",
                    unitPrice = BigDecimal("10.00")
                )

                shouldThrow<IllegalStateException> {
                    ProductMapper.toDomain(entity)
                }
            }

            it("given entity with null timestamps when mapping to domain then should throw") {
                val entity = EntityProduct(
                    id = UUID.randomUUID(),
                    name = "No timestamps",
                    unitPrice = BigDecimal("10.00")
                ).apply {
                    createdAt = null
                    updatedAt = null
                }

                shouldThrow<IllegalStateException> {
                    ProductMapper.toDomain(entity)
                }
            }

            it("given entity with nullable fields as null when mapping to domain then should map correctly") {
                val entity = EntityProduct(
                    id = UUID.randomUUID(),
                    name = "Minimal",
                    unitPrice = BigDecimal("5.00")
                ).apply {
                    createdAt = now
                    updatedAt = now
                }

                val result = ProductMapper.toDomain(entity)

                result.description shouldBe null
                result.categoryId shouldBe null
                result.barcode shouldBe null
                result.commercialUserId shouldBe null
            }
        }

        describe("toEntity") {

            it("given domain product when mapping to entity then should return correct EntityProduct") {
                val result = ProductMapper.toEntity(domainProduct)

                result.id shouldBe domainProduct.id
                result.name shouldBe domainProduct.name
                result.description shouldBe domainProduct.description
                result.categoryId shouldBe domainProduct.categoryId
                result.barcode shouldBe domainProduct.barcode
                result.unitPrice shouldBe domainProduct.unitPrice
                result.isActive shouldBe domainProduct.isActive
                result.commercialUserId shouldBe domainProduct.commercialUserId
            }

            it("given domain product when mapping to entity then createdAt and updatedAt should be mapped") {
                val result = ProductMapper.toEntity(domainProduct)

                result.createdAt shouldBe domainProduct.createdAt
                result.updatedAt shouldBe domainProduct.updatedAt
            }

            it("given domain with null optionals when mapping to entity then should preserve nulls") {
                val minimal = Product(
                    id = UUID.randomUUID(),
                    name = "Minimal",
                    description = null,
                    categoryId = null,
                    barcode = null,
                    unitPrice = BigDecimal("5.00"),
                    isActive = true,
                    commercialUserId = null,
                    createdAt = now,
                    updatedAt = now
                )

                val result = ProductMapper.toEntity(minimal)

                result.description shouldBe null
                result.categoryId shouldBe null
                result.barcode shouldBe null
                result.commercialUserId shouldBe null
            }
        }

        describe("roundtrip") {

            it("given domain product when toEntity then toDomain should preserve all fields") {
                val entity = ProductMapper.toEntity(domainProduct)
                // createdAt and updatedAt are now mapped through toEntity

                val roundtrip = ProductMapper.toDomain(entity)

                roundtrip shouldBe domainProduct
            }
        }
    }
})
