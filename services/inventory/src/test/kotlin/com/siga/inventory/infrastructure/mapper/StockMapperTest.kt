package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Stock
import com.siga.inventory.entity.Stock as EntityStock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class StockMapperTest : DescribeSpec({

    val productId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    val storeId = UUID.fromString("00000000-0000-0000-0000-000000000001")

    val domainStock = Stock(
        productId = productId,
        storeId = storeId,
        quantity = 10
    )

    describe("StockMapper") {

        describe("toDomain") {

            it("given entity with all fields when mapping to domain then should return correct Stock") {
                val entity = EntityStock(
                    productId = productId,
                    storeId = storeId,
                    quantity = 10
                )

                val result = StockMapper.toDomain(entity)

                result.productId shouldBe domainStock.productId
                result.storeId shouldBe domainStock.storeId
                result.quantity shouldBe domainStock.quantity
            }

            it("given entity with zero quantity when mapping to domain then should preserve zero") {
                val entity = EntityStock(
                    productId = productId,
                    storeId = storeId,
                    quantity = 0
                )

                val result = StockMapper.toDomain(entity)

                result.quantity shouldBe 0
            }
        }

        describe("toEntity") {

            it("given domain stock when mapping to entity then should return correct EntityStock") {
                val result = StockMapper.toEntity(domainStock)

                result.productId shouldBe domainStock.productId
                result.storeId shouldBe domainStock.storeId
                result.quantity shouldBe domainStock.quantity
            }

            it("given domain stock when mapping to entity then id should default to null") {
                val result = StockMapper.toEntity(domainStock)

                result.id shouldBe null
            }

            it("given domain stock when mapping to entity then minimumQuantity should default to 0") {
                val result = StockMapper.toEntity(domainStock)

                result.minimumQuantity shouldBe 0
            }
        }

        describe("roundtrip") {

            it("given entity when toDomain then toEntity should preserve core fields") {
                val entity = EntityStock(
                    id = UUID.randomUUID(),
                    productId = productId,
                    storeId = storeId,
                    quantity = 25,
                    minimumQuantity = 5
                )

                val domain = StockMapper.toDomain(entity)
                val backToEntity = StockMapper.toEntity(domain)

                backToEntity.productId shouldBe entity.productId
                backToEntity.storeId shouldBe entity.storeId
                backToEntity.quantity shouldBe entity.quantity
            }
        }
    }
})
