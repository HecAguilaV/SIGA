package com.siga.auth.infrastructure.mapper

import com.siga.auth.domain.model.PlatformAdmin
import com.siga.auth.entity.PlatformAdmin as PlatformAdminEntity

/**
 * Maps between PlatformAdmin domain model and JPA entity.
 */
object PlatformAdminMapper {
    fun toDomain(entity: PlatformAdminEntity): PlatformAdmin = PlatformAdmin(
        id = entity.id ?: error("PlatformAdmin.id must not be null after persist"),
        email = entity.email,
        passwordHash = entity.passwordHash,
        displayName = entity.displayName,
        isActive = entity.isActive,
        lastLoginAt = entity.lastLoginAt,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )

    fun toEntity(domain: PlatformAdmin): PlatformAdminEntity = PlatformAdminEntity(
        id = domain.id,
        email = domain.email,
        passwordHash = domain.passwordHash,
        displayName = domain.displayName,
        isActive = domain.isActive,
        lastLoginAt = domain.lastLoginAt,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt
    )
}
