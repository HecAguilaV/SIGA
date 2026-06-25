package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.RolePermission
import com.siga.auth.domain.port.RolePermissionRepositoryPort
import com.siga.auth.infrastructure.mapper.RolePermissionMapper
import com.siga.auth.repository.RolePermissionRepository
import org.springframework.stereotype.Component

/**
 * JPA Adapter implementing RolePermissionRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class RolePermissionJpaAdapter(
    private val rolePermissionRepository: RolePermissionRepository
) : RolePermissionRepositoryPort {

    override fun findByRole(role: String): List<RolePermission> {
        return rolePermissionRepository.findByIdRole(role).map { RolePermissionMapper.toDomain(it) }
    }
}
