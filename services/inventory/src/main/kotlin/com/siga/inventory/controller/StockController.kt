package com.siga.inventory.controller

import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.StockRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage stock/inventory levels.
 */
@RestController
@RequestMapping("/api/stock")
class StockController(
    private val stockRepository: StockRepository
) {
    @GetMapping
    fun getAllStock(): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockRepository.findAll())
    }

    @GetMapping("/product/{productId}")
    fun getStockByProduct(@PathVariable productId: Int): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockRepository.findByProductId(productId))
    }

    @GetMapping("/store/{storeId}")
    fun getStockByStore(@PathVariable storeId: Int): ResponseEntity<List<Stock>> {
        return ResponseEntity.ok(stockRepository.findByStoreId(storeId))
    }

    @PutMapping("/{id}")
    fun updateStock(@PathVariable id: Int, @RequestBody stock: Stock): ResponseEntity<Stock> {
        return if (stockRepository.existsById(id)) {
            stock.id = id
            ResponseEntity.ok(stockRepository.save(stock))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
