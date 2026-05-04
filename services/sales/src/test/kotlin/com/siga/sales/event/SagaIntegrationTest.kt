package com.siga.sales.event

import com.siga.sales.entity.Sale
import com.siga.sales.entity.SaleStatus
import com.siga.sales.repository.SaleRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import com.siga.sales.event.StockEventConsumer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest(properties = ["spring.kafka.bootstrap-servers=\${spring.embedded.kafka.brokers:localhost:9092}"])
@ActiveProfiles("test")
class SagaIntegrationTest : DescribeSpec() {

    @Autowired
    private lateinit var saleRepository: SaleRepository

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    @Autowired
    private lateinit var stockEventConsumer: StockEventConsumer

    init {
        extension(SpringExtension())

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
            
            // Call consumer directly instead of waiting for Kafka
            stockEventConsumer.consume(event)

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
            
            stockEventConsumer.consume(event)

            val finalSale = saleRepository.findById(saleId).get()
            finalSale.status shouldBe SaleStatus.CANCELLED
        }
    }
}
}
