package com.siga.auth.repository

import com.siga.auth.entity.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for permissions.
 */
@Repository
interface PermissionRepository : JpaRepository<Permission, Int> {
    fun findByName(name: String): Permission?
}
