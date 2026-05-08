package com.siga.sales.domain.port

import com.siga.sales.domain.model.PaymentMethod
import java.util.UUID

/**
 * Port for PaymentMethod persistence.
 */
interface PaymentMethodRepositoryPort {
    fun findById(id: UUID): PaymentMethod?
    fun save(paymentMethod: PaymentMethod): PaymentMethod
    fun findAll(): List<PaymentMethod>
}
