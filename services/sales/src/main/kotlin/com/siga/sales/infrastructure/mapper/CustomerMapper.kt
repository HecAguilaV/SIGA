package com.siga.sales.infrastructure.mapper

import com.siga.sales.domain.model.Customer
import com.siga.sales.entity.CustomerEntity
import java.time.Instant
import java.util.UUID

/**
 * Mapper between Customer domain model and CustomerEntity JPA entity.
 */
@Component
class CustomerMapper {

    fun toDomain(entity: CustomerEntity): Customer {
        return Customer(
            id = entity.id ?: UUID.randomUUID(),
            taxId = entity.taxId,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            address = entity.address,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Customer): CustomerEntity {
        return CustomerEntity(
            id = if (domain.id == UUID.fromString("00000000-0000-0000-0000-000000000000")) null else domain.id,
            taxId = domain.taxId,
            name = domain.name,
            email = domain.email,
            phone = domain.phone,
            address = domain.address
        )
    }
}
