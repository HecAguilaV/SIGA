package com.siga.inventory.controller

import com.siga.inventory.application.usecase.ConsolidatedStockResponse
import com.siga.inventory.application.usecase.ConsolidatedStockUseCase
import com.siga.inventory.application.usecase.ReconcileRequest
import com.siga.inventory.application.usecase.ReconcileStockUseCase
import com.siga.inventory.application.usecase.ReconcileResponse
import com.siga.inventory.application.usecase.TransferMovementHistoryUseCase
import com.siga.inventory.application.usecase.TransferStockUseCase
import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.entity.Stock
import com.siga.inventory.repository.StockRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.UUID

/**
 * Controller to manage product stock.
 *
 * Existing endpoints use StockRepository (JPA) directly.
 * New endpoints (Phase 4) inject use cases for hexagonal architecture.
 */
@RestController
@RequestMapping("/api/v1/inventory/stock")
class StockController(
    private val stockRepository: StockRepository,
    private val consolidatedStockUseCase: ConsolidatedStockUseCase,
    private val reconcileStockUseCase: ReconcileStockUseCase,
    private val transferStockUseCase: TransferStockUseCase,
    private val transferMovementHistoryUseCase: TransferMovementHistoryUseCase
) {
    // --- Existing endpoints (unchanged) ---

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

        // --- New endpoints (Phase 4.1) ---

    /**
     * GET /api/v1/inventory/stock/consolidated
     * Returns consolidated stock view per product across all stores.
     */
    @GetMapping("/consolidated")
    fun getConsolidatedStock(
        @RequestParam(required = false) storeId: UUID?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") size: Int
    ): ResponseEntity<ConsolidatedStockResponse> {
        return ResponseEntity.ok(
            consolidatedStockUseCase.execute(storeId, page, size)
        )
    }

    /**
     * POST /api/v1/inventory/stock/reconciliations
     * Registers a physical count and reconciles stock.
     */
    @PostMapping("/reconciliations")
    fun reconcileStock(@RequestBody request: ReconcileRequest): ResponseEntity<*> {
        return try {
            ResponseEntity.ok(reconcileStockUseCase.execute(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf("error" to "PRODUCT_NOT_FOUND_AT_STORE", "message" to e.message)
            )
        }
    }

    /**
     * POST /api/v1/inventory/stock/transfers
     * Transfers stock between two stores atomically.
     */
    @PostMapping("/transfers")
    fun transferStock(@RequestBody request: TransferRequest): ResponseEntity<*> {
        return try {
            // userId will be extracted from security context once auth is integrated
            val response = transferStockUseCase.execute(
                productId = request.productId,
                originStoreId = request.originStoreId,
                destinationStoreId = request.destinationStoreId,
                quantity = request.quantity,
                userId = null
            )
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf("error" to "INVALID_REQUEST", "message" to e.message)
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                mapOf("error" to "INSUFFICIENT_STOCK", "message" to e.message)
            )
        }
    }

    /**
     * GET /api/v1/inventory/stock/movements
     * Returns filtered movement history.
     */
    @GetMapping("/movements")
    fun getMovements(
        @RequestParam(required = false) storeId: UUID?,
        @RequestParam(required = false) type: MovementType?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: Instant?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) to: Instant?,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<Movement>> {
        return ResponseEntity.ok(
            transferMovementHistoryUseCase.execute(storeId, type, from, to, pageable)
        )
    }
}
