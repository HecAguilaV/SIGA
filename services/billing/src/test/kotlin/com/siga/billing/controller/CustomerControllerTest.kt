package com.siga.billing.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.billing.domain.model.Customer
import com.siga.billing.domain.port.CustomerRepositoryPort
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant
import java.util.UUID

/**
 * Web-layer tests for [CustomerController].
 *
 * SECURITY AUDIT FINDINGS:
 * - SECURITY: POST /customers — No @Valid or input validation. Accepts any JSON that
 *   Jackson can deserialize. Missing non-null fields cause deserialization errors.
 * - SECURITY: GET /email/{email} — No email format validation. Any string is accepted
 *   as a path variable and passed to the port. Should validate email format server-side.
 * - SECURITY: No DTO layer — full Customer domain model is returned, including internal
 *   fields like taxId, isOnTrial, trialStartAt, planId. Consider a response DTO.
 * - SECURITY: No rate limiting on customer creation (abuse potential).
 * - POSITIVE: Invalid UUID → 400 (Spring handles).
 * - POSITIVE: PUT returns 501 (safe default).
 */
@WebMvcTest(CustomerController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var customerPort: CustomerRepositoryPort

    private val validCustomer = Customer(
        id = UUID.randomUUID(),
        email = "customer@example.com",
        name = "Juan",
        lastName = "Pérez",
        taxId = "76.123.456-7",
        phoneNumber = "+56912345678",
        companyName = "Test Corp",
        isActive = true,
        isOnTrial = false,
        trialStartAt = null,
        trialEndAt = null,
        role = "ADMIN",
        planId = UUID.randomUUID(),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/customers
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/customers → 200 with empty list")
    fun getAllCustomers_returnsEmptyList() {
        mockMvc.perform(get("/api/v1/billing/customers"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/customers/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/customers/{id} → 200 when found")
    fun getCustomerById_whenFound_returnsCustomer() {
        `when`(customerPort.findById(validCustomer.id)).thenReturn(validCustomer)

        mockMvc.perform(get("/api/v1/billing/customers/{id}", validCustomer.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validCustomer.id.toString()))
            .andExpect(jsonPath("$.email").value("customer@example.com"))
            .andExpect(jsonPath("$.name").value("Juan"))
    }

    @Test
    @DisplayName("GET /api/v1/billing/customers/{id} → 404 when not found")
    fun getCustomerById_whenNotFound_returns404() {
        val unknownId = UUID.randomUUID()
        `when`(customerPort.findById(unknownId)).thenReturn(null)

        mockMvc.perform(get("/api/v1/billing/customers/{id}", unknownId))
            .andExpect(status().isNotFound)
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/customers/email/{email}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/customers/email/{email} → 200 when found")
    fun getCustomerByEmail_whenFound_returnsCustomer() {
        val email = "customer@example.com"
        `when`(customerPort.findByEmail(email)).thenReturn(validCustomer)

        mockMvc.perform(get("/api/v1/billing/customers/email/{email}", email))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.name").value("Juan"))
    }

    @Test
    @DisplayName("GET /api/v1/billing/customers/email/{email} → 404 when not found")
    fun getCustomerByEmail_whenNotFound_returns404() {
        val email = "nonexistent@example.com"
        `when`(customerPort.findByEmail(email)).thenReturn(null)

        mockMvc.perform(get("/api/v1/billing/customers/email/{email}", email))
            .andExpect(status().isNotFound)
    }

    // ---------------------------------------------------------------
    // POST /api/v1/billing/customers
    // ---------------------------------------------------------------

    @Test
    @DisplayName("POST /api/v1/billing/customers → 200 with created customer")
    fun createCustomer_returnsOk() {
        `when`(customerPort.save(anyObject())).thenReturn(validCustomer)

        mockMvc.perform(post("/api/v1/billing/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validCustomer.id.toString()))
            .andExpect(jsonPath("$.email").value("customer@example.com"))
    }

    // ---------------------------------------------------------------
    // PUT /api/v1/billing/customers/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/v1/billing/customers/{id} → 501 Not Implemented")
    fun updateCustomer_returns501() {
        mockMvc.perform(put("/api/v1/billing/customers/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validCustomer)))
            .andExpect(status().isNotImplemented)
    }

    // ---------------------------------------------------------------
    // Security: Input validation
    // ---------------------------------------------------------------

    // SECURITY: Invalid UUID in path → 400.
    @Test
    @DisplayName("GET /api/v1/billing/customers/{id} with invalid UUID → 400")
    fun getCustomerById_withInvalidUUID_returns400() {
        mockMvc.perform(get("/api/v1/billing/customers/{id}", "bad-uuid"))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Empty body on POST → 400.
    @Test
    @DisplayName("POST /api/v1/billing/customers with empty body → 400")
    fun createCustomer_withEmptyBody_returns400() {
        mockMvc.perform(post("/api/v1/billing/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Empty JSON object → 400 (missing required non-null fields).
    @Test
    @DisplayName("POST /api/v1/billing/customers with empty JSON → 400")
    fun createCustomer_withEmptyJson_returns400() {
        mockMvc.perform(post("/api/v1/billing/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: No email format validation on /email/{email} — any string is accepted.
    // The endpoint will pass "not-an-email" directly to the port which returns 404.
    @Test
    @DisplayName("GET /api/v1/billing/customers/email/{email} with invalid email format → 404 (no validation)")
    fun getCustomerByEmail_withInvalidFormat_returns404() {
        val badEmail = "this-is-not-an-email"
        `when`(customerPort.findByEmail(badEmail)).thenReturn(null)

        mockMvc.perform(get("/api/v1/billing/customers/email/{email}", badEmail))
            .andExpect(status().isNotFound)
        // SECURITY: This should ideally be 400 with a validation error.
        // There is no email format validation on this endpoint.
    }
}
