package com.siga.inventory.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "alerts", schema = "inventory")
class Alert(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    val type: AlertType,

    @Column(name = "product_id")
    val productId: Int? = null,

    @Column(name = "store_id")
    val storeId: Int? = null,

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
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Alert(id=$id, type=$type, isRead=$isRead)"
}
