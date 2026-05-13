package com.siga.auth

import com.siga.auth.domain.model.Customer
import com.siga.auth.domain.model.User
import com.siga.auth.domain.model.UserRole
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

/**
 * Full HTTP integration test for the auth flow.
 *
 * Tests the complete stack: Controller → UseCase → Adapter → H2
 * via MockMvc using BaseIntegrationTest (addFilters = false).
 */
class AuthFlowIntegrationTest : BaseIntegrationTest() {

    // ===== User Endpoints (/api/v1/auth/users) =====

    @Test
    fun `POST api v1 auth users creates a user and returns 201`() {
        val user = User(
            id = UUID.randomUUID(),
            email = "http_user_${UUID.randomUUID()}@test.com",
            passwordHash = "http_hash_123",
            firstName = "HTTP",
            lastName = "Test User",
            role = UserRole.ADMINISTRATOR,
            isActive = true
        )

        val requestJson = objectMapper.writeValueAsString(user)

        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.firstName").value("HTTP"))
            .andExpect(jsonPath("$.lastName").value("Test User"))
            .andExpect(jsonPath("$.email").value(user.email))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `GET api v1 auth users returns users list`() {
        // Create a user first
        val user = User(
            id = UUID.randomUUID(),
            email = "list_user_${UUID.randomUUID()}@test.com",
            passwordHash = "hash123",
            firstName = "List",
            lastName = "Test",
            role = UserRole.OPERATOR,
            isActive = true
        )
        val createJson = objectMapper.writeValueAsString(user)
        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        ).andExpect(status().isCreated)

        mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `GET api v1 auth users id returns a user`() {
        // First create a user
        val createEmail = "getbyid_${UUID.randomUUID()}@test.com"
        val userToCreate = User(
            id = UUID.randomUUID(),
            email = createEmail,
            passwordHash = "hash123",
            firstName = "GetById",
            lastName = "Test",
            role = UserRole.CASHIER,
            isActive = true
        )
        val createJson = objectMapper.writeValueAsString(userToCreate)

        val createResult = mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        )
            .andExpect(status().isCreated)
            .andReturn()

        val createdUser = objectMapper.readTree(createResult.response.contentAsString)
        val userId = UUID.fromString(createdUser.get("id").asText())

        mockMvc.perform(get("/api/v1/auth/users/{id}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.firstName").value("GetById"))
            .andExpect(jsonPath("$.email").value(createEmail))
    }

    @Test
    fun `GET api v1 auth users email email returns user by email`() {
        val email = "getbyemail_${UUID.randomUUID()}@test.com"
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            passwordHash = "hash123",
            firstName = "GetByEmail",
            lastName = "Test",
            role = UserRole.OPERATOR,
            isActive = true
        )
        val createJson = objectMapper.writeValueAsString(user)
        mockMvc.perform(
            post("/api/v1/auth/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        ).andExpect(status().isCreated)

        mockMvc.perform(get("/api/v1/auth/users/email/{email}", email))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.firstName").value("GetByEmail"))
    }

    @Test
    fun `GET api v1 auth users id returns 404 for non-existent id`() {
        mockMvc.perform(get("/api/v1/auth/users/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `GET api v1 auth users email email returns 404 for non-existent email`() {
        mockMvc.perform(get("/api/v1/auth/users/email/{email}", "nonexistent_${UUID.randomUUID()}@test.com"))
            .andExpect(status().isNotFound)
    }

    // ===== Customer Endpoints (/api/v1/auth/customers) =====

    @Test
    fun `POST api auth customers creates a customer and returns 200`() {
        val customer = Customer(
            id = null,
            email = "http_customer_${UUID.randomUUID()}@test.com",
            passwordHash = "customer_hash_123",
            name = "HTTP Customer",
            lastName = "Test",
            companyName = "Test Corp",
            isActive = true
        )

        val requestJson = objectMapper.writeValueAsString(customer)

        mockMvc.perform(
            post("/api/v1/auth/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("HTTP Customer"))
            .andExpect(jsonPath("$.lastName").value("Test"))
            .andExpect(jsonPath("$.email").value(customer.email))
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `GET api auth customers returns customers list`() {
        // Create a customer first
        val customer = Customer(
            id = null,
            email = "list_customer_${UUID.randomUUID()}@test.com",
            passwordHash = "hash123",
            name = "List Customer",
            isActive = true
        )
        val createJson = objectMapper.writeValueAsString(customer)
        mockMvc.perform(
            post("/api/v1/auth/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        ).andExpect(status().isOk)

        mockMvc.perform(get("/api/v1/auth/customers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `GET api auth customers id returns a customer`() {
        // First create a customer
        val createEmail = "cust_getbyid_${UUID.randomUUID()}@test.com"
        val customerToCreate = Customer(
            id = null,
            email = createEmail,
            passwordHash = "hash123",
            name = "CustomerGetById",
            isActive = true
        )
        val createJson = objectMapper.writeValueAsString(customerToCreate)

        val createResult = mockMvc.perform(
            post("/api/v1/auth/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        )
            .andExpect(status().isOk)
            .andReturn()

        val createdCustomer = objectMapper.readTree(createResult.response.contentAsString)
        val customerId = createdCustomer.get("id").asInt()

        mockMvc.perform(get("/api/v1/auth/customers/{id}", customerId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customerId))
            .andExpect(jsonPath("$.name").value("CustomerGetById"))
            .andExpect(jsonPath("$.email").value(createEmail))
    }

    @Test
    fun `GET api auth customers id returns 404 for non-existent id`() {
        mockMvc.perform(get("/api/v1/auth/customers/{id}", 999999))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `GET api auth customers email email returns customer by email`() {
        val email = "cust_getbyemail_${UUID.randomUUID()}@test.com"
        val customer = Customer(
            id = null,
            email = email,
            passwordHash = "hash123",
            name = "CustGetByEmail",
            isActive = true
        )
        val createJson = objectMapper.writeValueAsString(customer)
        mockMvc.perform(
            post("/api/v1/auth/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        ).andExpect(status().isOk)

        mockMvc.perform(get("/api/v1/auth/customers/email/{email}", email))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.name").value("CustGetByEmail"))
    }
}
