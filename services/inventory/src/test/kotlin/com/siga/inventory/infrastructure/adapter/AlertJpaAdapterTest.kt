package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.domain.model.AlertType
import com.siga.inventory.domain.port.AlertRepositoryPort
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.UUID

/**
 * Integration test for [AlertJpaAdapter].
 * Verifies Alert persistence through the hexagonal port with H2.
 */
@SpringBootTest
@ActiveProfiles("test")
class AlertJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: AlertRepositoryPort

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("AlertJpaAdapter") {

            it("save and find by storeId") {
                val storeId = UUID.randomUUID()
                val alert = com.siga.inventory.domain.model.Alert(
                    id = UUID.randomUUID(),
                    type = AlertType.LOW_STOCK,
                    productId = UUID.randomUUID(),
                    storeId = storeId,
                    message = "Stock bajo para producto X",
                    isRead = false,
                    createdAt = java.time.Instant.now()
                )

                val saved = adapter.save(alert)
                saved.id shouldNotBe null
                saved.type shouldBe AlertType.LOW_STOCK
                saved.message shouldBe "Stock bajo para producto X"
                saved.isRead shouldBe false

                val found = adapter.findByStoreId(storeId)
                found.size shouldBe 1
                found[0].id shouldBe saved.id
            }

            it("findByStoreId returns empty list when no alerts") {
                val alerts = adapter.findByStoreId(UUID.randomUUID())
                alerts shouldBe emptyList()
            }

            it("save and find multiple alerts for the same store") {
                val storeId = UUID.randomUUID()

                val alert1 = com.siga.inventory.domain.model.Alert(
                    id = UUID.randomUUID(), type = AlertType.LOW_STOCK,
                    productId = UUID.randomUUID(), storeId = storeId,
                    message = "Alerta 1", isRead = false,
                    createdAt = java.time.Instant.now()
                )
                val alert2 = com.siga.inventory.domain.model.Alert(
                    id = UUID.randomUUID(), type = AlertType.OUT_OF_STOCK,
                    productId = UUID.randomUUID(), storeId = storeId,
                    message = "Alerta 2", isRead = true,
                    createdAt = java.time.Instant.now()
                )

                adapter.save(alert1)
                adapter.save(alert2)

                val alerts = adapter.findByStoreId(storeId)
                alerts.size shouldBe 2
            }

            it("findByStoreId does not return alerts from other stores") {
                val storeA = UUID.randomUUID()
                val storeB = UUID.randomUUID()

                val alertA = com.siga.inventory.domain.model.Alert(
                    id = UUID.randomUUID(), type = AlertType.HIGH_SALES,
                    productId = null, storeId = storeA,
                    message = "Store A alert", isRead = false,
                    createdAt = java.time.Instant.now()
                )
                adapter.save(alertA)

                val alertsB = adapter.findByStoreId(storeB)
                alertsB shouldBe emptyList()
            }

            it("save alert with null productId") {
                val alert = com.siga.inventory.domain.model.Alert(
                    id = UUID.randomUUID(), type = AlertType.SUSPICIOUS_MOVEMENT,
                    productId = null, storeId = UUID.randomUUID(),
                    message = "Movimiento sospechoso detectado", isRead = false,
                    createdAt = java.time.Instant.now()
                )

                val saved = adapter.save(alert)
                saved.productId shouldBe null
                saved.message shouldBe "Movimiento sospechoso detectado"
            }
        }
    }
}
