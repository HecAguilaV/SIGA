package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Alert
import com.siga.inventory.domain.model.AlertType
import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.port.AlertRepositoryPort
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID
import kotlin.math.abs

/**
 * Use Case: Registers a physical count for a product at a store,
 * calculates discrepancy, adjusts stock, and optionally creates an alert.
 *
 * Flow:
 * 1. Validate physical count >= 0
 * 2. Load current system stock
 * 3. Calculate discrepancy (physical - system)
 * 4. If discrepancy != 0: adjust stock + create RECONCILIATION movement
 * 5. If abs(discrepancy) > 10% of system stock: create alert
 */
@Service
class ReconcileStockUseCase(
    private val stockPort: StockRepositoryPort,
    private val movementPort: MovementRepositoryPort,
    private val alertPort: AlertRepositoryPort
) {
    /**
     * Executes a stock reconciliation.
     *
     * @param request The reconciliation request.
     * @return [ReconcileResponse] with details of the reconciliation.
     * @throws IllegalArgumentException if physical count is negative or product not found.
     */
    fun execute(request: ReconcileRequest): ReconcileResponse {
        if (request.physicalCount < 0) {
            throw IllegalArgumentException("Physical count must be >= 0")
        }

        val currentStock = stockPort.findByProductIdAndStoreId(request.productId, request.storeId)
            ?: throw IllegalArgumentException("Product not found at store")

        val previousStock = currentStock.quantity
        val discrepancy = request.physicalCount - previousStock
        val now = Instant.now()

        var alertCreated = false

        if (discrepancy != 0) {
            // Adjust stock
            stockPort.save(currentStock.copy(quantity = request.physicalCount))

            // Record reconciliation movement
            movementPort.save(
                Movement(
                    id = UUID.randomUUID(),
                    productId = request.productId,
                    storeId = request.storeId,
                    type = MovementType.RECONCILIATION,
                    quantity = abs(discrepancy),
                    previousQuantity = previousStock,
                    newQuantity = request.physicalCount,
                    userId = request.userId,
                    saleId = null,
                    observations = "Reconciliation: ${request.motive}, discrepancy=$discrepancy",
                    correlationId = null,
                    destinationStoreId = null
                )
            )

            // Check if alert threshold is exceeded (>10% discrepancy)
            val discrepancyPercent = if (previousStock > 0) {
                abs(discrepancy).toDouble() / previousStock.toDouble()
            } else {
                // If system stock was 0, any positive discrepancy is >10%
                if (discrepancy > 0) 1.0 else 0.0
            }

            if (discrepancyPercent > 0.1) {
                alertPort.save(
                    Alert(
                        id = null,
                        type = AlertType.SUSPICIOUS_MOVEMENT,
                        productId = request.productId,
                        storeId = request.storeId,
                        message = "Stock discrepancy of $discrepancy units (${(discrepancyPercent * 100).toInt()}%) " +
                            "detected during reconciliation. Motive: ${request.motive}",
                        isRead = false,
                        createdAt = now
                    )
                )
                alertCreated = true
            }
        }

        return ReconcileResponse(
            reconciliationId = UUID.randomUUID(),
            productId = request.productId,
            storeId = request.storeId,
            previousStock = previousStock,
            newStock = request.physicalCount,
            discrepancy = discrepancy,
            motive = request.motive,
            reconciledBy = request.userId,
            reconciledAt = now,
            alertCreated = alertCreated
        )
    }
}

/**
 * Request DTO for stock reconciliation.
 */
data class ReconcileRequest(
    val productId: UUID,
    val storeId: UUID,
    val physicalCount: Int,
    val motive: String,
    val userId: UUID
)

/**
 * Response DTO for stock reconciliation.
 */
data class ReconcileResponse(
    val reconciliationId: UUID,
    val productId: UUID,
    val storeId: UUID,
    val previousStock: Int,
    val newStock: Int,
    val discrepancy: Int,
    val motive: String,
    val reconciledBy: UUID?,
    val reconciledAt: Instant,
    val alertCreated: Boolean
)
