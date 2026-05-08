package com.siga.auth.domain.port

import com.siga.auth.domain.model.Permission

/**
 * Port for Permission persistence (hexagonal architecture).
 */
interface PermissionRepositoryPort {
    fun findById(id: Int): Permission?
    fun findByName(name: String): Permission?
    fun findAll(): List<Permission>
    fun save(permission: Permission): Permission
}
