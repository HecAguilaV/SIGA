package com.siga.billing.infrastructure.adapter

import com.siga.billing.domain.model.Customer
import com.siga.billing.domain.port.CustomerRepositoryPort
import com.siga.billing.entity.CustomerEntity
import com.siga.billing.infrastructure.mapper.CustomerMapper
import com.siga.billing.repository.CustomerRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing the CustomerRepositoryPort.
 * Bridges the gap between Domain and Spring Data JPA.
 */
@Component
class CustomerJpaAdapter(
    private val customerRepository: CustomerRepository
) : CustomerRepositoryPort {

    override fun findById(id: UUID): Customer? {
        val entity = customerRepository.findById(id)
        return if (entity.isPresent) CustomerMapper.toDomain(entity.get()) else null
    }

    override fun save(customer: Customer): Customer {
        val entity = CustomerMapper.toEntity(customer)
        val savedEntity = customerRepository.save(entity)
        return CustomerMapper.toDomain(savedEntity)
    }

    override fun findByEmail(email: String): Customer? {
        val entity = customerRepository.findByEmail(email) ?: return null
        return CustomerMapper.toDomain(entity)
    }
}
