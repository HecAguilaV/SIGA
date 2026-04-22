package com.siga.sales.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "sales", schema = "sales")
class Sale(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    @Column(name = "store_id", nullable = false)
    val storeId: Int,

    @Column(name = "user_id")
    val userId: Int? = null,

    @Column(name = "commercial_user_id")
    val commercialUserId: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal,

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    var status: SaleStatus = SaleStatus.COMPLETED,

    @Column(columnDefinition = "TEXT")
    var observations: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sale) return false
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "Sale(id=$id, storeId=$storeId, total=$total, status=$status)"
}
