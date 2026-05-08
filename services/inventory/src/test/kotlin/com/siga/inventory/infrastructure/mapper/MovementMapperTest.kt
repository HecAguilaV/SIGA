package com.siga.inventory.infrastructure.mapper

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType as DomainMovementType
import com.siga.inventory.entity.Movement as EntityMovement
import com.siga.inventory.entity.MovementType as EntityMovementType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class MovementMapperTest : DescribeSpec({

    val movementId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    val productId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    val storeId = UUID.fromString("00000000-0000-0000-0000-000000000002")
    val userId = UUID.fromString("00000000-0000-0000-0000-000000000003")
    val saleId = UUID.fromString("00000000-0000-0000-0000-000000000004")

    val domainMovement = Movement(
        id = movementId,
        productId = productId,
        storeId = storeId,
        type = DomainMovementType.SALE,
        quantity = 5,
        previousQuantity = 10,
        newQuantity = 5,
        userId = userId,
        saleId = saleId,
        observations = "SALE: stock reserved"
    )

    describe("MovementMapper") {

        describe("toDomain") {

            it("given entity with all fields when mapping to domain then should return correct Movement") {
                val entity = EntityMovement(
                    id = movementId,
                    productId = productId,
                    storeId = storeId,
                    type = EntityMovementType.SALE,
                    quantity = 5,
                    previousQuantity = 10,
                    newQuantity = 5,
                    userId = userId,
                    saleId = saleId,
                    observations = "SALE: stock reserved"
                )

                val result = MovementMapper.toDomain(entity)

                result.id shouldBe domainMovement.id
                result.productId shouldBe domainMovement.productId
                result.storeId shouldBe domainMovement.storeId
                result.type shouldBe DomainMovementType.SALE
                result.quantity shouldBe domainMovement.quantity
                result.previousQuantity shouldBe domainMovement.previousQuantity
                result.newQuantity shouldBe domainMovement.newQuantity
                result.userId shouldBe domainMovement.userId
                result.saleId shouldBe domainMovement.saleId
                result.observations shouldBe domainMovement.observations
            }

            it("given entity with ADJUSTMENT type when mapping to domain then should map enum correctly") {
                val entity = EntityMovement(
                    id = movementId,
                    productId = productId,
                    storeId = storeId,
                    type = EntityMovementType.ADJUSTMENT,
                    quantity = 3,
                    previousQuantity = 5,
                    newQuantity = 8
                )

                val result = MovementMapper.toDomain(entity)

                result.type shouldBe DomainMovementType.ADJUSTMENT
            }

            it("given entity with nullable fields as null when mapping to domain then should map correctly") {
                val entity = EntityMovement(
                    id = null,
                    productId = productId,
                    storeId = storeId,
                    type = EntityMovementType.IN,
                    quantity = 10,
                    previousQuantity = 0,
                    newQuantity = 10
                )

                val result = MovementMapper.toDomain(entity)

                result.id shouldBe null
                result.userId shouldBe null
                result.saleId shouldBe null
                result.observations shouldBe null
            }
        }

        describe("toEntity") {

            it("given domain movement when mapping to entity then should return correct EntityMovement") {
                val result = MovementMapper.toEntity(domainMovement)

                result.id shouldBe domainMovement.id
                result.productId shouldBe domainMovement.productId
                result.storeId shouldBe domainMovement.storeId
                result.type shouldBe EntityMovementType.SALE
                result.quantity shouldBe domainMovement.quantity
                result.previousQuantity shouldBe domainMovement.previousQuantity
                result.newQuantity shouldBe domainMovement.newQuantity
                result.userId shouldBe domainMovement.userId
                result.saleId shouldBe domainMovement.saleId
                result.observations shouldBe domainMovement.observations
            }

            it("given domain with ADJUSTMENT type when mapping to entity then should map enum correctly") {
                val adjustDomain = domainMovement.copy(type = DomainMovementType.ADJUSTMENT, id = UUID.randomUUID())

                val result = MovementMapper.toEntity(adjustDomain)

                result.type shouldBe EntityMovementType.ADJUSTMENT
            }
        }

        describe("roundtrip") {

            it("given domain movement when toEntity then toDomain should preserve all fields") {
                val entity = MovementMapper.toEntity(domainMovement)
                val roundtrip = MovementMapper.toDomain(entity)

                roundtrip shouldBe domainMovement
            }
        }
    }
})
