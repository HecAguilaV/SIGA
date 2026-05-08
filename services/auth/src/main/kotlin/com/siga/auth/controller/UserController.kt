package com.siga.auth.controller

import com.siga.auth.application.usecase.ManageUserUseCase
import com.siga.auth.domain.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage SaaS users (employees).
 * Now uses ManageUserUseCase (hexagonal) instead of directly injecting UserRepository.
 */
@RestController
@RequestMapping("/api/v1/auth/users")
class UserController(
    private val manageUserUseCase: ManageUserUseCase
) {
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(manageUserUseCase.findAll())
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = manageUserUseCase.findById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = manageUserUseCase.findByEmail(email)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.status(201).body(manageUserUseCase.create(user))
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user: User): ResponseEntity<User> {
        return try {
            ResponseEntity.ok(manageUserUseCase.update(id, user))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}
