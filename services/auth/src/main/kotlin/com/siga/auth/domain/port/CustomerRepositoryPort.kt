package com.siga.auth.domain.port

import com.siga.auth.domain.model.Customer

/**
 * Port for Customer persistence (hexagonal architecture).
 */
interface CustomerRepositoryPort {
    fun findById(id: Int): Customer?
    fun findByEmail(email: String): Customer?
    fun existsByEmail(email: String): Boolean
    fun findAll(): List<Customer>
    fun save(customer: Customer): Customer
}
