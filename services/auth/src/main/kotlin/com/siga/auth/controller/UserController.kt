package com.siga.auth.controller

import com.siga.auth.entity.User
import com.siga.auth.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage SaaS users (employees).
 */
@RestController
@RequestMapping("/api/auth/users")
class UserController(
    private val userRepository: UserRepository
) {
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Int): ResponseEntity<User> {
        val user = userRepository.findById(id)
        return if (user.isPresent) {
            ResponseEntity.ok(user.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = userRepository.findByEmail(email)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userRepository.save(user))
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Int, @RequestBody user: User): ResponseEntity<User> {
        return if (userRepository.existsById(id)) {
            user.id = id
            ResponseEntity.ok(userRepository.save(user))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
