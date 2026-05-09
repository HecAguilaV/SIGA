package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.Permission as DomainPermission
import com.siga.auth.entity.Permission as PermissionEntity
import java.time.Instant

/**
 * Mapper between Permission JPA entity and Permission domain model.
 * Handles:
 * - UUID? ↔ UUID? (null-safe, DB generates UUID via default)
 * - Instant? ↔ Instant for createdAt with fallback to Instant.now()
 */
object PermissionMapper {

    fun toDomain(entity: PermissionEntity): DomainPermission {
        return DomainPermission(
            id = entity.id,
            code = entity.code,
            name = entity.name,
            description = entity.description,
            category = entity.category,
            isActive = entity.isActive,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: DomainPermission): PermissionEntity {
        return PermissionEntity(
            id = domain.id,
            code = domain.code,
            name = domain.name,
            description = domain.description,
            category = domain.category,
            isActive = domain.isActive,
            createdAt = domain.createdAt ?: Instant.now()
        )
    }
}
