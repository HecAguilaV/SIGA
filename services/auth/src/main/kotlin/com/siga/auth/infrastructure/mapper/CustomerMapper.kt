package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.Customer as DomainCustomer
import com.siga.auth.entity.Customer as CustomerEntity
import java.time.Instant

/**
 * Mapper between Customer JPA entity and Customer domain model.
 * Handles:
 * - Int? ↔ Int conversion for IDENTITY PK (0 means new entity)
 * - Instant? ↔ Instant for createdAt/updatedAt with fallback to Instant.now()
 */
object CustomerMapper {

    fun toDomain(entity: CustomerEntity): DomainCustomer {
        return DomainCustomer(
            id = entity.id,
            email = entity.email,
            passwordHash = entity.passwordHash,
            name = entity.name,
            lastName = entity.lastName,
            taxId = entity.taxId,
            phone = entity.phone,
            companyName = entity.companyName,
            isActive = entity.isActive,
            isOnTrial = entity.isOnTrial,
            trialStartAt = entity.trialStartAt,
            trialEndAt = entity.trialEndAt,
            role = entity.role,
            planId = entity.planId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: DomainCustomer): CustomerEntity {
        return CustomerEntity(
            id = domain.id ?: 0,
            email = domain.email,
            passwordHash = domain.passwordHash,
            name = domain.name,
            lastName = domain.lastName,
            taxId = domain.taxId,
            phone = domain.phone,
            companyName = domain.companyName,
            isActive = domain.isActive,
            isOnTrial = domain.isOnTrial,
            trialStartAt = domain.trialStartAt,
            trialEndAt = domain.trialEndAt,
            role = domain.role,
            planId = domain.planId,
            createdAt = domain.createdAt ?: Instant.now(),
            updatedAt = domain.updatedAt ?: Instant.now()
        )
    }
}
