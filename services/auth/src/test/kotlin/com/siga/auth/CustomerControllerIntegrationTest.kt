package com.siga.auth

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.port.CustomerRepositoryPort
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

/**
 * Integration test for CustomerController.
 *
 * Covers all endpoints to ensure at least 80% instruction coverage.
 */
class CustomerControllerIntegrationTest @Autowired constructor(
    private val customerRepositoryPort: CustomerRepositoryPort
) : BaseIntegrationTest() {

    private fun createCustomerJson(
        name: String = "Test",
        email: String = "customer_${UUID.randomUUID()}@test.com",
        passwordHash: String = "hash",
        companyName: String? = null
    ): String {
        val customer = Customer(
            name = name,
            email = email,
            passwordHash = passwordHash,
            companyName = companyName
        )
        return objectMapper.writeValueAsString(customer)
    }

    @Test
    fun `GET customers returns all customers`() {
        customerRepositoryPort.save(
            Customer(name = "Test", companyName = "Test One", email = "one@test.com", passwordHash = "hash1")
        )
        customerRepositoryPort.save(
            Customer(name = "Test", companyName = "Test Two", email = "two@test.com", passwordHash = "hash2")
        )

        mockMvc.perform(get("/api/v1/auth/customers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[?(@.companyName == 'Test One')]").exists())
            .andExpect(jsonPath("$[?(@.companyName == 'Test Two')]").exists())
    }

    @Test
    fun `GET customer by id returns customer when found`() {
        val saved = customerRepositoryPort.save(
            Customer(name = "Test", companyName = "Find Me", email = "find@test.com", passwordHash = "hash")
        )

        mockMvc.perform(get("/api/v1/auth/customers/${saved.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.companyName").value("Find Me"))
            .andExpect(jsonPath("$.email").value("find@test.com"))
    }

    @Test
    fun `GET customer by id returns 404 when not found`() {
        mockMvc.perform(get("/api/v1/auth/customers/99999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `GET customer by email returns customer when found`() {
        customerRepositoryPort.save(
            Customer(name = "Test", companyName = "By Email", email = "byemail@test.com", passwordHash = "hash")
        )

        mockMvc.perform(get("/api/v1/auth/customers/email/byemail@test.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.companyName").value("By Email"))
    }

    @Test
    fun `GET customer by email returns 404 when not found`() {
        mockMvc.perform(get("/api/v1/auth/customers/email/nobody@test.com"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `POST customer creates and returns customer`() {
        val body = createCustomerJson(
            email = "post_new_${UUID.randomUUID()}@test.com",
            companyName = "New Co"
        )

        mockMvc.perform(
            post("/api/v1/auth/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.companyName").value("New Co"))
    }

    @Test
    fun `PUT customer updates and returns customer`() {
        val saved = customerRepositoryPort.save(
            Customer(name = "Test", companyName = "Before", email = "before@test.com", passwordHash = "hash")
        )

        val body = createCustomerJson(
            email = "before@test.com",
            companyName = "After"
        )

        mockMvc.perform(
            put("/api/v1/auth/customers/${saved.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.companyName").value("After"))
    }

    @Test
    fun `PUT customer returns 404 when not found`() {
        val body = createCustomerJson(
            email = "ghost@test.com",
            companyName = "Ghost"
        )

        mockMvc.perform(
            put("/api/v1/auth/customers/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `POST customer with duplicate email returns 400`() {
        customerRepositoryPort.save(
            Customer(name = "Test", email = "dup@test.com", passwordHash = "hash")
        )

        val body = createCustomerJson(
            email = "dup@test.com",
            companyName = "Duplicate"
        )

        mockMvc.perform(
            post("/api/v1/auth/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        )
            .andExpect(status().isBadRequest)
    }
}
