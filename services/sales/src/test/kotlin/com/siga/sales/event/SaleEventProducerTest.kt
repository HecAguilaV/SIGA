package com.siga.sales.event

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Unit tests for [SaleEventProducer].
 * Verifies that sale events are published to the correct Kafka topic.
 */
class SaleEventProducerTest : DescribeSpec({

    val kafkaTemplate = mockk<KafkaTemplate<String, Any>>()
    val producer = SaleEventProducer(kafkaTemplate)

    beforeEach {
        clearAllMocks()
    }

    describe("SaleEventProducer") {

        it("given_valid_sale_event_when_published_then_send_to_correct_topic") {
            val event = SaleEvent(
                eventType = SaleEventType.SALE_INITIATED,
                saleId = UUID.randomUUID(),
                tenantId = UUID.randomUUID(),
                items = listOf(
                    SaleItemEvent(productId = UUID.randomUUID(), quantity = 2)
                )
            )

            every { kafkaTemplate.send(any(), any(), any()) } returns CompletableFuture()

            producer.publish(event)

            verify {
                kafkaTemplate.send(
                    SaleEventProducer.TOPIC,
                    event.saleId.toString(),
                    event
                )
            }
        }

        it("given_cancellation_event_when_published_then_use_sale_id_as_key") {
            val saleId = UUID.randomUUID()
            val event = SaleEvent(
                eventType = SaleEventType.SALE_CANCELLED,
                saleId = saleId,
                tenantId = UUID.randomUUID()
            )

            every { kafkaTemplate.send(any(), any(), any()) } returns CompletableFuture()

            producer.publish(event)

            verify {
                kafkaTemplate.send(
                    SaleEventProducer.TOPIC,
                    saleId.toString(),
                    event
                )
            }
        }
    }
})
