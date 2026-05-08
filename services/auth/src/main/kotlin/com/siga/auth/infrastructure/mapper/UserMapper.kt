package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.User as DomainUser
import com.siga.auth.domain.model.UserRole as DomainUserRole
import com.siga.auth.entity.User as UserEntity
import com.siga.auth.entity.UserRole as EntityUserRole

/**
 * Mapper between User JPA entity and User domain model.
 * Handles UserRole enum conversion between entity and domain packages.
 */
object UserMapper {

    fun toDomain(entity: UserEntity): DomainUser {
        return DomainUser(
            id = entity.id,
            email = entity.email,
            passwordHash = entity.passwordHash,
            firstName = entity.firstName,
            lastName = entity.lastName,
            role = when (entity.role) {
                EntityUserRole.ADMINISTRATOR -> DomainUserRole.ADMINISTRATOR
                EntityUserRole.OPERATOR -> DomainUserRole.OPERATOR
                EntityUserRole.CASHIER -> DomainUserRole.CASHIER
            },
            commercialUserId = entity.commercialUserId,
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: DomainUser): UserEntity {
        return UserEntity(
            id = domain.id,
            email = domain.email,
            passwordHash = domain.passwordHash,
            firstName = domain.firstName,
            lastName = domain.lastName,
            role = when (domain.role) {
                DomainUserRole.ADMINISTRATOR -> EntityUserRole.ADMINISTRATOR
                DomainUserRole.OPERATOR -> EntityUserRole.OPERATOR
                DomainUserRole.CASHIER -> EntityUserRole.CASHIER
            },
            commercialUserId = domain.commercialUserId,
            isActive = domain.isActive,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
