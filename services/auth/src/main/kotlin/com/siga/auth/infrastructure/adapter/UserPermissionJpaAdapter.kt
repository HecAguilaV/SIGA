package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.UserPermission
import com.siga.auth.domain.port.UserPermissionRepositoryPort
import com.siga.auth.infrastructure.mapper.UserPermissionMapper
import com.siga.auth.repository.UserPermissionRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * JPA Adapter implementing UserPermissionRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 */
@Component
class UserPermissionJpaAdapter(
    private val userPermissionRepository: UserPermissionRepository
) : UserPermissionRepositoryPort {

    override fun findByUserId(userId: UUID): List<UserPermission> {
        return userPermissionRepository.findById_UserId(userId).map { UserPermissionMapper.toDomain(it) }
    }

    override fun findByPermissionId(permissionId: UUID): List<UserPermission> {
        return userPermissionRepository.findById_PermissionId(permissionId).map { UserPermissionMapper.toDomain(it) }
    }

    override fun save(userPermission: UserPermission): UserPermission {
        val entity = UserPermissionMapper.toEntity(userPermission)
        val saved = userPermissionRepository.save(entity)
        return UserPermissionMapper.toDomain(saved)
    }

    @Transactional
    override fun deleteByUserIdAndPermissionId(userId: UUID, permissionId: UUID) {
        userPermissionRepository.deleteById_UserIdAndId_PermissionId(userId, permissionId)
    }

    override fun existsByUserIdAndPermissionId(userId: UUID, permissionId: UUID): Boolean {
        return userPermissionRepository.existsById_UserIdAndId_PermissionId(userId, permissionId)
    }
}
