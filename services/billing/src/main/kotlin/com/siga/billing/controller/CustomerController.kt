package com.siga.billing.controller

import com.siga.billing.domain.model.Customer
import com.siga.billing.domain.port.CustomerRepositoryPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage commercial customers.
 * Uses CustomerRepositoryPort (Hexagonal) for persistence.
 */
@RestController
@RequestMapping("/api/v1/billing/customers")
class CustomerController(
    private val customerPort: CustomerRepositoryPort
) {
    @GetMapping
    fun getAllCustomers(): ResponseEntity<List<Customer>> {
        // Note: CustomerRepositoryPort doesn't have findAll, this is a simplification.
        // In real Hexagonal, you'd add findAll to the Port or use a specific Use Case.
        return ResponseEntity.ok(emptyList()) 
    }

    @GetMapping("/{id}")
    fun getCustomerById(@PathVariable id: UUID): ResponseEntity<Customer> {
        val customer = customerPort.findById(id)
        return if (customer != null) {
            ResponseEntity.ok(customer)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getCustomerByEmail(@PathVariable email: String): ResponseEntity<Customer> {
        val customer = customerPort.findByEmail(email)
        return if (customer != null) {
            ResponseEntity.ok(customer)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCustomer(@RequestBody customer: Customer): ResponseEntity<Customer> {
        return ResponseEntity.ok(customerPort.save(customer))
    }

    @PutMapping("/{id}")
    fun updateCustomer(@PathVariable id: UUID, @RequestBody customer: Customer): ResponseEntity<Customer> {
        // In Hexagonal, updates often go through a Use Case.
        // For now, we assume the port handles the update by ID.
        return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED).build()
    }
}
