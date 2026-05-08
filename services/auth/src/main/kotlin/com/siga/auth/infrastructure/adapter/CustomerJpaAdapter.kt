package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import com.siga.auth.infrastructure.mapper.CustomerMapper
import com.siga.auth.repository.CustomerRepository
import org.springframework.stereotype.Component

/**
 * JPA Adapter implementing CustomerRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class CustomerJpaAdapter(
    private val customerRepository: CustomerRepository
) : CustomerRepositoryPort {

    override fun findById(id: Int): Customer? {
        return customerRepository.findById(id).map { CustomerMapper.toDomain(it) }.orElse(null)
    }

    override fun findByEmail(email: String): Customer? {
        val entity = customerRepository.findByEmail(email) ?: return null
        return CustomerMapper.toDomain(entity)
    }

    override fun existsByEmail(email: String): Boolean = customerRepository.existsByEmail(email)

    override fun findAll(): List<Customer> = customerRepository.findAll().map { CustomerMapper.toDomain(it) }

    override fun save(customer: Customer): Customer {
        val entity = CustomerMapper.toEntity(customer)
        val saved = customerRepository.save(entity)
        return CustomerMapper.toDomain(saved)
    }
}
