package com.siga.inventory.application.usecase

import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.MovementRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * Use Case: Transfers stock between two stores atomically.
 *
 * WHY @Transactional: Origin debit and destination credit must be all-or-nothing.
 * Both operations happen in the same PostgreSQL instance, so a single database
 * transaction with REQUIRED propagation ensures atomicity. If either leg fails,
 * the entire operation rolls back.
 *
 * Flow:
 * 1. Validate: origin != destination, quantity > 0, origin stock exists and is sufficient
 * 2. Debit origin: reduce stock quantity
 * 3. Credit destination: increase stock quantity (create if not exists)
 * 4. Create OUT movement with correlationId for origin
 * 5. Create IN movement with same correlationId for destination
 */
@Service
class TransferStockUseCase(
    private val stockPort: StockRepositoryPort,
    private val movementPort: MovementRepositoryPort
) {
    /**
     * Executes an atomic stock transfer between two stores.
     *
     * @param productId The product being transferred.
     * @param originStoreId Source store.
     * @param destinationStoreId Target store.
     * @param quantity Number of units to transfer.
     * @param userId User initiating the transfer.
     * @return [TransferResponse] with details of the completed transfer.
     * @throws IllegalArgumentException if validation fails.
     * @throws IllegalStateException if origin stock is insufficient.
     */
    @Transactional
    fun execute(
        productId: UUID,
        originStoreId: UUID,
        destinationStoreId: UUID,
        quantity: Int,
        userId: UUID?
    ): TransferResponse {
        // --- Validation ---
        if (originStoreId == destinationStoreId) {
            throw IllegalArgumentException("Origin and destination must be different")
        }
        if (quantity <= 0) {
            throw IllegalArgumentException("Quantity must be positive")
        }

        val originStock = stockPort.findByProductIdAndStoreId(productId, originStoreId)
            ?: throw IllegalArgumentException("Product not found at origin store")

        if (originStock.quantity < quantity) {
            throw IllegalStateException(
                "Insufficient stock: available=${originStock.quantity}, requested=$quantity"
            )
        }

        val now = Instant.now()
        val correlationId = UUID.randomUUID()

        // --- Debit origin ---
        val originNewQty = originStock.quantity - quantity
        stockPort.save(originStock.copy(quantity = originNewQty))

        // --- Credit destination ---
        val destStock = stockPort.findByProductIdAndStoreId(productId, destinationStoreId)
        val destNewQty = (destStock?.quantity ?: 0) + quantity
        val destinationStock = Stock(
            productId = productId,
            storeId = destinationStoreId,
            quantity = destNewQty,
            lastMovementAt = now
        )
        stockPort.save(destinationStock)

        // --- OUT movement for origin ---
        movementPort.save(
            Movement(
                id = UUID.randomUUID(),
                productId = productId,
                storeId = originStoreId,
                type = MovementType.TRANSFER,
                quantity = quantity,
                previousQuantity = originStock.quantity,
                newQuantity = originNewQty,
                userId = userId,
                saleId = null,
                observations = "Transfer OUT to $destinationStoreId",
                correlationId = correlationId,
                destinationStoreId = destinationStoreId
            )
        )

        // --- IN movement for destination ---
        movementPort.save(
            Movement(
                id = UUID.randomUUID(),
                productId = productId,
                storeId = destinationStoreId,
                type = MovementType.TRANSFER,
                quantity = quantity,
                previousQuantity = destStock?.quantity ?: 0,
                newQuantity = destNewQty,
                userId = userId,
                saleId = null,
                observations = "Transfer IN from $originStoreId",
                correlationId = correlationId,
                destinationStoreId = null
            )
        )

        return TransferResponse(
            transferId = UUID.randomUUID(),
            correlationId = correlationId,
            productId = productId,
            originStoreId = originStoreId,
            destinationStoreId = destinationStoreId,
            quantity = quantity,
            originNewStock = originNewQty,
            destinationNewStock = destNewQty,
            transferredBy = userId,
            transferredAt = now
        )
    }
}

/**
 * Response DTO for stock transfer.
 */
data class TransferResponse(
    val transferId: UUID,
    val correlationId: UUID,
    val productId: UUID,
    val originStoreId: UUID,
    val destinationStoreId: UUID,
    val quantity: Int,
    val originNewStock: Int,
    val destinationNewStock: Int,
    val transferredBy: UUID?,
    val transferredAt: Instant
)
