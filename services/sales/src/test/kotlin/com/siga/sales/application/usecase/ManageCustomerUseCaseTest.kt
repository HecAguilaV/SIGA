package com.siga.sales.application.usecase

import com.siga.sales.domain.model.Customer
import com.siga.sales.domain.port.CustomerRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.time.Instant
import java.util.UUID

/**
 * Unit tests for [ManageCustomerUseCase].
 */
class ManageCustomerUseCaseTest : DescribeSpec({

    val customerRepositoryPort = mockk<CustomerRepositoryPort>()
    val useCase = ManageCustomerUseCase(customerRepositoryPort)

    beforeEach {
        clearAllMocks()
    }

    describe("ManageCustomerUseCase") {

        it("given_valid_customer_when_create_then_save_and_return") {
            val customer = Customer(
                id = UUID.randomUUID(),
                taxId = "76.123.456-7",
                name = "Cliente Ejemplo Ltda.",
                email = "contacto@ejemplo.cl",
                phone = "+56 9 1234 5678",
                address = "Av. Providencia 1234, Santiago",
                createdAt = Instant.now()
            )

            every { customerRepositoryPort.save(customer) } returns customer

            val result = useCase.createCustomer(customer)

            verify(exactly = 1) { customerRepositoryPort.save(customer) }
            result shouldBe customer
        }

        it("given_existing_id_when_find_by_id_then_return_customer") {
            val id = UUID.randomUUID()
            val customer = Customer(
                id = id,
                taxId = null,
                name = "Cliente Anónimo",
                email = null,
                phone = null,
                address = null,
                createdAt = Instant.now()
            )

            every { customerRepositoryPort.findById(id) } returns customer

            val result = useCase.findCustomerById(id)

            result shouldNotBe null
            result?.id shouldBe id
            result?.name shouldBe "Cliente Anónimo"
        }

        it("given_missing_id_when_find_by_id_then_return_null") {
            val id = UUID.randomUUID()

            every { customerRepositoryPort.findById(id) } returns null

            val result = useCase.findCustomerById(id)

            result shouldBe null
        }

        it("given_existing_taxId_when_find_by_taxId_then_return_customer") {
            val taxId = "76.123.456-7"
            val customer = Customer(
                id = UUID.randomUUID(),
                taxId = taxId,
                name = "Cliente Ejemplo Ltda.",
                email = null,
                phone = null,
                address = null,
                createdAt = Instant.now()
            )

            every { customerRepositoryPort.findByTaxId(taxId) } returns customer

            val result = useCase.findCustomerByTaxId(taxId)

            result shouldNotBe null
            result?.taxId shouldBe taxId
        }

        it("given_missing_taxId_when_find_by_taxId_then_return_null") {
            val taxId = "99.999.999-9"

            every { customerRepositoryPort.findByTaxId(taxId) } returns null

            val result = useCase.findCustomerByTaxId(taxId)

            result shouldBe null
        }
    }
})
