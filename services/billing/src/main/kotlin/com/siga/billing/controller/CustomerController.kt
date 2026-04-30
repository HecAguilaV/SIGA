package com.siga.billing.controller

import com.siga.billing.entity.Customer
import com.siga.billing.repository.CustomerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller to manage commercial customers.
 */
@RestController
@RequestMapping("/api/v1/billing/customers")
class CustomerController(
    private val customerRepository: CustomerRepository
) {
    @GetMapping
    fun getAllCustomers(): ResponseEntity<List<Customer>> {
        return ResponseEntity.ok(customerRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getCustomerById(@PathVariable id: UUID): ResponseEntity<Customer> {
        val customer = customerRepository.findById(id)
        return if (customer.isPresent) {
            ResponseEntity.ok(customer.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getCustomerByEmail(@PathVariable email: String): ResponseEntity<Customer> {
        val customer = customerRepository.findByEmail(email)
        return if (customer != null) {
            ResponseEntity.ok(customer)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createCustomer(@RequestBody customer: Customer): ResponseEntity<Customer> {
        return ResponseEntity.ok(customerRepository.save(customer))
    }

    @PutMapping("/{id}")
    fun updateCustomer(@PathVariable id: UUID, @RequestBody customer: Customer): ResponseEntity<Customer> {
        return if (customerRepository.existsById(id)) {
            // ID assignment is handled by the object mapping or should be verified
            ResponseEntity.ok(customerRepository.save(customer))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
