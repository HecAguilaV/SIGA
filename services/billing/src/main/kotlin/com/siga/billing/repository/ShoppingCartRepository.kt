package com.siga.billing.repository

import com.siga.billing.entity.ShoppingCart
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for shopping carts.
 */
@Repository
interface ShoppingCartRepository : JpaRepository<ShoppingCart, Int> {
    fun findByCustomerId(customerId: Int): ShoppingCart?
}