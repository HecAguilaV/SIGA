package com.siga.billing.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.billing.application.usecase.ManageSubscriptionUseCase
import com.siga.billing.domain.model.*
import jakarta.servlet.ServletException
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertThrows
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
import java.time.Instant
import java.util.UUID

/**
 * Web-layer tests for [SubscriptionController].
 *
 * Uses @WebMvcTest to only load the web slice + mocks the [ManageSubscriptionUseCase].
 *
 * SECURITY AUDIT FINDINGS:
 * - SECURITY: GET /customer/{customerId} and /customer/{customerId}/active — Potential IDOR:
 *   no ownership check. Any authenticated user can query another customer's subscriptions.
 * - SECURITY: @RequestParam amount is parsed via BigDecimal(amount) without try-catch.
 *   Invalid (non-numeric) amount → NumberFormatException → 500 with stack trace leak.
 * - SECURITY: No @Valid or input validation on POST subscription body.
 * - SECURITY: PUT returns 501 when found or 404 when not found — ok, but the existence
 *   check via getSubscriptionById is unnecessary.
 * - POSITIVE: Missing amount param → 400 (Spring handles required param).
 * - POSITIVE: Invalid UUID → 400 (Spring TypeMismatchException).
 */
@WebMvcTest(SubscriptionController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class SubscriptionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var manageSubscriptionUseCase: ManageSubscriptionUseCase

    private val validSubscription = Subscription(
        id = UUID.randomUUID(),
        customerId = UUID.randomUUID(),
        planId = UUID.randomUUID(),
        period = BillingPeriod.MONTHLY,
        status = SubscriptionStatus.ACTIVE,
        startsAt = Instant.now(),
        endsAt = null
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/subscriptions
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/subscriptions → 200 with empty list")
    fun getAllSubscriptions_returnsEmptyList() {
        mockMvc.perform(get("/api/v1/billing/subscriptions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/subscriptions/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/subscriptions/{id} → 200 when found")
    fun getSubscriptionById_whenFound_returnsSubscription() {
        `when`(manageSubscriptionUseCase.getSubscriptionById(validSubscription.id))
            .thenReturn(validSubscription)

        mockMvc.perform(get("/api/v1/billing/subscriptions/{id}", validSubscription.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validSubscription.id.toString()))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
    }

    @Test
    @DisplayName("GET /api/v1/billing/subscriptions/{id} → 404 when not found")
    fun getSubscriptionById_whenNotFound_returns404() {
        val unknownId = UUID.randomUUID()
        `when`(manageSubscriptionUseCase.getSubscriptionById(unknownId)).thenReturn(null)

        mockMvc.perform(get("/api/v1/billing/subscriptions/{id}", unknownId))
            .andExpect(status().isNotFound)
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/subscriptions/customer/{customerId}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/subscriptions/customer/{customerId} → 200 with list")
    fun getSubscriptionsByCustomer_returnsList() {
        val customerId = UUID.randomUUID()
        val subscriptions = listOf(validSubscription.copy(customerId = customerId))

        `when`(manageSubscriptionUseCase.getSubscriptionsByCustomer(customerId))
            .thenReturn(subscriptions)

        mockMvc.perform(
            get("/api/v1/billing/subscriptions/customer/{customerId}", customerId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/subscriptions/customer/{customerId}/active
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/subscriptions/customer/{customerId}/active → 200 with active")
    fun getActiveSubscriptions_returnsActiveOnly() {
        val customerId = UUID.randomUUID()
        val activeSubscriptions = listOf(validSubscription.copy(customerId = customerId))

        `when`(manageSubscriptionUseCase.getActiveSubscriptions(customerId))
            .thenReturn(activeSubscriptions)

        mockMvc.perform(
            get("/api/v1/billing/subscriptions/customer/{customerId}/active", customerId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].status").value("ACTIVE"))
    }

    // ---------------------------------------------------------------
    // POST /api/v1/billing/subscriptions?amount=
    // ---------------------------------------------------------------

    @Test
    @DisplayName("POST /api/v1/billing/subscriptions?amount= → 201 on success")
    fun createSubscription_whenPaymentSuccess_returns201() {
        val amount = "15000"
        val paymentResponse = PaymentResponse(
            success = true,
            transactionId = "TXN-001",
            responseCode = "0",
            message = "Approved"
        )

        `when`(manageSubscriptionUseCase.createSubscriptionWithPayment(anyObject(), anyObject()))
            .thenReturn(Pair(validSubscription, paymentResponse))

        mockMvc.perform(post("/api/v1/billing/subscriptions")
            .param("amount", amount)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validSubscription)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(validSubscription.id.toString()))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
    }

    @Test
    @DisplayName("POST /api/v1/billing/subscriptions?amount= → 402 when payment fails")
    fun createSubscription_whenPaymentFails_returns402() {
        val amount = "15000"
        val failedResponse = PaymentResponse(
            success = false,
            transactionId = "FAILED-001",
            responseCode = "1",
            message = "Insufficient funds"
        )

        `when`(manageSubscriptionUseCase.createSubscriptionWithPayment(anyObject(), anyObject()))
            .thenReturn(Pair(validSubscription, failedResponse))

        mockMvc.perform(post("/api/v1/billing/subscriptions")
            .param("amount", amount)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validSubscription)))
            .andExpect(status().isPaymentRequired)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Insufficient funds"))
    }

    // ---------------------------------------------------------------
    // PUT /api/v1/billing/subscriptions/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/v1/billing/subscriptions/{id} → 501 Not Implemented")
    fun updateSubscription_returns501() {
        // Controller calls getSubscriptionById first; if found, returns 501
        val subId = UUID.randomUUID()
        `when`(manageSubscriptionUseCase.getSubscriptionById(subId))
            .thenReturn(validSubscription.copy(id = subId))

        mockMvc.perform(put("/api/v1/billing/subscriptions/{id}", subId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validSubscription)))
            .andExpect(status().isNotImplemented)
    }

    // ---------------------------------------------------------------
    // Security: Input validation
    // ---------------------------------------------------------------

    // SECURITY: Missing required @RequestParam amount → 400 (Spring handles).
    @Test
    @DisplayName("POST /api/v1/billing/subscriptions without amount → 400")
    fun createSubscription_withoutAmount_returns400() {
        mockMvc.perform(post("/api/v1/billing/subscriptions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validSubscription)))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Non-numeric amount causes NumberFormatException → 500 with stack trace.
    // The controller parses amount via BigDecimal(amount) without validation or try-catch.
    // BigDecimal(amount) throws NumberFormatException inside the controller BEFORE
    // the use case is ever called. Spring MVC wraps it in ServletException since no
    // @ExceptionHandler is configured — this means the user gets a 500 instead of a
    // properly validated 400, potentially leaking stack traces.
    @Test
    @DisplayName("POST /api/v1/billing/subscriptions with non-numeric amount → ServletException")
    fun createSubscription_withInvalidAmount_throwsException() {
        val amount = "not-a-number"

        val exception = org.junit.jupiter.api.Assertions.assertThrows(ServletException::class.java) {
            mockMvc.perform(post("/api/v1/billing/subscriptions")
                .param("amount", amount)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSubscription)))
        }

        assertInstanceOf(NumberFormatException::class.java, exception.cause,
            "Should wrap NumberFormatException from BigDecimal parsing")
    }

    // SECURITY: Invalid subscription UUID → 400.
    @Test
    @DisplayName("GET /api/v1/billing/subscriptions/{id} with invalid UUID → 400")
    fun getSubscriptionById_withInvalidUUID_returns400() {
        mockMvc.perform(get("/api/v1/billing/subscriptions/{id}", "bad-uuid"))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Invalid customer UUID → 400.
    @Test
    @DisplayName("GET /api/v1/billing/subscriptions/customer/{customerId} with invalid UUID → 400")
    fun getSubscriptionsByCustomer_withInvalidUUID_returns400() {
        mockMvc.perform(
            get("/api/v1/billing/subscriptions/customer/{customerId}", "bad-uuid")
        )
            .andExpect(status().isBadRequest)
    }
}
