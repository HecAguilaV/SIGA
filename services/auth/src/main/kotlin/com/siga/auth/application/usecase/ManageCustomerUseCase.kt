package com.siga.auth.application.usecase

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import org.springframework.stereotype.Service

/**
 * Use case for Customer management operations.
 * Encapsulates domain logic (validation, uniqueness checks) above the port layer.
 */
@Service
class ManageCustomerUseCase(
    private val customerRepositoryPort: CustomerRepositoryPort
) {

    fun findById(id: Int): Customer? = customerRepositoryPort.findById(id)

    fun findByEmail(email: String): Customer? = customerRepositoryPort.findByEmail(email)

    fun findAll(): List<Customer> = customerRepositoryPort.findAll()

    fun create(customer: Customer): Customer {
        require(customer.email.isNotBlank()) { "Email must not be blank" }
        require(customer.name.isNotBlank()) { "Name must not be blank" }

        if (customerRepositoryPort.existsByEmail(customer.email)) {
            throw IllegalArgumentException("Email already exists: ${customer.email}")
        }

        return customerRepositoryPort.save(customer.copy(id = null))
    }

    fun update(id: Int, customer: Customer): Customer {
        val existing = customerRepositoryPort.findById(id)
            ?: throw IllegalArgumentException("Customer not found: $id")

        return customerRepositoryPort.save(customer.copy(id = id))
    }
}
