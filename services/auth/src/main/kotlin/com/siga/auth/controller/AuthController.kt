package com.siga.auth.controller

import com.siga.auth.application.usecase.LoginUseCase
import com.siga.auth.application.usecase.RegisterCustomerUseCase
import com.siga.auth.application.usecase.VerifyCustomerUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for authentication flows: registration, email verification, and login.
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerCustomerUseCase: RegisterCustomerUseCase,
    private val verifyCustomerUseCase: VerifyCustomerUseCase,
    private val loginUseCase: LoginUseCase
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

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        return try {
            val result = loginUseCase.login(request.email, request.password)
            ResponseEntity.ok(
                mapOf(
                    "token" to result.token,
                    "email" to result.email,
                    "tenantId" to (result.tenantId ?: "null"),
                    "role" to result.role,
                    "principalType" to result.principalType
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
}

/**
 * Request DTO for customer registration.
 */
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val companyName: String
)

/**
 * Request DTO for login.
 */
data class LoginRequest(
    val email: String,
    val password: String
)
