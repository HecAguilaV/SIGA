package com.siga.auth.domain.port

import com.siga.auth.domain.model.PlatformAdmin
import java.util.UUID

/**
 * Hexagonal port for platform admin persistence.
 * Platform admins live in their own table (auth.platform_admins), separate from users.
 */
interface PlatformAdminRepositoryPort {
    fun findById(id: UUID): PlatformAdmin?
    fun findByEmail(email: String): PlatformAdmin?
    fun save(platformAdmin: PlatformAdmin): PlatformAdmin
    fun updateLastLogin(id: UUID, lastLoginAt: java.time.Instant)
}
