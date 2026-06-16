package com.siga.sales.event

import com.siga.sales.BaseSalesIntegrationTest
import com.siga.sales.KafkaTestContainer
import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [KafkaTestContainer.Initializer::class])
@ActiveProfiles("test")
class StockEventConsumerIntegrationTest : DescribeSpec() {

    init {
        extension(SpringExtension())
    }

    @Autowired
    private lateinit var saleRepositoryPort: SaleRepositoryPort

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    init {
        describe("StockEventConsumer with Testcontainers Kafka broker") {

            it("given_pending_sale_when_stock_reserved_event_on_kafka_then_sale_becomes_completed") {
                // ─── setup: create a PENDING sale ───
                val sale = Sale(
                    id = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    commercialUserId = null,
                    createdAt = Instant.now(),
                    total = BigDecimal("1500.00"),
                    status = SaleStatus.PENDING,
                    observations = "Kafka integration test"
                )
                saleRepositoryPort.save(sale)

                // ─── act: publish StockEvent to real Kafka topic ───
                val stockEvent = StockEvent(
                    eventId = UUID.randomUUID(),
                    eventType = StockEventType.STOCK_RESERVED,
                    saleId = sale.id,
                    tenantId = sale.storeId,
                    reason = null
                )
                kafkaTemplate.send("stock-events", sale.id.toString(), stockEvent)

                // ─── wait for async consumption (poll with timeout) ───
                var updatedSale: Sale? = null
                val deadline = System.currentTimeMillis() + 15_000
                while (System.currentTimeMillis() < deadline) {
                    updatedSale = saleRepositoryPort.findById(sale.id)
                    if (updatedSale?.status == SaleStatus.COMPLETED) break
                    Thread.sleep(500)
                }
                updatedSale?.status shouldBe SaleStatus.COMPLETED
            }

            it("given_pending_sale_when_stock_failed_event_on_kafka_then_sale_becomes_cancelled") {
                // ─── setup ───
                val sale = Sale(
                    id = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    commercialUserId = null,
                    createdAt = Instant.now(),
                    total = BigDecimal("800.00"),
                    status = SaleStatus.PENDING,
                    observations = "Kafka integration test - fail"
                )
                saleRepositoryPort.save(sale)

                // ─── act ───
                val stockEvent = StockEvent(
                    eventId = UUID.randomUUID(),
                    eventType = StockEventType.STOCK_FAILED,
                    saleId = sale.id,
                    tenantId = sale.storeId,
                    reason = "Insufficient stock"
                )
                kafkaTemplate.send("stock-events", sale.id.toString(), stockEvent)

                // ─── verify ───
                var updatedSale: Sale? = null
                val deadline = System.currentTimeMillis() + 15_000
                while (System.currentTimeMillis() < deadline) {
                    updatedSale = saleRepositoryPort.findById(sale.id)
                    if (updatedSale?.status == SaleStatus.CANCELLED) break
                    Thread.sleep(500)
                }
                updatedSale?.status shouldBe SaleStatus.CANCELLED
            }

            it("given_duplicate_event_when_already_processed_then_sale_status_unchanged") {
                // ─── setup ───
                val eventId = UUID.randomUUID()
                val sale = Sale(
                    id = UUID.randomUUID(),
                    storeId = UUID.randomUUID(),
                    userId = UUID.randomUUID(),
                    commercialUserId = null,
                    createdAt = Instant.now(),
                    total = BigDecimal("2000.00"),
                    status = SaleStatus.PENDING,
                    observations = "Kafka dupe test"
                )
                saleRepositoryPort.save(sale)

                // First event — should complete the sale
                val firstEvent = StockEvent(
                    eventId = eventId,
                    eventType = StockEventType.STOCK_RESERVED,
                    saleId = sale.id,
                    tenantId = sale.storeId
                )
                kafkaTemplate.send("stock-events", sale.id.toString(), firstEvent)

                // Wait for first event to process
                var updatedSale: Sale? = null
                val deadline = System.currentTimeMillis() + 15_000
                while (System.currentTimeMillis() < deadline) {
                    updatedSale = saleRepositoryPort.findById(sale.id)
                    if (updatedSale?.status == SaleStatus.COMPLETED) break
                    Thread.sleep(500)
                }
                updatedSale?.status shouldBe SaleStatus.COMPLETED

                // Second event with same eventId — should be skipped (idempotency)
                val secondEvent = StockEvent(
                    eventId = eventId,
                    eventType = StockEventType.STOCK_FAILED,
                    saleId = sale.id,
                    tenantId = sale.storeId,
                    reason = "Should be ignored"
                )
                kafkaTemplate.send("stock-events", sale.id.toString(), secondEvent)

                // Wait for second event to be consumed (or skipped)
                Thread.sleep(3000)

                // Status must still be COMPLETED (not CANCELLED)
                val finalSale = saleRepositoryPort.findById(sale.id)
                finalSale?.status shouldBe SaleStatus.COMPLETED
            }
        }
    }
}
