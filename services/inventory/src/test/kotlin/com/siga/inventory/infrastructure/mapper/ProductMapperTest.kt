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
        updatedAt = now,
        sku = null,
        unitType = null
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
                result.sku shouldBe domainProduct.sku
                result.unitType shouldBe domainProduct.unitType
            }

            it("given entity with sku and unitType when mapping to domain then should map correctly") {
                val entity = EntityProduct(
                    id = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                    name = "Sku Test",
                    unitPrice = BigDecimal("10.00")
                ).apply {
                    sku = "PRD-001"
                    unitType = "UNIDAD"
                    createdAt = now
                    updatedAt = now
                }

                val result = ProductMapper.toDomain(entity)

                result.sku shouldBe "PRD-001"
                result.unitType shouldBe "UNIDAD"
            }

            it("given entity with null sku and unitType when mapping to domain then should return null") {
                val entity = EntityProduct(
                    id = UUID.randomUUID(),
                    name = "No Sku",
                    unitPrice = BigDecimal("10.00")
                ).apply {
                    createdAt = now
                    updatedAt = now
                }

                val result = ProductMapper.toDomain(entity)

                result.sku shouldBe null
                result.unitType shouldBe null
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
                result.sku shouldBe domainProduct.sku
                result.unitType shouldBe domainProduct.unitType
            }

            it("given domain product with sku and unitType when mapping to entity then should map correctly") {
                val domainWithSku = domainProduct.copy(sku = "PRD-001", unitType = "UNIDAD")

                val result = ProductMapper.toEntity(domainWithSku)

                result.sku shouldBe "PRD-001"
                result.unitType shouldBe "UNIDAD"
            }

            it("given domain product with null sku and unitType when mapping to entity then should map null") {
                val domainNullSku = domainProduct.copy(sku = null, unitType = null)

                val result = ProductMapper.toEntity(domainNullSku)

                result.sku shouldBe null
                result.unitType shouldBe null
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
