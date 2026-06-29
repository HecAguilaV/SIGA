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
 * All operations are tenant-scoped via JWT `tenantId` claim.
 *
 * Authorization model (multitenancy — fixed):
 *  - `platform_admin` principal → 403 on this endpoint (platform admins don't manage
 *    tenant users; they have a separate `/api/v1/platform/<resource>` surface).
 *  - `customer` principal → scope by `tenantId` (from JWT).
 *  - `user` principal with `customerId` → scope by that customerId (tenant-scoped).
 *  - `user` principal WITHOUT `customerId` (orphaned user) → 403.
 *  - Non-admin/owner user roles (OPERATOR, CASHIER, EMPLOYEE) → 403.
 *
 * Removed: the "OWNER/ADMINISTRATOR role sees all users" branch — that was the
 * multitenancy bug. OWNER of a tenant now sees only the users of THEIR tenant.
 */
@RestController
@RequestMapping("/api/v1/auth/users")
class UserController(
    private val manageUserUseCase: ManageUserUseCase
) {
    companion object {
        private val TENANT_ADMIN_ROLES = setOf("OWNER", "ADMINISTRATOR")
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()
        val tenantId = getCustomerIdFromSecurityContext()

        return when {
            // Platform admins do not manage tenant users from this endpoint
            principalType == "platform_admin" ->
                ResponseEntity.status(HttpStatus.FORBIDDEN).build()

            // Customer principal: scoped by tenantId from JWT
            principalType == "customer" && tenantId != null ->
                ResponseEntity.ok(manageUserUseCase.findByCustomerId(tenantId))

            // User principal: must be tenant-admin AND have a customerId (tenant scope)
            principalType == "user" && role in TENANT_ADMIN_ROLES && tenantId != null ->
                ResponseEntity.ok(manageUserUseCase.findByCustomerId(tenantId))

            // Anything else: 403
            else ->
                ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()
        val tenantId = getCustomerIdFromSecurityContext()

        if (principalType == null || tenantId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (principalType == "platform_admin") {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (principalType == "user" && role !in TENANT_ADMIN_ROLES) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val user = manageUserUseCase.findById(id)
        return if (user != null && isUserAccessible(user, tenantId)) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()
        val tenantId = getCustomerIdFromSecurityContext()

        if (principalType == null || tenantId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (principalType == "platform_admin") {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (principalType == "user" && role !in TENANT_ADMIN_ROLES) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val user = manageUserUseCase.findByEmail(email)
        return if (user != null && isUserAccessible(user, tenantId)) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()
        val tenantId = getCustomerIdFromSecurityContext()

        if (principalType == "platform_admin") {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (principalType == "user" && role !in TENANT_ADMIN_ROLES) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        // Force customerId from JWT — never trust client-provided tenant on create
        val scopedUser = if (tenantId != null) user.copy(customerId = tenantId) else user
        return ResponseEntity.status(201).body(manageUserUseCase.create(scopedUser))
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user: User): ResponseEntity<User> {
        val principalType = getPrincipalTypeFromSecurityContext()
        val role = getRoleFromSecurityContext()
        val tenantId = getCustomerIdFromSecurityContext()

        if (principalType == "platform_admin") {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        if (principalType == "user" && role !in TENANT_ADMIN_ROLES) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val existing = manageUserUseCase.findById(id)
        if (existing == null) return ResponseEntity.notFound().build()

        // Tenant isolation: a tenant admin can only update users of their own tenant
        if (tenantId != null && existing.customerId != tenantId) {
            return ResponseEntity.notFound().build()
        }

        // Prevent customerId spoofing on update
        val scopedUser = if (tenantId != null) user.copy(customerId = tenantId) else user
        return try {
            ResponseEntity.ok(manageUserUseCase.update(id, scopedUser))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Returns true if the user is accessible to the current principal.
     * - tenantId must be present (no backward compat — no auth = no access)
     * - User's customerId must match the principal's tenantId
     */
    private fun isUserAccessible(
        user: User,
        tenantId: Int?
    ): Boolean {
        if (tenantId == null) return false
        return user.customerId == tenantId
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
