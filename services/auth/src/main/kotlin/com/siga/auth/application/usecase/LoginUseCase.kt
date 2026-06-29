package com.siga.auth.application.usecase

import com.siga.auth.domain.model.User
import com.siga.auth.domain.port.PermissionRepositoryPort
import com.siga.auth.domain.port.RolePermissionRepositoryPort
import com.siga.auth.domain.port.UserPermissionRepositoryPort
import com.siga.auth.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Use case for dual-principal login.
 *
 * CONSOLIDATION (Customer IS Owner):
 * After registration, every customer has a paired User with role OWNER. Login resolves
 * the User first because that's the principal that carries the role-based permissions.
 * Customer lookup is kept as a fallback for backward compatibility with seed data or
 * legacy records that exist only in the customers table.
 */
@Service
class LoginUseCase(
    private val manageCustomerUseCase: ManageCustomerUseCase,
    private val manageUserUseCase: ManageUserUseCase,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val userPermissionRepositoryPort: UserPermissionRepositoryPort,
    private val permissionRepositoryPort: PermissionRepositoryPort,
    private val rolePermissionRepositoryPort: RolePermissionRepositoryPort
) {

    fun login(email: String, rawPassword: String): LoginResult {
        // 1. Try User first — primary principal after consolidation (Customer IS Owner)
        val user = manageUserUseCase.findByEmail(email)
        if (user != null) {
            return authenticateUser(user, rawPassword)
        }

        // 2. Fallback: Customer (legacy records or seed-only data)
        val customer = manageCustomerUseCase.findByEmail(email)
        if (customer != null) {
            return authenticateCustomer(customer, rawPassword)
        }

        // 3. Neither found
        throw NoSuchElementException("Invalid credentials")
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

        val token = jwtService.generateToken(
            email = user.email,
            rol = user.role.name,
            tenantId = null,
            principalType = "user",
            permissions = permissions
        )

        return LoginResult(
            token = token,
            email = user.email,
            tenantId = null,
            role = user.role.name,
            principalType = "user",
            userId = user.id,
            permissions = permissions
        )
    }
}
