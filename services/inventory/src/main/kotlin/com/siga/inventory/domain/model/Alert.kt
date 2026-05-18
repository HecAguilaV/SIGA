package com.siga.inventory.domain.model

import java.time.Instant
import java.util.UUID

/**
 * Domain model representing a system-generated alert for inventory anomalies.
 *
 * WHY HEXAGONAL: This is the domain representation of an Alert. The entity
 * counterpart ([com.siga.inventory.entity.Alert]) carries JPA annotations
 * and belongs to the Infrastructure layer. This class is pure business logic.
 */
data class Alert(
    val id: UUID?,
    val type: AlertType,
    val productId: UUID?,
    val storeId: UUID?,
    val message: String,
    val isRead: Boolean,
    val createdAt: Instant
)

enum class AlertType {
    LOW_STOCK,
    OUT_OF_STOCK,
    HIGH_SALES,
    SUSPICIOUS_MOVEMENT
}
