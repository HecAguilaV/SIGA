package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.Permission
import com.siga.auth.domain.port.PermissionRepositoryPort
import com.siga.auth.infrastructure.mapper.PermissionMapper
import com.siga.auth.repository.PermissionRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * JPA Adapter implementing PermissionRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class PermissionJpaAdapter(
    private val permissionRepository: PermissionRepository
) : PermissionRepositoryPort {

    override fun findById(id: UUID): Permission? {
        return permissionRepository.findById(id).map { PermissionMapper.toDomain(it) }.orElse(null)
    }

    override fun findByName(name: String): Permission? {
        val entity = permissionRepository.findByName(name) ?: return null
        return PermissionMapper.toDomain(entity)
    }

    override fun findByCode(code: String): Permission? {
        val entity = permissionRepository.findByCode(code) ?: return null
        return PermissionMapper.toDomain(entity)
    }

    override fun findAll(): List<Permission> = permissionRepository.findAll().map { PermissionMapper.toDomain(it) }

    override fun save(permission: Permission): Permission {
        val entity = PermissionMapper.toEntity(permission)
        // Auto-generate UUID when id is null (tests pass null, expecting DB generation)
        if (entity.id == null) {
            entity.id = UUID.randomUUID()
        }
        val saved = permissionRepository.save(entity)
        return PermissionMapper.toDomain(saved)
    }

    override fun deleteById(id: UUID) {
        permissionRepository.deleteById(id)
    }
}
