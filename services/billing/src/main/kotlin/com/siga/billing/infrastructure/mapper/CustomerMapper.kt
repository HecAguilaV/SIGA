package com.siga.billing.infrastructure.mapper

import com.siga.billing.domain.model.Customer
import com.siga.billing.entity.CustomerEntity

object CustomerMapper {
    fun toDomain(entity: CustomerEntity): Customer {
        return Customer(
            id = entity.id ?: throw IllegalStateException("Customer ID cannot be null"),
            email = entity.email,
            name = entity.name,
            lastName = entity.lastName,
            taxId = entity.taxId,
            phoneNumber = entity.phone,
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

    fun toEntity(model: Customer): CustomerEntity {
        return CustomerEntity(
            id = model.id,
            email = model.email,
            passwordHash = "", // Password is not part of the domain model for security
            name = model.name,
            lastName = model.lastName,
            taxId = model.taxId,
            phone = model.phoneNumber,
            companyName = model.companyName,
            isActive = model.isActive,
            isOnTrial = model.isOnTrial,
            trialStartAt = model.trialStartAt,
            trialEndAt = model.trialEndAt,
            role = model.role,
            planId = model.planId,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt
        )
    }
}
