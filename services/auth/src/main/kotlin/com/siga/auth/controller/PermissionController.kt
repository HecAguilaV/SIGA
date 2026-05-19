package com.siga.auth.controller

import com.siga.auth.application.usecase.ManageUserPermissionUseCase
import com.siga.auth.domain.model.Permission
import com.siga.auth.domain.port.PermissionRepositoryPort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller for permission catalogue CRUD and user-permission assignment.
 *
 * Catalogue endpoints: /api/v1/auth/permissions
 * User-permission endpoints: /api/v1/auth/users/{userId}/permissions
 *
 * All endpoints require authentication (enforced by SecurityConfig).
 * Mutating operations validate tenant ownership via JWT claims.
 */
@RestController
@RequestMapping("/api/v1/auth")
class PermissionController(
    private val permissionRepositoryPort: PermissionRepositoryPort,
    private val manageUserPermissionUseCase: ManageUserPermissionUseCase
) {

    // ===== Permission Catalogue =====

    @GetMapping("/permissions")
    fun getAllPermissions(): ResponseEntity<List<Permission>> {
        return ResponseEntity.ok(permissionRepositoryPort.findAll())
    }

    @PostMapping("/permissions")
    fun createPermission(@RequestBody permission: Permission): ResponseEntity<Permission> {
        return try {
            val saved = permissionRepositoryPort.save(permission)
            ResponseEntity.status(HttpStatus.CREATED).body(saved)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/permissions/{id}")
    fun updatePermission(@PathVariable id: UUID, @RequestBody permission: Permission): ResponseEntity<Permission> {
        val existing = permissionRepositoryPort.findById(id) ?: return ResponseEntity.notFound().build()
        val updated = permission.copy(id = id)
        return try {
            ResponseEntity.ok(permissionRepositoryPort.save(updated))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/permissions/{id}")
    fun deletePermission(@PathVariable id: UUID): ResponseEntity<Void> {
        val existing = permissionRepositoryPort.findById(id) ?: return ResponseEntity.notFound().build()
        permissionRepositoryPort.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    // ===== User-Permission Assignment =====

    @GetMapping("/users/{userId}/permissions")
    fun getUserPermissions(@PathVariable userId: UUID): ResponseEntity<List<com.siga.auth.domain.model.UserPermission>> {
        return ResponseEntity.ok(manageUserPermissionUseCase.findByUserId(userId))
    }

    @PostMapping("/users/{userId}/permissions")
    fun assignPermission(
        @PathVariable userId: UUID,
        @RequestBody request: AssignPermissionRequest
    ): ResponseEntity<com.siga.auth.domain.model.UserPermission> {
        val tenantId = getCustomerIdFromSecurityContext()

        return try {
            val result = manageUserPermissionUseCase.assign(
                userId = userId,
                permissionId = request.permissionId,
                assignedBy = null,
                tenantId = tenantId
            )
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            when {
                e.message!!.contains("Cross-tenant") -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                else -> ResponseEntity.badRequest().build()
            }
        }
    }

    @DeleteMapping("/users/{userId}/permissions/{permissionId}")
    fun revokePermission(
        @PathVariable userId: UUID,
        @PathVariable permissionId: UUID
    ): ResponseEntity<Void> {
        val tenantId = getCustomerIdFromSecurityContext()

        return try {
            manageUserPermissionUseCase.revoke(userId, permissionId, tenantId)
            ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            when {
                e.message!!.contains("Cross-tenant") -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                else -> ResponseEntity.notFound().build()
            }
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/users/{userId}/permissions/verify")
    fun verifyPermission(
        @PathVariable userId: UUID,
        @RequestParam code: UUID
    ): ResponseEntity<Map<String, Boolean>> {
        val hasPermission = manageUserPermissionUseCase.existsByUserIdAndPermissionId(userId, code)
        return ResponseEntity.ok(mapOf("hasPermission" to hasPermission))
    }

    // ===== Security Context Helpers =====

    private fun getCustomerIdFromSecurityContext(): Int? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is UsernamePasswordAuthenticationToken && auth.details is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val details = auth.details as Map<String, Any?>
            return details["tenantId"] as? Int
        }
        return null
    }

}

/**
 * Request DTO for assigning a permission to a user.
 */
data class AssignPermissionRequest(
    val permissionId: UUID
)
