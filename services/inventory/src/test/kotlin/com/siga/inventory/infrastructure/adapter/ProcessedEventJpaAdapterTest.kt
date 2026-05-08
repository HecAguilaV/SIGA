package com.siga.inventory.infrastructure.adapter

import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.UUID

/**
 * Integration test for [ProcessedEventJpaAdapter].
 * Verifies idempotency tracking persistence through the hexagonal port with H2.
 *
 * ProcessedEvent is used by the Kafka consumer to guarantee exactly-once processing
 * by tracking which event IDs have already been handled.
 */
@SpringBootTest
@ActiveProfiles("test")
class ProcessedEventJpaAdapterTest : DescribeSpec() {

    @Autowired
    private lateinit var adapter: ProcessedEventJpaAdapter

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    init {
        extension(SpringExtension())

        describe("ProcessedEventJpaAdapter") {

            it("save and existsById returns true for saved event") {
                val eventId = UUID.randomUUID()
                val eventType = "STOCK_DEDUCTED"

                adapter.save(eventId, eventType)

                val exists = adapter.existsById(eventId)
                exists shouldBe true
            }

            it("existsById returns false for non-existing event") {
                val exists = adapter.existsById(UUID.randomUUID())
                exists shouldBe false
            }

            it("save multiple events with different IDs") {
                val id1 = UUID.randomUUID()
                val id2 = UUID.randomUUID()

                adapter.save(id1, "STOCK_DEDUCTED")
                adapter.save(id2, "ORDER_CREATED")

                adapter.existsById(id1) shouldBe true
                adapter.existsById(id2) shouldBe true
            }

            it("save overwrites existing event with same id") {
                val eventId = UUID.randomUUID()

                adapter.save(eventId, "STOCK_DEDUCTED")
                // Save again with same ID but different type
                adapter.save(eventId, "ORDER_CREATED")

                val exists = adapter.existsById(eventId)
                exists shouldBe true
            }

            it("handles multiple event types") {
                val eventId = UUID.randomUUID()
                val eventType = "INVENTORY_ADJUSTMENT"

                adapter.save(eventId, eventType)
                adapter.existsById(eventId) shouldBe true
            }
        }
    }
}
