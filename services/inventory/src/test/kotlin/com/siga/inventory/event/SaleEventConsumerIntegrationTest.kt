package com.siga.inventory.event

import com.siga.inventory.entity.Product
import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.MovementRepository
import com.siga.inventory.repository.ProductRepository
import com.siga.inventory.repository.StockRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.*

@SpringBootTest(properties = ["spring.kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}"])
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = ["sale-events", "stock-events"])
class SaleEventConsumerIntegrationTest : DescribeSpec() {

    @org.springframework.beans.factory.annotation.Autowired
    private lateinit var productRepository: ProductRepository

    @org.springframework.beans.factory.annotation.Autowired
    private lateinit var stockRepository: StockRepository

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    @org.springframework.beans.factory.annotation.Autowired
    private lateinit var movementRepository: MovementRepository

    @org.springframework.beans.factory.annotation.Autowired
    private lateinit var saleEventConsumer: SaleEventConsumer

    init {
        extension(io.kotest.extensions.spring.SpringExtension())

        describe("SaleEventConsumer Integration (SAGA Step 2)") {

        it("should reserve stock and record movement on SALE_INITIATED") {
            val tenantId = UUID.randomUUID()
            val product = productRepository.save(
                Product(
                    name = "Test Product", 
                    barcode = "TEST-001", 
                    unitPrice = BigDecimal("10.00"), 
                    categoryId = UUID.randomUUID()
                )
            )
            val stock = stockRepository.save(
                Stock(productId = product.id!!, storeId = tenantId, quantity = 10)
            )

            val event = SaleEvent(
                eventType = SaleEventType.SALE_INITIATED,
                saleId = UUID.randomUUID(),
                tenantId = tenantId,
                items = listOf(SaleItemEvent(productId = product.id!!, quantity = 4))
            )

            saleEventConsumer.consume(event)

            val updatedStock = stockRepository.findById(stock.id!!).get()
            updatedStock.quantity shouldBe 6
            
            val movements = movementRepository.findBySaleId(event.saleId)
            movements.shouldNotBeEmpty()
            movements[0].quantity shouldBe 4
        }
    }
}
}
