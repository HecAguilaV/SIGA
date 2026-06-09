package com.siga.billing.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.billing.domain.model.Plan
import com.siga.billing.domain.port.PlanRepositoryPort
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
import java.math.BigDecimal
import java.util.UUID

/**
 * Web-layer tests for [PlanController].
 *
 * Uses @WebMvcTest to only load the web slice.
 * Security filters are disabled — the controller doesn't enforce auth at this level.
 *
 * SECURITY AUDIT FINDINGS:
 * - No @Valid or input validation on POST /plans body — accepts any Plan JSON without field-level checks
 * - No DTO layer — returns the domain model Plan directly (acceptable for internal services)
 * - No custom error handler — relies on Spring's default 400 for deserialization errors
 * - Positive: UUID path params are validated by Spring (invalid UUID → 400)
 * - Positive: PUT and DELETE return 501, which is safe
 */
@WebMvcTest(PlanController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PlanControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var planPort: PlanRepositoryPort

    private val validPlan = Plan(
        id = UUID.randomUUID(),
        name = "Basic Plan",
        description = "Basic subscription tier",
        storeLimit = 1,
        userLimit = 3,
        productLimit = 50,
        monthlyPrice = BigDecimal("29.99"),
        yearlyPrice = BigDecimal("299.99"),
        displayOrder = 1,
        isActive = true
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/plans
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/plans → 200 with list of active plans")
    fun getAllPlans_returnsActivePlans() {
        `when`(planPort.findByIsActiveTrue()).thenReturn(listOf(validPlan))

        mockMvc.perform(get("/api/v1/billing/plans"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].id").value(validPlan.id.toString()))
            .andExpect(jsonPath("$[0].name").value("Basic Plan"))
            .andExpect(jsonPath("$[0].monthlyPrice").value(29.99))
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/plans/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/plans/{id} → 200 when plan exists")
    fun getPlanById_whenFound_returnsPlan() {
        `when`(planPort.findById(validPlan.id)).thenReturn(validPlan)

        mockMvc.perform(get("/api/v1/billing/plans/{id}", validPlan.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validPlan.id.toString()))
            .andExpect(jsonPath("$.name").value("Basic Plan"))
            .andExpect(jsonPath("$.storeLimit").value(1))
    }

    @Test
    @DisplayName("GET /api/v1/billing/plans/{id} → 404 when plan not found")
    fun getPlanById_whenNotFound_returns404() {
        val unknownId = UUID.randomUUID()
        `when`(planPort.findById(unknownId)).thenReturn(null)

        mockMvc.perform(get("/api/v1/billing/plans/{id}", unknownId))
            .andExpect(status().isNotFound)
    }

    // ---------------------------------------------------------------
    // POST /api/v1/billing/plans
    // ---------------------------------------------------------------

    @Test
    @DisplayName("POST /api/v1/billing/plans → 200 with created plan")
    fun createPlan_returnsOk() {
        `when`(planPort.save(anyObject())).thenReturn(validPlan)

        mockMvc.perform(post("/api/v1/billing/plans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validPlan)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validPlan.id.toString()))
            .andExpect(jsonPath("$.name").value("Basic Plan"))
    }

    // ---------------------------------------------------------------
    // PUT /api/v1/billing/plans/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/v1/billing/plans/{id} → 501 Not Implemented")
    fun updatePlan_returns501() {
        mockMvc.perform(put("/api/v1/billing/plans/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validPlan)))
            .andExpect(status().isNotImplemented)
    }

    // ---------------------------------------------------------------
    // DELETE /api/v1/billing/plans/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/v1/billing/plans/{id} → 501 Not Implemented")
    fun deletePlan_returns501() {
        mockMvc.perform(delete("/api/v1/billing/plans/{id}", UUID.randomUUID()))
            .andExpect(status().isNotImplemented)
    }

    // ---------------------------------------------------------------
    // Security: Input validation
    // ---------------------------------------------------------------

    // SECURITY: Invalid UUID in path → 400. Spring handles this via TypeMismatchException.
    @Test
    @DisplayName("GET /api/v1/billing/plans/{id} with invalid UUID → 400")
    fun getPlanById_withInvalidUUID_returns400() {
        mockMvc.perform(get("/api/v1/billing/plans/{id}", "not-a-valid-uuid"))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Empty body → 400. Spring rejects unreadable body.
    @Test
    @DisplayName("POST /api/v1/billing/plans with empty body → 400")
    fun createPlan_withEmptyBody_returns400() {
        mockMvc.perform(post("/api/v1/billing/plans")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Empty JSON object → 400 (missing required non-null fields).
    // No domain validation — relies entirely on Jackson deserialization.
    @Test
    @DisplayName("POST /api/v1/billing/plans with empty JSON object → 400")
    fun createPlan_withEmptyJson_returns400() {
        mockMvc.perform(post("/api/v1/billing/plans")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest)
    }
}
