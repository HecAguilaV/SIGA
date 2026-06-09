package com.siga.billing.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.billing.domain.model.Payment
import com.siga.billing.domain.model.PaymentStatus
import com.siga.billing.domain.port.PaymentRepositoryPort
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
 * Web-layer tests for [PaymentController].
 *
 * SECURITY AUDIT FINDINGS:
 * - SECURITY: GET /payments/customer/{customerId} — Potential IDOR: no check that the
 *   authenticated user owns the requested customerId. Any user can query any customer's payments.
 * - SECURITY: POST /payments — No @Valid or input validation on request body.
 * - SECURITY: No DTO layer — returns Payment domain model directly.
 * - SECURITY: No rate limiting on payment creation endpoint (abuse potential).
 * - POSITIVE: Spring validates UUID path params automatically → invalid UUIDs get 400.
 */
@WebMvcTest(PaymentController::class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PaymentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var paymentPort: PaymentRepositoryPort

    private val validPayment = Payment(
        id = UUID.randomUUID(),
        subscriptionId = UUID.randomUUID(),
        customerId = UUID.randomUUID(),
        amount = BigDecimal("15000"),
        paymentMethod = "TRANSBANK_FICTITIOUS",
        status = PaymentStatus.COMPLETED,
        reference = "TXN-001",
        paidAt = Instant.now()
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/payments
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/payments → 200 with empty list")
    fun getAllPayments_returnsEmptyList() {
        // The controller returns emptyList() directly, no port call
        mockMvc.perform(get("/api/v1/billing/payments"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/payments/{id}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/payments/{id} → 200 when payment exists")
    fun getPaymentById_whenFound_returnsPayment() {
        `when`(paymentPort.findById(validPayment.id)).thenReturn(validPayment)

        mockMvc.perform(get("/api/v1/billing/payments/{id}", validPayment.id))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validPayment.id.toString()))
            .andExpect(jsonPath("$.reference").value("TXN-001"))
            .andExpect(jsonPath("$.amount").value(15000))
    }

    @Test
    @DisplayName("GET /api/v1/billing/payments/{id} → 404 when not found")
    fun getPaymentById_whenNotFound_returns404() {
        val unknownId = UUID.randomUUID()
        `when`(paymentPort.findById(unknownId)).thenReturn(null)

        mockMvc.perform(get("/api/v1/billing/payments/{id}", unknownId))
            .andExpect(status().isNotFound)
    }

    // ---------------------------------------------------------------
    // GET /api/v1/billing/payments/customer/{customerId}
    // ---------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/billing/payments/customer/{customerId} → 200 with payments")
    fun getPaymentsByCustomer_returnsList() {
        val customerId = UUID.randomUUID()
        val payments = listOf(validPayment.copy(customerId = customerId))

        `when`(paymentPort.findByCustomerId(customerId)).thenReturn(payments)

        mockMvc.perform(get("/api/v1/billing/payments/customer/{customerId}", customerId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].customerId").value(customerId.toString()))
    }

    // ---------------------------------------------------------------
    // POST /api/v1/billing/payments
    // ---------------------------------------------------------------

    @Test
    @DisplayName("POST /api/v1/billing/payments → 200 with created payment")
    fun createPayment_returnsOk() {
        `when`(paymentPort.save(anyObject())).thenReturn(validPayment)

        mockMvc.perform(post("/api/v1/billing/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validPayment)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(validPayment.id.toString()))
            .andExpect(jsonPath("$.reference").value("TXN-001"))
    }

    // ---------------------------------------------------------------
    // Security: Input validation
    // ---------------------------------------------------------------

    // SECURITY: Invalid UUID in path → 400 (Spring TypeMismatchException).
    @Test
    @DisplayName("GET /api/v1/billing/payments/{id} with invalid UUID → 400")
    fun getPaymentById_withInvalidUUID_returns400() {
        mockMvc.perform(get("/api/v1/billing/payments/{id}", "not-a-uuid"))
            .andExpect(status().isBadRequest)
    }

    // SECURITY: Invalid UUID in customer path → 400
    @Test
    @DisplayName("GET /api/v1/billing/payments/customer/{customerId} with invalid UUID → 400")
    fun getPaymentsByCustomer_withInvalidUUID_returns400() {
        mockMvc.perform(get("/api/v1/billing/payments/customer/{customerId}", "bad-uuid"))
            .andExpect(status().isBadRequest)
    }
}
