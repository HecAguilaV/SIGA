package com.siga.sales

import com.siga.sales.domain.model.Sale
import com.siga.sales.domain.model.SaleItem
import com.siga.sales.domain.model.SaleStatus
import com.siga.sales.event.SaleEventProducer
import com.siga.sales.event.SaleEvent
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Full HTTP integration test for the sale flow.
 *
 * Tests the complete stack: Controller → Use Case → Adapters → H2
 * via MockMvc. Kafka is mocked to avoid dependency on a real broker.
 */
class SaleFlowIntegrationTest : BaseSalesIntegrationTest() {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var saleEventProducer: SaleEventProducer

    private lateinit var mockMvc: MockMvc

    init {
        beforeTest {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        }

        describe("Sale full HTTP flow") {

            it("POST /api/v1/sales creates a sale, persists it, and returns 200") {
                val saleId = UUID.randomUUID()
                val storeId = UUID.randomUUID()
                val userId = UUID.randomUUID()

                val sale = Sale(
                    id = saleId, storeId = storeId, userId = userId,
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("150.00"), status = SaleStatus.PENDING,
                    observations = "HTTP flow test"
                )
                val items = listOf(
                    SaleItem(
                        id = UUID.randomUUID(), saleId = saleId,
                        productId = UUID.randomUUID(), quantity = 2,
                        unitPrice = BigDecimal("50.00"), subtotal = BigDecimal("100.00")
                    )
                )

                val requestJson = objectMapper.writeValueAsString(
                    mapOf("sale" to sale, "items" to items)
                )

                mockMvc.perform(
                    post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(saleId.toString()))
                    .andExpect(jsonPath("$.total").value(150.00))
                    .andExpect(jsonPath("$.status").value("PENDING"))

                // SaleEventProducer publish is verified in unit tests (CreateSaleUseCaseTest)
                // and integration tests (StockEventConsumerIntegrationTest)
            }

            it("GET /api/v1/sales/{id} returns a persisted sale") {
                val saleId = UUID.randomUUID()
                val sale = Sale(
                    id = saleId, storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("250.00"), status = SaleStatus.PENDING,
                    observations = "GET test"
                )
                val createJson = objectMapper.writeValueAsString(
                    mapOf("sale" to sale, "items" to emptyList<SaleItem>())
                )
                mockMvc.perform(
                    post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isOk)

                mockMvc.perform(get("/api/v1/sales/{id}", saleId))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(saleId.toString()))
                    .andExpect(jsonPath("$.total").value(250.00))
                    .andExpect(jsonPath("$.observations").value("GET test"))
            }

            it("GET /api/v1/sales returns all sales") {
                mockMvc.perform(get("/api/v1/sales"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
            }

            it("GET /api/v1/sales/{id} returns 404 for non-existent sale") {
                mockMvc.perform(get("/api/v1/sales/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound)
            }

            it("POST /api/v1/sales handles empty items list") {
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("0.00"), status = SaleStatus.PENDING,
                    observations = "Empty items"
                )
                val requestJson = objectMapper.writeValueAsString(
                    mapOf("sale" to sale, "items" to emptyList<SaleItem>())
                )
                mockMvc.perform(
                    post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                ).andExpect(status().isOk)
            }

            it("GET /api/v1/sales/store/{storeId} filters by store") {
                val storeId = UUID.randomUUID()
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = storeId, userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING,
                    observations = null
                )
                val requestJson = objectMapper.writeValueAsString(
                    mapOf("sale" to sale, "items" to emptyList<SaleItem>())
                )
                mockMvc.perform(
                    post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                ).andExpect(status().isOk)

                mockMvc.perform(get("/api/v1/sales/store/{storeId}", storeId))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
            }

            it("GET /api/v1/sales/status/{status} filters by status") {
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING,
                    observations = null
                )
                val requestJson = objectMapper.writeValueAsString(
                    mapOf("sale" to sale, "items" to emptyList<SaleItem>())
                )
                mockMvc.perform(
                    post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                ).andExpect(status().isOk)

                mockMvc.perform(get("/api/v1/sales/status/{status}", "PENDING"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
            }

            it("GET /api/v1/sales/status/{status} returns 400 for invalid status") {
                mockMvc.perform(get("/api/v1/sales/status/{status}", "INVALID"))
                    .andExpect(status().isBadRequest)
            }

            it("PUT /api/v1/sales/{id} updates a sale") {
                val saleId = UUID.randomUUID()
                val sale = Sale(
                    id = saleId, storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING,
                    observations = "Before update"
                )
                val createJson = objectMapper.writeValueAsString(
                    mapOf("sale" to sale, "items" to emptyList<SaleItem>())
                )
                mockMvc.perform(
                    post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isOk)

                val updatedSale = sale.copy(
                    total = BigDecimal("200.00"), status = SaleStatus.COMPLETED,
                    observations = "After update"
                )
                val updateJson = objectMapper.writeValueAsString(updatedSale)

                mockMvc.perform(
                    put("/api/v1/sales/{id}", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.total").value(200.00))
                    .andExpect(jsonPath("$.status").value("COMPLETED"))
                    .andExpect(jsonPath("$.observations").value("After update"))
            }

            it("PUT /api/v1/sales/{id} returns 404 for non-existent sale") {
                val sale = Sale(
                    id = UUID.randomUUID(), storeId = UUID.randomUUID(), userId = UUID.randomUUID(),
                    commercialUserId = null, createdAt = Instant.now(),
                    total = BigDecimal("100.00"), status = SaleStatus.PENDING,
                    observations = null
                )
                val updateJson = objectMapper.writeValueAsString(sale)

                mockMvc.perform(
                    put("/api/v1/sales/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                ).andExpect(status().isNotFound)
            }
        }
    }
}
