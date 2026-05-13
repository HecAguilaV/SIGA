package com.siga.auth.controller

import com.siga.auth.application.usecase.ManageCustomerUseCase
import com.siga.auth.domain.model.Customer
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage customers (business owners).
 * Now uses ManageCustomerUseCase (hexagonal) instead of directly injecting CustomerRepository.
 */
@RestController
@RequestMapping("/api/v1/auth/customers")
class CustomerController(
    private val manageCustomerUseCase: ManageCustomerUseCase
) {
    @GetMapping
    fun getAllCustomers(): ResponseEntity<List<Customer>> {
        return ResponseEntity.ok(manageCustomerUseCase.findAll())
    }

    @GetMapping("/{id}")
    fun getCustomerById(@PathVariable id: Int): ResponseEntity<Customer> {
        val customer = manageCustomerUseCase.findById(id)
        return if (customer != null) {
            ResponseEntity.ok(customer)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getCustomerByEmail(@PathVariable email: String): ResponseEntity<Customer> {
        val customer = manageCustomerUseCase.findByEmail(email)
        return if (customer != null) {
            ResponseEntity.ok(customer)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCustomer(@RequestBody customer: Customer): ResponseEntity<Customer> {
        return ResponseEntity.ok(manageCustomerUseCase.create(customer))
    }

    @PutMapping("/{id}")
    fun updateCustomer(@PathVariable id: Int, @RequestBody customer: Customer): ResponseEntity<Customer> {
        return try {
            ResponseEntity.ok(manageCustomerUseCase.update(id, customer))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}
