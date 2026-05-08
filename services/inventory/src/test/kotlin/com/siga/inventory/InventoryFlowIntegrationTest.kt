package com.siga.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.inventory.entity.Product
import com.siga.inventory.event.StockEventProducer
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
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
 * Full HTTP integration test for the inventory product flow.
 *
 * Tests the complete stack: Controller → Repository → H2
 * via MockMvc. Kafka producer is mocked to avoid dependency on a real broker.
 */
@SpringBootTest
@ActiveProfiles("test")
class InventoryFlowIntegrationTest : DescribeSpec() {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    private lateinit var mockMvc: MockMvc

    init {
        extension(SpringExtension())

        beforeTest {
            mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        }

        describe("Inventory product HTTP flow") {

            it("POST /api/v1/inventory/products creates a product and returns 201") {
                val productId = UUID.randomUUID()
                val product = Product(
                    id = productId,
                    name = "Test Laptop",
                    description = "A test laptop",
                    categoryId = UUID.randomUUID(),
                    barcode = "INV-FLOW-001",
                    unitPrice = BigDecimal("1200.00"),
                    isActive = true,
                    commercialUserId = UUID.randomUUID(),
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )

                val requestJson = objectMapper.writeValueAsString(product)

                mockMvc.perform(
                    post("/api/v1/inventory/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.name").value("Test Laptop"))
                    .andExpect(jsonPath("$.barcode").value("INV-FLOW-001"))
                    .andExpect(jsonPath("$.unitPrice").value(1200.00))
                    .andExpect(jsonPath("$.id").isNotEmpty)
            }

            it("GET /api/v1/inventory/products returns all products") {
                // Create a product first
                val product = Product(
                    id = UUID.randomUUID(), name = "List Test", description = null,
                    categoryId = null, barcode = "LIST-001",
                    unitPrice = BigDecimal("50.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val createJson = objectMapper.writeValueAsString(product)
                mockMvc.perform(
                    post("/api/v1/inventory/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isCreated)

                mockMvc.perform(get("/api/v1/inventory/products"))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$").isArray)
            }

            it("GET /api/v1/inventory/products/{id} returns a persisted product") {
                val productId = UUID.randomUUID()
                val product = Product(
                    id = productId, name = "Get By ID", description = "Test",
                    categoryId = UUID.randomUUID(), barcode = "GET-ID-001",
                    unitPrice = BigDecimal("300.00"), isActive = true,
                    commercialUserId = UUID.randomUUID(), createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val createJson = objectMapper.writeValueAsString(product)
                mockMvc.perform(
                    post("/api/v1/inventory/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isCreated)

                mockMvc.perform(get("/api/v1/inventory/products/{id}", productId))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.name").value("Get By ID"))
                    .andExpect(jsonPath("$.barcode").value("GET-ID-001"))
            }

            it("GET /api/v1/inventory/products/{id} returns 404 for non-existent product") {
                mockMvc.perform(get("/api/v1/inventory/products/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound)
            }

            it("PUT /api/v1/inventory/products/{id} updates a product") {
                val productId = UUID.randomUUID()
                val product = Product(
                    id = productId, name = "Before Update", description = "Original",
                    categoryId = null, barcode = "UPD-001",
                    unitPrice = BigDecimal("100.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val createJson = objectMapper.writeValueAsString(product)
                mockMvc.perform(
                    post("/api/v1/inventory/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isCreated)

                val updated = Product(
                    id = productId, name = "After Update", description = "Modified",
                    categoryId = null, barcode = "UPD-001",
                    unitPrice = BigDecimal("200.00"), isActive = false,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val updateJson = objectMapper.writeValueAsString(updated)

                mockMvc.perform(
                    put("/api/v1/inventory/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.name").value("After Update"))
                    .andExpect(jsonPath("$.unitPrice").value(200.00))
                    .andExpect(jsonPath("$.active").value(false))
            }

            it("PUT /api/v1/inventory/products/{id} returns 404 for non-existent product") {
                val product = Product(
                    id = UUID.randomUUID(), name = "Ghost", description = null,
                    categoryId = null, barcode = null,
                    unitPrice = BigDecimal("10.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val updateJson = objectMapper.writeValueAsString(product)

                mockMvc.perform(
                    put("/api/v1/inventory/products/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson)
                ).andExpect(status().isNotFound)
            }

            it("DELETE /api/v1/inventory/products/{id} deletes a product and returns 204") {
                val productId = UUID.randomUUID()
                val product = Product(
                    id = productId, name = "To Delete", description = null,
                    categoryId = null, barcode = "DEL-001",
                    unitPrice = BigDecimal("50.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val createJson = objectMapper.writeValueAsString(product)
                mockMvc.perform(
                    post("/api/v1/inventory/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isCreated)

                mockMvc.perform(delete("/api/v1/inventory/products/{id}", productId))
                    .andExpect(status().isNoContent)

                // Verify it's gone
                mockMvc.perform(get("/api/v1/inventory/products/{id}", productId))
                    .andExpect(status().isNotFound)
            }

            it("DELETE /api/v1/inventory/products/{id} returns 404 for non-existent product") {
                mockMvc.perform(delete("/api/v1/inventory/products/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound)
            }

            it("GET /api/v1/inventory/products/barcode/{barcode} finds product by barcode") {
                val productId = UUID.randomUUID()
                val barcode = "BARCODE-SEARCH-001"
                val product = Product(
                    id = productId, name = "Barcode Search", description = null,
                    categoryId = null, barcode = barcode,
                    unitPrice = BigDecimal("75.00"), isActive = true,
                    commercialUserId = null, createdAt = Instant.now(), updatedAt = Instant.now()
                )
                val createJson = objectMapper.writeValueAsString(product)
                mockMvc.perform(
                    post("/api/v1/inventory/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson)
                ).andExpect(status().isCreated)

                mockMvc.perform(get("/api/v1/inventory/products/barcode/{barcode}", barcode))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.id").value(productId.toString()))
                    .andExpect(jsonPath("$.barcode").value(barcode))
            }

            it("GET /api/v1/inventory/products/barcode/{barcode} returns 404 when not found") {
                mockMvc.perform(get("/api/v1/inventory/products/barcode/{barcode}", "NONEXISTENT"))
                    .andExpect(status().isNotFound)
            }
        }
    }
}
