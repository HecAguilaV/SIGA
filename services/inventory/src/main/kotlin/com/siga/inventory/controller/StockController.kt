package com.siga.inventory.controller

import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.StockRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage product stock.
 */
@RestController
@RequestMapping("/api/v1/inventory/stock")
class StockController(
    private val stockRepository: StockRepository
) {
    @GetMapping("/product/{productId}")
    fun getStockByProduct(@PathVariable productId: UUID): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockRepository.findByProductId(productId))
    }

    @GetMapping("/store/{storeId}")
    fun getStockByStore(@PathVariable storeId: UUID): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockRepository.findByStoreId(storeId))
    }

    @GetMapping("/product/{productId}/store/{storeId}")
    fun getStockByProductAndStore(
        @PathVariable productId: UUID,
        @PathVariable storeId: UUID
    ): ResponseEntity<Stock> {
        val stock = stockRepository.findByProductIdAndStoreId(productId, storeId)
        return if (stock != null) {
            ResponseEntity.ok(stock)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
