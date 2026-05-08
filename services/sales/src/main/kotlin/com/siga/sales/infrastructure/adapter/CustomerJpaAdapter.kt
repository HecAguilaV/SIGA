package com.siga.sales.infrastructure.adapter

import com.siga.sales.domain.model.Customer
import com.siga.sales.domain.port.CustomerRepositoryPort
import com.siga.sales.entity.CustomerEntity
import com.siga.sales.infrastructure.mapper.CustomerMapper
import org.springframework.stereotype.Component

/**
 * JPA Adapter for Customer.
 */
@Component
class CustomerJpaAdapter(
    private val customerRepository: com.siga.sales.repository.CustomerRepository,
    private val customerMapper: CustomerMapper
) : CustomerRepositoryPort {

    override fun findById(id: UUID): Customer? {
        return customerRepository.findById(id).orElse(null)?.let { customerMapper.toDomain(it) }
    }

    override fun save(customer: Customer): Customer {
        val entity = customerMapper.toEntity(customer)
        return customerMapper.toDomain(customerRepository.save(entity))
    }

    override fun findByTaxId(taxId: String): Customer? {
        return customerRepository.findByTaxId(taxId)?.let { customerMapper.toDomain(it) }
    }
}
