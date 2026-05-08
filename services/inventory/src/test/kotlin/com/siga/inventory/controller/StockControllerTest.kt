package com.siga.inventory.controller

import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.StockRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import java.util.UUID

class StockControllerTest : DescribeSpec({

    val stockRepository = mockk<StockRepository>()
    val controller = StockController(stockRepository)

    val productId = UUID.randomUUID()
    val storeId = UUID.randomUUID()

    describe("StockController") {

        describe("getStockByProduct") {

            it("given existing stock when getting by product then should return 200 OK with stock list") {
                val stocks = listOf(
                    Stock(productId = productId, storeId = storeId, quantity = 10)
                )
                every { stockRepository.findByProductId(productId) } returns stocks

                val response = controller.getStockByProduct(productId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
                response.body?.first()?.quantity shouldBe 10
            }

            it("given no stock when getting by product then should return 200 OK with empty list") {
                every { stockRepository.findByProductId(productId) } returns emptyList()

                val response = controller.getStockByProduct(productId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.isEmpty() shouldBe true
            }
        }

        describe("getStockByStore") {

            it("given existing stock when getting by store then should return 200 OK") {
                every { stockRepository.findByStoreId(storeId) } returns listOf(
                    Stock(productId = productId, storeId = storeId, quantity = 5)
                )

                val response = controller.getStockByStore(storeId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.size shouldBe 1
            }
        }

        describe("getStockByProductAndStore") {

            it("given existing stock when getting by product and store then should return 200 OK") {
                val stock = Stock(productId = productId, storeId = storeId, quantity = 10)
                every { stockRepository.findByProductIdAndStoreId(productId, storeId) } returns stock

                val response = controller.getStockByProductAndStore(productId, storeId)

                response.statusCode shouldBe HttpStatus.OK
                response.body?.quantity shouldBe 10
            }

            it("given no stock when getting by product and store then should return 404 Not Found") {
                every { stockRepository.findByProductIdAndStoreId(productId, storeId) } returns null

                val response = controller.getStockByProductAndStore(productId, storeId)

                response.statusCode shouldBe HttpStatus.NOT_FOUND
            }
        }
    }
})
