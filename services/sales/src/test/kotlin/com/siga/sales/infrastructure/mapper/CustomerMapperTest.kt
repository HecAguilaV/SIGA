package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.Customer
import com.siga.sales.entity.CustomerEntity
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant
import java.util.UUID

/**
 * Tests for [CustomerMapper].
 */
class CustomerMapperTest : DescribeSpec({

    val mapper = CustomerMapper()
    val ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

    describe("toDomain") {

        it("maps all fields from entity with non-null id") {
            val now = Instant.now()
            val entity = CustomerEntity(
                id = UUID.randomUUID(),
                taxId = "76.123.456-7",
                name = "Cliente Ejemplo Ltda.",
                email = "contacto@ejemplo.cl",
                phone = "+56 9 1234 5678",
                address = "Av. Providencia 1234",
                createdAt = now
            )

            val domain = mapper.toDomain(entity)

            domain.id shouldBe entity.id
            domain.taxId shouldBe "76.123.456-7"
            domain.name shouldBe "Cliente Ejemplo Ltda."
            domain.email shouldBe "contacto@ejemplo.cl"
            domain.phone shouldBe "+56 9 1234 5678"
            domain.address shouldBe "Av. Providencia 1234"
            domain.createdAt shouldBe now
        }

        it("generates random UUID when entity id is null") {
            val entity = CustomerEntity(name = "Cliente Anónimo")
            entity.id shouldBe null

            val domain = mapper.toDomain(entity)

            domain.id shouldNotBe null
            domain.id shouldNotBe ZERO_UUID
        }

        it("maps nullable fields as null") {
            val entity = CustomerEntity(name = "Cliente Mínimo")

            val domain = mapper.toDomain(entity)

            domain.taxId shouldBe null
            domain.email shouldBe null
            domain.phone shouldBe null
            domain.address shouldBe null
        }
    }

    describe("toEntity") {

        it("maps all fields from domain") {
            val domain = Customer(
                id = UUID.randomUUID(),
                taxId = "77.888.999-0",
                name = "Persona Natural",
                email = "persona@email.com",
                phone = "+56 2 2345 6789",
                address = "Calle Uno 456",
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe domain.id
            entity.taxId shouldBe domain.taxId
            entity.name shouldBe domain.name
            entity.email shouldBe domain.email
            entity.phone shouldBe domain.phone
            entity.address shouldBe domain.address
        }

        it("sets id to null when domain id is ZERO_UUID") {
            val domain = Customer(
                id = ZERO_UUID,
                taxId = null,
                name = "Nuevo Cliente",
                email = null,
                phone = null,
                address = null,
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(domain)

            entity.id shouldBe null
        }

        it("maps nullable fields as null") {
            val domain = Customer(
                id = UUID.randomUUID(),
                taxId = null,
                name = "Cliente Sin Datos",
                email = null,
                phone = null,
                address = null,
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(domain)

            entity.taxId shouldBe null
            entity.email shouldBe null
            entity.phone shouldBe null
            entity.address shouldBe null
        }
    }

    describe("roundtrip") {

        it("domain -> entity -> domain preserves all fields") {
            val original = Customer(
                id = UUID.randomUUID(),
                taxId = "76.123.456-7",
                name = "Cliente Ejemplo Ltda.",
                email = "contacto@ejemplo.cl",
                phone = "+56 9 1234 5678",
                address = "Av. Providencia 1234",
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result shouldBe original
        }

        it("domain with minimum fields survives roundtrip") {
            val original = Customer(
                id = UUID.randomUUID(),
                taxId = null,
                name = "Cliente Mínimo",
                email = null,
                phone = null,
                address = null,
                createdAt = Instant.now()
            )

            val entity = mapper.toEntity(original)
            val result = mapper.toDomain(entity)

            result.id shouldBe original.id
            result.name shouldBe "Cliente Mínimo"
            result.taxId shouldBe null
            result.email shouldBe null
            result.phone shouldBe null
            result.address shouldBe null
        }
    }
})
