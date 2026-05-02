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

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = ["sale-events", "stock-events"])
class SaleEventConsumerIntegrationTest(
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val movementRepository: MovementRepository,
    private val saleEventConsumer: SaleEventConsumer
) : DescribeSpec({

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
})
