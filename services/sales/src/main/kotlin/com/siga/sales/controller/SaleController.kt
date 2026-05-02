package com.siga.sales.controller

import com.siga.sales.entity.Sale
import com.siga.sales.entity.SaleStatus
import com.siga.sales.repository.SaleRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage sales.
 */
@RestController
@RequestMapping("/api/v1/sales")
class SaleController(
    private val saleRepository: SaleRepository
) {
    @GetMapping
    fun getAllSales(): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getSaleById(@PathVariable id: UUID): ResponseEntity<Sale> {
        val sale = saleRepository.findById(id)
        return if (sale.isPresent) {
            ResponseEntity.ok(sale.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/store/{storeId}")
    fun getSalesByStore(@PathVariable storeId: UUID): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleRepository.findByStoreId(storeId))
    }

    @GetMapping("/user/{userId}")
    fun getSalesByUser(@PathVariable userId: UUID): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleRepository.findByUserId(userId))
    }

    @GetMapping("/status/{status}")
    fun getSalesByStatus(@PathVariable status: String): ResponseEntity<List<Sale>> {
        return try {
            val saleStatus = SaleStatus.valueOf(status.uppercase())
            ResponseEntity.ok(saleRepository.findByStatus(saleStatus))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping
    fun createSale(@RequestBody sale: Sale): ResponseEntity<Sale> {
        return ResponseEntity.ok(saleRepository.save(sale))
    }

    @PutMapping("/{id}")
    fun updateSale(@PathVariable id: UUID, @RequestBody sale: Sale): ResponseEntity<Sale> {
        return if (saleRepository.existsById(id)) {
            sale.id = id
            ResponseEntity.ok(saleRepository.save(sale))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
