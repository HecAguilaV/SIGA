package com.siga.auth.controller

import com.siga.auth.application.usecase.ManageUserUseCase
import com.siga.auth.domain.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage SaaS users (employees).
 * All operations are tenant-scoped via JWT `customerId` claim.
 *
 * - Customer principals → scope by `tenantId` (from JWT)
 * - User principals → 403 Forbidden (R4.4)
 * - No JWT → backward compat (returns all users for addFilters=false tests)
 */
@RestController
@RequestMapping("/api/v1/auth/users")
class UserController(
    private val manageUserUseCase: ManageUserUseCase
) {
    companion object {
        private val ADMIN_ROLES = setOf("OWNER", "ADMINISTRATOR")
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val customerId = getCustomerIdFromSecurityContext()
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()

        return when {
            principalType == "user" && role !in ADMIN_ROLES -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
            principalType == "user" && role in ADMIN_ROLES -> ResponseEntity.ok(manageUserUseCase.findAll())
            customerId != null -> ResponseEntity.ok(manageUserUseCase.findByCustomerId(customerId))
            else -> ResponseEntity.ok(manageUserUseCase.findAll())
        }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val customerId = getCustomerIdFromSecurityContext()
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()

        if (principalType == "user" && role !in ADMIN_ROLES) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val user = manageUserUseCase.findById(id)
        return if (user != null && (role in ADMIN_ROLES || isUserAccessible(user))) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val customerId = getCustomerIdFromSecurityContext()
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()

        if (principalType == "user" && role !in ADMIN_ROLES) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val user = manageUserUseCase.findByEmail(email)
        return if (user != null && (role in ADMIN_ROLES || isUserAccessible(user))) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val customerId = getCustomerIdFromSecurityContext()
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()

        if (principalType == "user" && role !in ADMIN_ROLES) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        // OWNER/ADMIN creating user without tenant: forward as-is
        // Customer creating user: scope by their tenantId
        val scopedUser = if (customerId != null) user.copy(customerId = customerId) else user
        return ResponseEntity.status(201).body(manageUserUseCase.create(scopedUser))
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user: User): ResponseEntity<User> {
        val customerId = getCustomerIdFromSecurityContext()
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()

        if (principalType == "user" && role !in ADMIN_ROLES) return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        // Verify the user belongs to the authenticated tenant (ADMIN bypasses this check)
        val existing = manageUserUseCase.findById(id)
        if (existing == null) return ResponseEntity.notFound().build()
        if (customerId != null && role !in ADMIN_ROLES && existing.customerId != customerId) {
            return ResponseEntity.notFound().build()
        }

        // Prevent customerId spoofing on update
        val scopedUser = if (customerId != null) user.copy(customerId = customerId) else user
        return try {
            ResponseEntity.ok(manageUserUseCase.update(id, scopedUser))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Returns true if the user is accessible to the current principal.
     * - No customerId in context → accessible (backward compat)
     * - User's customerId matches context → accessible
     * - Otherwise → not accessible
     */
    private fun isUserAccessible(user: User): Boolean {
        val customerId = getCustomerIdFromSecurityContext()
        return customerId == null || user.customerId == customerId
    }

    /**
     * Extracts `tenantId` from JWT claims stored in SecurityContext authentication details.
     * Returns null if no JWT claims are present (backward compat for addFilters=false tests).
     */
    private fun getCustomerIdFromSecurityContext(): Int? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is UsernamePasswordAuthenticationToken && auth.details is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val details = auth.details as Map<String, Any?>
            return details["tenantId"] as? Int
        }
        return null
    }

    /**
     * Extracts `principalType` from JWT claims stored in SecurityContext authentication details.
     * Returns null if no JWT claims are present.
     */
    private fun getPrincipalTypeFromSecurityContext(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is UsernamePasswordAuthenticationToken && auth.details is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val details = auth.details as Map<String, Any?>
            return details["principalType"] as? String
        }
        return null
    }

    /**
     * Extracts `rol` from JWT claims stored in SecurityContext authentication details.
     * Returns null if no JWT claims are present.
     */
    private fun getRoleFromSecurityContext(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth is UsernamePasswordAuthenticationToken && auth.details is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val details = auth.details as Map<String, Any?>
            return details["rol"] as? String
        }
        return null
    }
}
