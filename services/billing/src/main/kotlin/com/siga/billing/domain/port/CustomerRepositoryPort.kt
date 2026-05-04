package com.siga.billing.domain.port

import com.siga.billing.domain.model.Customer
import java.util.UUID

/**
 * Port for Customer persistence.
 * Defines the contract the domain needs, implemented in infrastructure.
 */
interface CustomerRepositoryPort {
    fun findById(id: UUID): Customer?
    fun save(customer: Customer): Customer
    fun findByEmail(email: String): Customer?
}
