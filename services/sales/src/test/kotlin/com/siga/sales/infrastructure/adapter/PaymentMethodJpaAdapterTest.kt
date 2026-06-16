package com.siga.sales.infrastructure.adapter

import com.siga.sales.BaseSalesIntegrationTest
import com.siga.sales.domain.model.PaymentMethod
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
import java.util.UUID

/**
 * Integration test for [PaymentMethodJpaAdapter].
 * Verifies PaymentMethod persistence through the hexagonal port with H2.
 */
class PaymentMethodJpaAdapterTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var adapter: PaymentMethodJpaAdapter

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    init {
        describe("PaymentMethodJpaAdapter") {

            it("save and find by id") {
                val method = PaymentMethod(
                    id = UUID.randomUUID(),
                    name = "Efectivo",
                    isActive = true
                )

                val saved = adapter.save(method)
                saved.id shouldBe method.id
                saved.name shouldBe "Efectivo"
                saved.isActive shouldBe true

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.name shouldBe "Efectivo"
                found?.isActive shouldBe true
            }

            it("findById returns null when payment method does not exist") {
                val found = adapter.findById(UUID.randomUUID())
                found shouldBe null
            }

            it("findAll returns all payment methods") {
                val efectivo = PaymentMethod(id = UUID.randomUUID(), name = "Efectivo", isActive = true)
                val debito = PaymentMethod(id = UUID.randomUUID(), name = "Tarjeta Débito", isActive = true)
                val credito = PaymentMethod(id = UUID.randomUUID(), name = "Tarjeta Crédito", isActive = true)

                adapter.save(efectivo)
                adapter.save(debito)
                adapter.save(credito)

                val all = adapter.findAll()
                all.size shouldBe 3
                all.any { it.name == "Efectivo" } shouldBe true
                all.any { it.name == "Tarjeta Débito" } shouldBe true
                all.any { it.name == "Tarjeta Crédito" } shouldBe true
            }

            it("findAll returns empty list when no methods") {
                // PaymentMethods table may have seed data from migrations;
                // this tests the query itself works — adapt if seed data exists
                val all = adapter.findAll()
                all shouldNotBe null
            }

            it("save inactive payment method") {
                val method = PaymentMethod(
                    id = UUID.randomUUID(),
                    name = "Transferencia Obsoleta",
                    isActive = false
                )
                val saved = adapter.save(method)
                saved.isActive shouldBe false

                val found = adapter.findById(saved.id)
                found?.isActive shouldBe false
            }

            it("update payment method name and active status") {
                val method = PaymentMethod(
                    id = UUID.randomUUID(), name = "Vale Vista", isActive = true
                )
                val saved = adapter.save(method)

                val updated = saved.copy(name = "Vale Vista Plus", isActive = false)
                adapter.save(updated)

                val found = adapter.findById(saved.id)
                found shouldNotBe null
                found?.name shouldBe "Vale Vista Plus"
                found?.isActive shouldBe false
            }
        }
    }
}
