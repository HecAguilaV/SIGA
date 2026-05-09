package com.siga.auth.repository

import com.siga.auth.entity.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for permissions.
 */
@Repository
interface PermissionRepository : JpaRepository<Permission, UUID> {
    fun findByName(name: String): Permission?
}
