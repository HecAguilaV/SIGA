package com.siga.sales.application.usecase

import com.siga.sales.domain.model.Customer
import com.siga.sales.domain.port.CustomerRepositoryPort
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Use case for managing customer operations.
 */
@Component
class ManageCustomerUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort
) {
    fun createCustomer(customer: Customer): Customer {
        return customerRepositoryPort.save(customer)
    }

    fun findCustomerById(id: UUID): Customer? {
        return customerRepositoryPort.findById(id)
    }

    fun findCustomerByTaxId(taxId: String): Customer? {
        return customerRepositoryPort.findByTaxId(taxId)
    }
}
