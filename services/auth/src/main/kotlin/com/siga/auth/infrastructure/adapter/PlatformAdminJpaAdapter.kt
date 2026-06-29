package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.model.PlatformAdmin
import com.siga.auth.domain.port.PlatformAdminRepositoryPort
import com.siga.auth.infrastructure.mapper.PlatformAdminMapper
import com.siga.auth.repository.PlatformAdminRepository
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

/**
 * JPA Adapter implementing PlatformAdminRepositoryPort.
 * Delegates to Spring Data JPA repository + mapper.
 *
 * Note: Platform admins have no customerId and are not tenant-scoped.
 */
@Component
class PlatformAdminJpaAdapter(
    private val platformAdminRepository: PlatformAdminRepository
) : PlatformAdminRepositoryPort {

    override fun findById(id: UUID): PlatformAdmin? {
        return platformAdminRepository.findById(id)
            .map { PlatformAdminMapper.toDomain(it) }
            .orElse(null)
    }

    override fun findByEmail(email: String): PlatformAdmin? {
        val entity = platformAdminRepository.findByEmail(email) ?: return null
        return PlatformAdminMapper.toDomain(entity)
    }

    override fun save(platformAdmin: PlatformAdmin): PlatformAdmin {
        val entity = PlatformAdminMapper.toEntity(platformAdmin)
        val saved = platformAdminRepository.save(entity)
        return PlatformAdminMapper.toDomain(saved)
    }

    override fun updateLastLogin(id: UUID, lastLoginAt: Instant) {
        val entity = platformAdminRepository.findById(id).orElse(null) ?: return
        entity.lastLoginAt = lastLoginAt
        platformAdminRepository.save(entity)
    }
}
