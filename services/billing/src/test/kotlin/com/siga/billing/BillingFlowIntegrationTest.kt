package com.siga.billing

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.billing.domain.model.Plan
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.util.UUID

/**
 * Full HTTP integration test for the billing plan flow.
 *
 * Tests the complete stack: Controller → Adapter → H2
 * via MockMvc. No external dependencies.
 */
@SpringBootTest
@ActiveProfiles("test")
class BillingFlowIntegrationTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun `POST api v1 billing plans creates a plan and returns 200`() {
        val planId = UUID.randomUUID()
        val plan = Plan(
            id = planId,
            name = "HTTP Test Plan",
            description = "Created via HTTP",
            storeLimit = 3,
            userLimit = 5,
            productLimit = 50,
            monthlyPrice = BigDecimal("29.99"),
            yearlyPrice = BigDecimal("299.99"),
            displayOrder = 1,
            isActive = true
        )

        val requestJson = objectMapper.writeValueAsString(plan)

        mockMvc.perform(
            post("/api/v1/billing/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("HTTP Test Plan"))
            .andExpect(jsonPath("$.monthlyPrice").value(29.99))
            .andExpect(jsonPath("$.id").value(planId.toString()))
    }

    @Test
    fun `GET api v1 billing plans returns active plans`() {
        // Create a plan first
        val plan = Plan(
            id = UUID.randomUUID(), name = "List Test Plan",
            description = null, storeLimit = 1, userLimit = 3,
            productLimit = null, monthlyPrice = BigDecimal("9.99"),
            yearlyPrice = null, displayOrder = 0, isActive = true
        )
        val createJson = objectMapper.writeValueAsString(plan)
        mockMvc.perform(
            post("/api/v1/billing/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        ).andExpect(status().isOk)

        mockMvc.perform(get("/api/v1/billing/plans"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `GET api v1 billing plans id returns a plan`() {
        val planId = UUID.randomUUID()
        val plan = Plan(
            id = planId, name = "Get By ID Plan",
            description = "Test", storeLimit = 2, userLimit = 5,
            productLimit = 100, monthlyPrice = BigDecimal("19.99"),
            yearlyPrice = BigDecimal("199.99"), displayOrder = 0, isActive = true
        )
        val createJson = objectMapper.writeValueAsString(plan)
        mockMvc.perform(
            post("/api/v1/billing/plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
        ).andExpect(status().isOk)

        mockMvc.perform(get("/api/v1/billing/plans/{id}", planId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(planId.toString()))
            .andExpect(jsonPath("$.name").value("Get By ID Plan"))
    }

    @Test
    fun `GET api v1 billing plans id returns 404 for non-existent plan`() {
        mockMvc.perform(get("/api/v1/billing/plans/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound)
    }
}
