package com.siga.auth.domain.port

import com.siga.auth.domain.model.Permission
import java.util.UUID

/**
 * Port for Permission persistence (hexagonal architecture).
 */
interface PermissionRepositoryPort {
    fun findById(id: UUID): Permission?
    fun findByName(name: String): Permission?
    fun findByCode(code: String): Permission?
    fun findAll(): List<Permission>
    fun save(permission: Permission): Permission
    fun deleteById(id: UUID)
}
