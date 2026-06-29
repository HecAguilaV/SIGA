package com.siga.auth.application.usecase

import com.siga.auth.domain.model.PlatformAdmin
import com.siga.auth.domain.model.User
import com.siga.auth.domain.port.PermissionRepositoryPort
import com.siga.auth.domain.port.PlatformAdminRepositoryPort
import com.siga.auth.domain.port.RolePermissionRepositoryPort
import com.siga.auth.domain.port.UserPermissionRepositoryPort
import com.siga.auth.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Use case for login flow.
 *
 * Resolution order (most specific first):
 * 1. Platform admin (auth.platform_admins) → platform-level SaaS owner
 * 2. User (auth.users) — tenant-scoped employee, JWT carries customerId
 * 3. Customer (auth.customers) — legacy principal, fallback for seed/legacy data
 *
 * The fix for the multitenancy bug lives here:
 *  - authenticateUser now passes `user.customerId` as the JWT `tenantId` claim
 *    (was hardcoded to null), so the UserController can filter by tenant.
 *  - Platform admins have their own authentication path; they don't go through
 *    the users table at all.
 */
@Service
class LoginUseCase(
    private val manageCustomerUseCase: ManageCustomerUseCase,
    private val manageUserUseCase: ManageUserUseCase,
    private val platformAdminRepositoryPort: PlatformAdminRepositoryPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val userPermissionRepositoryPort: UserPermissionRepositoryPort,
    private val permissionRepositoryPort: PermissionRepositoryPort,
    private val rolePermissionRepositoryPort: RolePermissionRepositoryPort
) {

    fun login(email: String, rawPassword: String): LoginResult {
        // 1. Platform admin — checked first because their email is unique across the platform
        val platformAdmin = platformAdminRepositoryPort.findByEmail(email)
        if (platformAdmin != null) {
            return authenticatePlatformAdmin(platformAdmin, rawPassword)
        }

        // 2. Tenant user (employee) — primary principal after consolidation (Customer IS Owner)
        val user = manageUserUseCase.findByEmail(email)
        if (user != null) {
            return authenticateUser(user, rawPassword)
        }

        // 3. Customer (legacy records or seed-only data)
        val customer = manageCustomerUseCase.findByEmail(email)
        if (customer != null) {
            return authenticateCustomer(customer, rawPassword)
        }

        // 4. Not found in any table
        throw NoSuchElementException("Invalid credentials")
    }

    private fun authenticatePlatformAdmin(
        platformAdmin: PlatformAdmin,
        rawPassword: String
    ): LoginResult {
        if (!platformAdmin.isActive) {
            throw IllegalStateException("Platform admin account is not active")
        }

        if (!passwordEncoder.matches(rawPassword, platformAdmin.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        // Platform admins have all platform-level permissions by design.
        // Tenant-scoped permissions are NOT granted here.
        val permissions = listOf("*")

        val token = jwtService.generateToken(
            email = platformAdmin.email,
            rol = "PLATFORM_ADMIN",
            tenantId = null,
            principalType = "platform_admin",
            permissions = permissions
        )

        // Update last login (best-effort, ignore failures)
        try {
            platformAdminRepositoryPort.updateLastLogin(platformAdmin.id, Instant.now())
        } catch (_: Exception) {
            // swallow — login must not fail on audit write
        }

        return LoginResult(
            token = token,
            email = platformAdmin.email,
            tenantId = null,
            role = "PLATFORM_ADMIN",
            principalType = "platform_admin",
            permissions = permissions
        )
    }

    private fun authenticateCustomer(customer: com.siga.auth.domain.model.Customer, rawPassword: String): LoginResult {
        if (!customer.isActive) {
            throw IllegalStateException("Account is not active")
        }

        if (!passwordEncoder.matches(rawPassword, customer.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val rolePermissions = rolePermissionRepositoryPort
            .findByRole(customer.role)
            .mapNotNull { rolePerm -> permissionRepositoryPort.findById(rolePerm.permissionId)?.code }

        val token = jwtService.generateToken(
            email = customer.email,
            rol = customer.role,
            tenantId = customer.id,
            principalType = "customer",
            permissions = rolePermissions
        )

        return LoginResult(
            token = token,
            email = customer.email,
            tenantId = customer.id,
            role = customer.role,
            principalType = "customer",
            permissions = rolePermissions
        )
    }

    private fun authenticateUser(user: User, rawPassword: String): LoginResult {
        if (!user.isActive) {
            throw IllegalStateException("Account is not active")
        }

        if (!passwordEncoder.matches(rawPassword, user.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        // Merge user-specific permissions + role-based permissions, deduplicated
        val userPermissions = userPermissionRepositoryPort
            .findByUserId(user.id!!)
            .mapNotNull { userPerm -> permissionRepositoryPort.findById(userPerm.permissionId)?.code }

        val rolePermissions = rolePermissionRepositoryPort
            .findByRole(user.role.name)
            .mapNotNull { rolePerm -> permissionRepositoryPort.findById(rolePerm.permissionId)?.code }

        val permissions = (userPermissions + rolePermissions).distinct()

        // FIX: pass the user's customerId as the JWT tenantId so the UserController
        // can filter /api/v1/auth/users by tenant. Previously this was always null,
        // which made the controller fall through to findAll() and broke multitenancy.
        val token = jwtService.generateToken(
            email = user.email,
            rol = user.role.name,
            tenantId = user.customerId,
            principalType = "user",
            permissions = permissions
        )

        return LoginResult(
            token = token,
            email = user.email,
            tenantId = user.customerId,
            role = user.role.name,
            principalType = "user",
            userId = user.id,
            permissions = permissions
        )
    }
}
