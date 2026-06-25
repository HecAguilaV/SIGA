package com.siga.auth.controller

import com.siga.auth.application.usecase.LoginUseCase
import com.siga.auth.application.usecase.RegisterCustomerUseCase
import com.siga.auth.application.usecase.ResetPasswordConfirmUseCase
import com.siga.auth.application.usecase.ResetPasswordRequestUseCase
import com.siga.auth.application.usecase.VerifyCustomerUseCase
import com.siga.auth.security.JwtService
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

/**
 * Controller for authentication flows: registration, email verification, login, and password reset.
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerCustomerUseCase: RegisterCustomerUseCase,
    private val verifyCustomerUseCase: VerifyCustomerUseCase,
    private val loginUseCase: LoginUseCase,
    private val resetPasswordRequestUseCase: ResetPasswordRequestUseCase,
    private val resetPasswordConfirmUseCase: ResetPasswordConfirmUseCase,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Map<String, String>> {
        return try {
            registerCustomerUseCase.register(
                email = request.email,
                rawPassword = request.password,
                name = request.name,
                companyName = request.companyName
            )
            ResponseEntity.status(HttpStatus.CREATED)
                .body(mapOf("status" to "pending"))
        } catch (e: IllegalArgumentException) {
            val message = e.message ?: "Invalid request"
            if (message.startsWith("Email already exists")) {
                ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mapOf("error" to message))
            } else {
                ResponseEntity.badRequest()
                    .body(mapOf("error" to message))
            }
        }
    }

    @GetMapping("/verify")
    fun verify(@RequestParam token: String): ResponseEntity<Map<String, String>> {
        return try {
            verifyCustomerUseCase.verify(token)
            ResponseEntity.ok(mapOf("status" to "verified"))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Invalid verification token"))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.GONE)
                .body(mapOf("error" to "Verification token has expired"))
        }
    }

    @PostMapping("/reset-password/request")
    fun resetPasswordRequest(@RequestBody request: ResetPasswordRequest): ResponseEntity<Map<String, String>> {
        if (request.email.isNullOrBlank()) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Email is required"))
        }
        resetPasswordRequestUseCase.request(request.email)
        return ResponseEntity.ok(mapOf("message" to "If the email exists, a reset link has been sent"))
    }

    @PostMapping("/reset-password/confirm")
    fun resetPasswordConfirm(@RequestBody request: ResetPasswordConfirmRequest): ResponseEntity<*> {
        if (request.token.isNullOrBlank() || request.newPassword.isNullOrBlank()) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Token and newPassword are required"))
        }
        return try {
            resetPasswordConfirmUseCase.confirm(request.token, request.newPassword)
            ResponseEntity.noContent().build<Any>()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to (e.message ?: "Invalid reset token")))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.GONE)
                .body(mapOf("error" to (e.message ?: "Reset token has expired")))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        return try {
            val result = loginUseCase.login(request.email, request.password)
            ResponseEntity.ok(
                mapOf(
                    "token" to result.token,
                    "refreshToken" to result.token, // mismo JWT como refresh token
                    "email" to result.email,
                    "tenantId" to (result.tenantId ?: "null"),
                    "role" to result.role,
                    "principalType" to result.principalType,
                    "permissions" to result.permissions,
                    "userId" to (result.userId?.toString() ?: "")
                )
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to (e.message ?: "Account is not active")))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid credentials"))
        }
    }

    /**
     * POST /api/v1/auth/refresh
     *
     * Recibe un JWT válido en el body (refreshToken) y emite uno nuevo
     * con expiración extendida a 24h desde ahora.
     */
    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<Map<String, Any>> {
        return try {
            val oldClaims = jwtService.extractClaims(request.refreshToken)
            val email = oldClaims["email"] as? String ?: throw IllegalArgumentException("Invalid token")
            val rol = oldClaims["rol"] as? String ?: ""
            val tenantId = oldClaims["tenantId"] as? Int
            val principalType = oldClaims["principalType"] as? String ?: "user"

            @Suppress("UNCHECKED_CAST")
            val permissions = (oldClaims["permissions"] as? List<String>) ?: emptyList()

            val newToken = jwtService.generateToken(
                email = email,
                rol = rol,
                tenantId = tenantId,
                principalType = principalType,
                permissions = permissions
            )

            ResponseEntity.ok(
                mapOf(
                    "accessToken" to newToken,
                    "refreshToken" to newToken,
                    "expiresIn" to 86400 // 24h en segundos
                )
            )
        } catch (e: JWTVerificationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid or expired token"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to (e.message ?: "Refresh failed")))
        }
    }
}

/**
 * Request DTO for token refresh.
 */
data class RefreshRequest(
    val refreshToken: String
)

/**
 * Response DTO for token refresh.
 */
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long = 86400
)

/**
 * Request DTO for customer registration.
 *
 * `name` and `companyName` are optional (nullable with null defaults).
 * When `name` is null or blank, the use case falls back to the email prefix.
 * When `companyName` is null, it is skipped (stays null in the domain model).
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null,
    val companyName: String? = null
)

/**
 * Request DTO for login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Request DTO for password reset request.
 */
data class ResetPasswordRequest(
    val email: String?
)

/**
 * Request DTO for password reset confirmation.
 */
data class ResetPasswordConfirmRequest(
    val token: String?,
    val newPassword: String?
)
