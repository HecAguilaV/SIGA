package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * System-generated alert for inventory anomalies.
 *
 * Alerts are triggered automatically when stock falls below minimum
 * thresholds or suspicious movements are detected.
 */
@Entity
@Table(name = "alerts", schema = "inventory")
class Alert(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val type: AlertType,

    @Column(name = "product_id")
    val productId: UUID? = null,

    @Column(name = "store_id")
    val storeId: UUID? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Alert) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Alert(id=$id, type=$type, isRead=$isRead)"
}
