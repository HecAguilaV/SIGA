package com.siga.inventory.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.siga.inventory.application.usecase.ConsolidatedStockResponse
import com.siga.inventory.application.usecase.ConsolidatedProduct
import com.siga.inventory.application.usecase.ConsolidatedStockUseCase
import com.siga.inventory.application.usecase.CreateProductRequest
import com.siga.inventory.application.usecase.CreateProductResponse
import com.siga.inventory.application.usecase.CreateProductUseCase
import com.siga.inventory.application.usecase.ReconcileRequest
import com.siga.inventory.application.usecase.ReconcileResponse
import com.siga.inventory.application.usecase.ReconcileStockUseCase
import com.siga.inventory.application.usecase.SearchProductsUseCase
import com.siga.inventory.application.usecase.TransferMovementHistoryUseCase
import com.siga.inventory.application.usecase.TransferStockUseCase
import com.siga.inventory.controller.StockController
import com.siga.inventory.controller.ProductController
import com.siga.inventory.controller.TransferRequest
import com.siga.inventory.domain.model.Movement
import com.siga.inventory.domain.model.MovementType
import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.event.StockEventProducer
import com.siga.inventory.repository.ProductRepository
import com.siga.inventory.repository.StockRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Integration test for controller endpoints via MockMvc.
 *
 * Uses full Spring Boot context with [SpringBootTest] and [AutoConfigureMockMvc].
 * All use case and repository dependencies are mocked with [MockitoBean]
 * to isolate controller behavior. Security is bypassed by providing
 * a mock user via `with(user(...))`.
 *
 * Tests the HTTP layer: request mapping, JSON deserialization, status codes,
 * response bodies, and error handling.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ControllerIntegrationTest: HTTP endpoint layer")
class ControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // --- External beans to mock ---
    @MockitoBean
    private lateinit var stockEventProducer: StockEventProducer

    // --- StockController dependencies ---
    @MockitoBean
    private lateinit var consolidatedStockUseCase: ConsolidatedStockUseCase

    @MockitoBean
    private lateinit var reconcileStockUseCase: ReconcileStockUseCase

    @MockitoBean
    private lateinit var transferStockUseCase: TransferStockUseCase

    @MockitoBean
    private lateinit var transferMovementHistoryUseCase: TransferMovementHistoryUseCase

    // --- ProductController dependencies ---
    @MockitoBean
    private lateinit var createProductUseCase: CreateProductUseCase

    @MockitoBean
    private lateinit var searchProductsUseCase: SearchProductsUseCase

    @MockitoBean
    private lateinit var productRepositoryPort: ProductRepositoryPort

    private val productId = UUID.randomUUID()
    private val storeId = UUID.randomUUID()

    @Nested
    @DisplayName("GET /api/v1/inventory/stock/consolidated")
    inner class ConsolidatedStockEndpoint {

        @Test
        @DisplayName("returns 200 OK with consolidated stock response")
        fun `given storeId when requesting consolidated stock then returns 200 with response`() {
            val now = Instant.now()
            val responseDto = ConsolidatedStockResponse(
                products = listOf(
                    ConsolidatedProduct(
                        productId = productId,
                        productName = "Café Instantáneo 200g",
                        sku = "CAF-001",
                        totalStock = 80,
                        stores = listOf(
                            com.siga.inventory.application.usecase.StoreStock(
                                storeId = storeId, quantity = 50, lastMovementAt = now
                            )
                        )
                    )
                ),
                page = 0, size = 50, totalElements = 1, totalPages = 1
            )
            whenever(consolidatedStockUseCase.execute(eq(storeId), eq(0), eq(50)))
                .thenReturn(responseDto)

            mockMvc.perform(
                get("/api/v1/inventory/stock/consolidated")
                    .param("storeId", storeId.toString())
                    .param("page", "0")
                    .param("size", "50")
                    .with(user("admin"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.products[0].productName").value("Café Instantáneo 200g"))
                .andExpect(jsonPath("$.products[0].sku").value("CAF-001"))
                .andExpect(jsonPath("$.products[0].totalStock").value(80))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0))
        }

        @Test
        @DisplayName("returns 200 OK without storeId filter")
        fun `given no storeId when requesting consolidated stock then returns all products`() {
            val responseDto = ConsolidatedStockResponse(
                products = emptyList(),
                page = 0, size = 50, totalElements = 0, totalPages = 0
            )
            whenever(consolidatedStockUseCase.execute(eq(null), eq(0), eq(50)))
                .thenReturn(responseDto)

            mockMvc.perform(
                get("/api/v1/inventory/stock/consolidated")
                    .param("page", "0")
                    .param("size", "50")
                    .with(user("admin"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value(0))
        }
    }

    @Nested
    @DisplayName("POST /api/v1/inventory/stock/reconciliations")
    inner class ReconcileStockEndpoint {

        @Test
        @DisplayName("returns 200 OK for valid reconciliation request")
        fun `given valid request when reconciling then returns 200`() {
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 80,
                motive = "MERMA",
                userId = UUID.randomUUID()
            )
            val response = ReconcileResponse(
                reconciliationId = UUID.randomUUID(),
                productId = productId,
                storeId = storeId,
                previousStock = 100,
                newStock = 80,
                discrepancy = -20,
                motive = "MERMA",
                reconciledBy = request.userId,
                reconciledAt = Instant.now(),
                alertCreated = true
            )
            whenever(reconcileStockUseCase.execute(any())).thenReturn(response)

            mockMvc.perform(
                post("/api/v1/inventory/stock/reconciliations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.newStock").value(80))
                .andExpect(jsonPath("$.discrepancy").value(-20))
                .andExpect(jsonPath("$.alertCreated").value(true))
        }

        @Test
        @DisplayName("returns 404 when product not found at store")
        fun `given non existent product when reconciling then returns 404`() {
            val request = ReconcileRequest(
                productId = productId,
                storeId = storeId,
                physicalCount = 5,
                motive = "MERMA",
                userId = UUID.randomUUID()
            )
            whenever(reconcileStockUseCase.execute(any()))
                .thenThrow(IllegalArgumentException("Product not found at store"))

            mockMvc.perform(
                post("/api/v1/inventory/stock/reconciliations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.error").value("PRODUCT_NOT_FOUND_AT_STORE"))
        }
    }

    @Nested
    @DisplayName("POST /api/v1/inventory/stock/transfers")
    inner class TransferStockEndpoint {

        @Test
        @DisplayName("returns 201 Created for valid transfer request")
        fun `given valid request when transferring then returns 201`() {
            val request = TransferRequest(
                productId = productId,
                originStoreId = UUID.randomUUID(),
                destinationStoreId = UUID.randomUUID(),
                quantity = 30
            )
            val response = com.siga.inventory.application.usecase.TransferResponse(
                transferId = UUID.randomUUID(),
                correlationId = UUID.randomUUID(),
                productId = productId,
                originStoreId = request.originStoreId,
                destinationStoreId = request.destinationStoreId,
                quantity = 30,
                originNewStock = 70,
                destinationNewStock = 80,
                transferredBy = null,
                transferredAt = Instant.now()
            )
            whenever(
                transferStockUseCase.execute(
                    eq(request.productId), eq(request.originStoreId),
                    eq(request.destinationStoreId), eq(request.quantity), eq(null)
                )
            ).thenReturn(response)

            mockMvc.perform(
                post("/api/v1/inventory/stock/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.quantity").value(30))
                .andExpect(jsonPath("$.originNewStock").value(70))
                .andExpect(jsonPath("$.destinationNewStock").value(80))
        }

        @Test
        @DisplayName("returns 400 for invalid request (same store)")
        fun `given invalid request when transferring then returns 400`() {
            val request = TransferRequest(
                productId = productId,
                originStoreId = storeId,
                destinationStoreId = storeId,
                quantity = 10
            )
            whenever(
                transferStockUseCase.execute(
                    eq(request.productId), eq(request.originStoreId),
                    eq(request.destinationStoreId), eq(request.quantity), eq(null)
                )
            ).thenThrow(IllegalArgumentException("Origin and destination must be different"))

            mockMvc.perform(
                post("/api/v1/inventory/stock/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("INVALID_REQUEST"))
        }

        @Test
        @DisplayName("returns 409 for insufficient stock")
        fun `given insufficient stock when transferring then returns 409`() {
            val request = TransferRequest(
                productId = productId,
                originStoreId = UUID.randomUUID(),
                destinationStoreId = UUID.randomUUID(),
                quantity = 999
            )
            whenever(
                transferStockUseCase.execute(
                    eq(request.productId), eq(request.originStoreId),
                    eq(request.destinationStoreId), eq(request.quantity), eq(null)
                )
            ).thenThrow(IllegalStateException("Insufficient stock"))

            mockMvc.perform(
                post("/api/v1/inventory/stock/transfers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isConflict)
                .andExpect(jsonPath("$.error").value("INSUFFICIENT_STOCK"))
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventory/stock/movements")
    inner class MovementsEndpoint {

        @Test
        @DisplayName("returns 200 OK with movement history page")
        fun `given filters when requesting movements then returns 200`() {
            val movement = Movement(
                id = UUID.randomUUID(),
                productId = productId,
                storeId = storeId,
                type = MovementType.TRANSFER,
                quantity = 10,
                previousQuantity = 100,
                newQuantity = 90,
                userId = null,
                saleId = null,
                observations = "Test movement",
                correlationId = UUID.randomUUID(),
                destinationStoreId = UUID.randomUUID()
            )
            val pageable = PageRequest.of(0, 20)
            val page: Page<Movement> = PageImpl(listOf(movement), pageable, 1)
            whenever(
                transferMovementHistoryUseCase.execute(
                    eq(storeId), eq(MovementType.TRANSFER), eq(null), eq(null), any()
                )
            ).thenReturn(page)

            mockMvc.perform(
                get("/api/v1/inventory/stock/movements")
                    .param("storeId", storeId.toString())
                    .param("type", "TRANSFER")
                    .param("page", "0")
                    .param("size", "20")
                    .with(user("admin"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$.content[0].quantity").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
        }

        @Test
        @DisplayName("returns 200 OK without filters")
        fun `given no filters when requesting movements then returns all`() {
            val pageable = PageRequest.of(0, 20)
            val page: Page<Movement> = PageImpl(emptyList(), pageable, 0)
            whenever(
                transferMovementHistoryUseCase.execute(
                    eq(null), eq(null), eq(null), eq(null), any()
                )
            ).thenReturn(page)

            mockMvc.perform(
                get("/api/v1/inventory/stock/movements")
                    .with(user("admin"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.totalElements").value(0))
        }
    }

    @Nested
    @DisplayName("GET /api/v1/inventory/products/search")
    inner class SearchProductsEndpoint {

        @Test
        @DisplayName("returns 200 OK with search results")
        fun `given valid query when searching then returns 200 with results`() {
            val product = Product(
                id = productId,
                name = "Café Instantáneo 200g",
                description = null,
                categoryId = null,
                barcode = null,
                unitPrice = BigDecimal.valueOf(3200),
                isActive = true,
                commercialUserId = null,
                sku = "CAF-001",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            val page: Page<Product> = PageImpl(listOf(product))
            whenever(searchProductsUseCase.execute("cafe", 0, 20)).thenReturn(page)

            mockMvc.perform(
                get("/api/v1/inventory/products/search")
                    .param("q", "cafe")
                    .param("page", "0")
                    .param("size", "20")
                    .with(user("admin"))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.products[0].sku").value("CAF-001"))
                .andExpect(jsonPath("$.products[0].name").value("Café Instantáneo 200g"))
                .andExpect(jsonPath("$.totalElements").value(1))
        }

        @Test
        @DisplayName("returns 400 for too-short query")
        fun `given too short query when searching then returns 400`() {
            whenever(searchProductsUseCase.execute("a", 0, 20))
                .thenThrow(IllegalArgumentException("Search query must be at least 2 characters"))

            mockMvc.perform(
                get("/api/v1/inventory/products/search")
                    .param("q", "a")
                    .param("page", "0")
                    .param("size", "20")
                    .with(user("admin"))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("QUERY_TOO_SHORT"))
        }
    }

    @Nested
    @DisplayName("POST /api/v1/inventory/products")
    inner class CreateProductEndpoint {

        @Test
        @DisplayName("returns 201 Created for new product without duplicates")
        fun `given valid request when creating product then returns 201`() {
            val request = CreateProductRequest(
                name = "New Product",
                sku = null,
                categoryId = UUID.randomUUID(),
                categoryName = "Test",
                description = "A new product",
                unitType = "UNIDAD",
                barcode = null,
                force = false
            )
            val response = CreateProductResponse(
                productId = productId,
                sku = "NEW-001",
                name = "New Product",
                status = "ACTIVE",
                warning = null
            )

            whenever(productRepositoryPort.findByNameLike("New Product")).thenReturn(emptyList())
            whenever(createProductUseCase.execute(any(), eq(1L))).thenReturn(response)

            mockMvc.perform(
                post("/api/v1/inventory/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.sku").value("NEW-001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
        }

        @Test
        @DisplayName("returns 409 Conflict when duplicate found without force")
        fun `given duplicate product without force when creating then returns 409`() {
            val request = CreateProductRequest(
                name = "Existing Product",
                sku = null,
                categoryId = UUID.randomUUID(),
                categoryName = "Test",
                description = null,
                unitType = "UNIDAD",
                barcode = null,
                force = false
            )
            val existing = Product(
                id = UUID.randomUUID(),
                name = "Existing Product",
                description = null,
                categoryId = null,
                barcode = null,
                unitPrice = BigDecimal.ZERO,
                isActive = true,
                commercialUserId = null,
                sku = "EX-001",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            whenever(productRepositoryPort.findByNameLike("Existing Product")).thenReturn(listOf(existing))

            mockMvc.perform(
                post("/api/v1/inventory/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user("admin"))
                    .with(csrf())
            )
                .andExpect(status().isConflict)
                .andExpect(jsonPath("$.error").value("DUPLICATE_DETECTED"))
        }
    }
}
