package com.siga.sales.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

/**
 * Customer of a SME (PyME) that purchases goods or services.
 *
 * Represents the end client of our SME customers. Used primarily
 * for Factura generation where a customer reference is mandatory
 * per Chilean tax law (SII).
 *
 * @see SaleDocument the tax document that references this customer
 */
@Entity
@Table(name = "customers", schema = "sales")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "tax_id", unique = true, length = 20)
    val taxId: String? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(length = 255)
    var email: String? = null,

    @Column(length = 20)
    var phone: String? = null,

    @Column(columnDefinition = "TEXT")
    var address: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Customer) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Customer(id=$id, name=$name, taxId=$taxId)"
}
