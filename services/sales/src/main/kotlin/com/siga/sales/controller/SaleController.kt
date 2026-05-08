package com.siga.sales.controller

import com.siga.sales.application.usecase.CreateSaleUseCase
import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleItem
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.domain.port.SaleRepositoryPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage sales.
 * Uses CreateSaleUseCase for business logic.
 */
@RestController
@RequestMapping("/api/v1/sales")
class SaleController(
    private val saleRepositoryPort: SaleRepositoryPort,
    private val createSaleUseCase: CreateSaleUseCase
) {
    @GetMapping
    fun getAllSales(): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleRepositoryPort.findAll())
    }

    @GetMapping("/{id}")
    fun getSaleById(@PathVariable id: UUID): ResponseEntity<Sale> {
        val sale = saleRepositoryPort.findById(id)
        return if (sale != null) {
            ResponseEntity.ok(sale)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/store/{storeId}")
    fun getSalesByStore(@PathVariable storeId: UUID): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleRepositoryPort.findByStoreId(storeId))
    }

    @GetMapping("/user/{userId}")
    fun getSalesByUser(@PathVariable userId: UUID): ResponseEntity<List<Sale>> {
        return ResponseEntity.ok(saleRepositoryPort.findByUserId(userId))
    }

    @GetMapping("/status/{status}")
    fun getSalesByStatus(@PathVariable status: String): ResponseEntity<List<Sale>> {
        return try {
            val saleStatus = SaleStatus.valueOf(status.uppercase())
            ResponseEntity.ok(saleRepositoryPort.findByStatus(saleStatus))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping
    fun createSale(@RequestBody request: CreateSaleRequest): ResponseEntity<Sale> {
        val savedSale = createSaleUseCase.createSale(request.sale, request.items)
        return ResponseEntity.ok(savedSale)
    }

    @PutMapping("/{id}")
    fun updateSale(@PathVariable id: UUID, @RequestBody sale: Sale): ResponseEntity<Sale> {
        val existing = saleRepositoryPort.findById(id)
        return if (existing != null) {
            val updatedSale = existing.copy(
                total = sale.total,
                status = sale.status,
                observations = sale.observations
            )
            ResponseEntity.ok(saleRepositoryPort.save(updatedSale))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
