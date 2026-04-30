package com.siga.billing.repository

import com.siga.billing.entity.ShoppingCart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for shopping carts.
 */
@Repository
interface ShoppingCartRepository : JpaRepository<ShoppingCart, UUID> {
    fun findByCustomerId(customerId: UUID): ShoppingCart?
}