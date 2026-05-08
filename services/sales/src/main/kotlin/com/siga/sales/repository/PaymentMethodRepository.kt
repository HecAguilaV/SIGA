package com.siga.sales.repository

import com.siga.sales.entity.PaymentMethodEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for payment methods (JPA).
 */
@Repository
interface PaymentMethodRepository : JpaRepository<PaymentMethodEntity, UUID> {
    fun findByName(name: String): PaymentMethodEntity?
}
