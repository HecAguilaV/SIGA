package com.siga.sales.infrastructure.adapter

import com.siga.sales.BaseSalesIntegrationTest
import com.siga.sales.domain.model.Customer
import com.siga.sales.event.SaleEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * Integration test for [CustomerJpaAdapter].
 * Verifies Customer persistence through the hexagonal port with H2.
 */
class CustomerJpaAdapterTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var adapter: CustomerJpaAdapter

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    init {
        describe("CustomerJpaAdapter") {

            it("save and find by id") {
                val customer = Customer(
                    id = UUID.randomUUID(),
                    taxId = "76.123.456-7",
                    name = "Cliente Ejemplo Ltda.",
                    email = "contacto@ejemplo.cl",
                    phone = "+56 9 1234 5678",
                    address = "Av. Providencia 1234, Santiago",
                    createdAt = Instant.now()
                )

                val saved = adapter.save(customer)
                saved.id shouldBe customer.id
                saved.name shouldBe "Cliente Ejemplo Ltda."
                saved.taxId shouldBe "76.123.456-7"

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.name shouldBe "Cliente Ejemplo Ltda."
                found?.email shouldBe "contacto@ejemplo.cl"
            }

            it("findById returns null when customer does not exist") {
                val found = adapter.findById(UUID.randomUUID())
                found shouldBe null
            }

            it("findByTaxId returns customer by tax ID") {
                val customer = Customer(
                    id = UUID.randomUUID(),
                    taxId = "77.888.999-0",
                    name = "Otra Empresa SpA.",
                    email = null,
                    phone = null,
                    address = null,
                    createdAt = Instant.now()
                )
                adapter.save(customer)

                val found = adapter.findByTaxId("77.888.999-0")
                found shouldNotBe null
                found?.id shouldBe customer.id
                found?.name shouldBe "Otra Empresa SpA."
            }

            it("findByTaxId returns null when tax ID does not exist") {
                val found = adapter.findByTaxId("99.999.999-9")
                found shouldBe null
            }

            it("save customer with null optional fields") {
                val customer = Customer(
                    id = UUID.randomUUID(),
                    taxId = null,
                    name = "Cliente Anónimo",
                    email = null,
                    phone = null,
                    address = null,
                    createdAt = Instant.now()
                )
                val saved = adapter.save(customer)
                saved.name shouldBe "Cliente Anónimo"
                saved.taxId shouldBe null
            }

            it("update customer by saving with same id") {
                val customer = Customer(
                    id = UUID.randomUUID(), taxId = "76.000.001-1",
                    name = "Nombre Original", email = "original@test.cl",
                    phone = null, address = null, createdAt = Instant.now()
                )
                val saved = adapter.save(customer)

                val updated = saved.copy(name = "Nombre Actualizado", email = "actualizado@test.cl")
                adapter.save(updated)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.name shouldBe "Nombre Actualizado"
                found?.email shouldBe "actualizado@test.cl"
            }
        }
    }
}
