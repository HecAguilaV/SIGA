package com.siga.auth.controller

import com.siga.auth.application.usecase.RegisterCustomerUseCase
import com.siga.auth.application.usecase.VerifyCustomerUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller for authentication flows: registration and email verification.
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerCustomerUseCase: RegisterCustomerUseCase,
    private val verifyCustomerUseCase: VerifyCustomerUseCase
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
