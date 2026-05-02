package com.siga.sales.event

import com.siga.sales.entity.Sale
import com.siga.sales.entity.SaleStatus
import com.siga.sales.repository.SaleRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import org.awaitility.Awaitility.await
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = ["sale-events", "stock-events"])
class SagaIntegrationTest(
    private val saleRepository: SaleRepository,
    private val kafkaTemplate: KafkaTemplate<String, StockEvent>
) : DescribeSpec({

    extension(SpringExtension)

    describe("Sales SAGA Choreography Integration") {

        it("given_new_sale_when_stock_reserved_then_sale_completes") {
            // 1. Create a pending sale and GET the generated ID
            val savedSale = saleRepository.save(
                Sale(
                    storeId = UUID.randomUUID(),
                    total = BigDecimal("1500.00"),
                    status = SaleStatus.PENDING
                )
            )
            val saleId = savedSale.id!!

            // 2. Simulate Inventory response (STOCK_RESERVED)
            val event = StockEvent(
                eventType = StockEventType.STOCK_RESERVED,
                saleId = saleId,
                tenantId = savedSale.storeId
            )
            
            kafkaTemplate.send("stock-events", saleId.toString(), event).get()

            // 3. Wait for async consumer
            await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until {
                    val currentSale = saleRepository.findById(saleId).orElse(null)
                    currentSale?.status == SaleStatus.COMPLETED
                }

            val finalSale = saleRepository.findById(saleId).get()
            finalSale.status shouldBe SaleStatus.COMPLETED
        }

        it("given_new_sale_when_stock_failed_then_sale_cancels") {
            val savedSale = saleRepository.save(
                Sale(
                    storeId = UUID.randomUUID(),
                    total = BigDecimal("500.00"),
                    status = SaleStatus.PENDING
                )
            )
            val saleId = savedSale.id!!

            val event = StockEvent(
                eventType = StockEventType.STOCK_FAILED,
                saleId = saleId,
                tenantId = savedSale.storeId,
                reason = "Out of stock"
            )
            
            kafkaTemplate.send("stock-events", saleId.toString(), event).get()

            await()
                .atMost(10, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until {
                    val currentSale = saleRepository.findById(saleId).orElse(null)
                    currentSale?.status == SaleStatus.CANCELLED
                }

            val finalSale = saleRepository.findById(saleId).get()
            finalSale.status shouldBe SaleStatus.CANCELLED
        }
    }
})
