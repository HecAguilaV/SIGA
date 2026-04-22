package com.siga.auth.controller

import com.siga.auth.entity.Customer
import com.siga.auth.repository.CustomerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller to manage customers (business owners).
 */
@RestController
@RequestMapping("/api/auth/customers")
class CustomerController(
    private val customerRepository: CustomerRepository
) {
    @GetMapping
    fun getAllCustomers(): ResponseEntity<List<Customer>> {
        return ResponseEntity.ok(customerRepository.findAll())
    }

    @GetMapping("/{id}")
    fun getCustomerById(@PathVariable id: Int): ResponseEntity<Customer> {
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
    fun updateCustomer(@PathVariable id: Int, @RequestBody customer: Customer): ResponseEntity<Customer> {
        return if (customerRepository.existsById(id)) {
            customer.id = id
            ResponseEntity.ok(customerRepository.save(customer))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
