package com.siga.sales.domain.port

import com.siga.sales.domain.model.Customer
import java.util.UUID

/**
 * Port for Customer persistence.
 */
interface CustomerRepositoryPort {
    fun findById(id: UUID): Customer?
    fun save(customer: Customer): Customer
    fun findByTaxId(taxId: String): Customer?
}
