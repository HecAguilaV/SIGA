package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.PaymentMethod
import com.siga.sales.entity.PaymentMethodEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.UUID

/**
 * Tests for [PaymentMethodMapper].
 */
class PaymentMethodMapperTest : DescribeSpec({

    val mapper = PaymentMethodMapper()
    val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    describe("toDomain") {

        it("maps all fields from entity with non-null id") {
            val entity = PaymentMethodEntity(
                id = UUID.randomUUID(),
                name = "Efectivo",
                isActive = true
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldBe entity.id
            domain.name shouldBe "Efectivo"
            domain.isActive shouldBe true
        }

        it("generates random UUID when entity id is null") {
            val entity = PaymentMethodEntity(name = "Tarjeta Débito")

            val domain = mapper.toDomain(entity)

            domain.id shouldNotBe null
            domain.id shouldNotBe ZERO_UUID
        }

        it("maps default isActive when entity defaults") {
            val entity = PaymentMethodEntity(name = "Transferencia")

            val domain = mapper.toDomain(entity)

            domain.name shouldBe "Transferencia"
            domain.isActive shouldBe true
        }
    }

    describe("toEntity") {

        it("maps all fields from domain") {
            val domain = PaymentMethod(
                id = UUID.randomUUID(),
                name = "Tarjeta Crédito",
                isActive = false
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe domain.id
            entity.name shouldBe "Tarjeta Crédito"
            entity.isActive shouldBe false
        }

        it("sets id to null when domain id is ZERO_UUID") {
            val domain = PaymentMethod(
                id = ZERO_UUID,
                name = "Nuevo Método",
                isActive = true
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe null
        }
    }

    describe("roundtrip") {

        it("domain -> entity -> domain preserves all fields") {
            val original = PaymentMethod(
                id = UUID.randomUUID(),
                name = "Efectivo",
                isActive = true
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result shouldBe original
        }
    }
})
