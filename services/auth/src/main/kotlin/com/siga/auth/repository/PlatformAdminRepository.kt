package com.siga.auth.repository

import com.siga.auth.entity.PlatformAdmin
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA repository for platform_admins table.
 * Used by PlatformAdminJpaAdapter.
 */
@Repository
interface PlatformAdminRepository : JpaRepository<PlatformAdmin, UUID> {
    fun findByEmail(email: String): PlatformAdmin?
}
