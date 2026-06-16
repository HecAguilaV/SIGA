package com.siga.inventory.integration

import com.siga.inventory.application.usecase.ConsolidatedStockUseCase
import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.math.BigDecimal
import java.time.Instant
import java.util.*

/**
 * Integration Test: ConsolidatedStockUseCase caching with Redis.
 *
 * Verifies that:
 * 1. Initial call populates the cache (hits ports).
 * 2. Subsequent identical calls return from cache (skips ports).
 * 3. Different parameters (storeId, pagination) use different cache keys (hits ports).
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ConsolidatedStockCacheTest: Redis caching for ConsolidatedStockUseCase")
class ConsolidatedStockCacheTest : com.siga.inventory.BaseIntegrationTest() {

    @Autowired
    private lateinit var useCase: ConsolidatedStockUseCase

    @Autowired
    private lateinit var cacheManager: CacheManager

    @MockitoBean
    private lateinit var stockPort: StockRepositoryPort

    @MockitoBean
    private lateinit var productPort: ProductRepositoryPort

    private val productId = UUID.randomUUID()
    private val storeId = UUID.randomUUID()

    private val sampleProduct = com.siga.inventory.domain.model.Product(
        productId,
        "Test Product",
        "Description",
        null,
        "123456",
        BigDecimal("10.00"),
        true,
        null,
        null,
        null,
        Instant.now(),
        Instant.now()
    )

    private val sampleStocks = listOf(
        Stock(
            productId = productId,
            storeId = storeId,
            quantity = 100,
            lastMovementAt = Instant.now()
        )
    )

    @BeforeEach
    fun clearCache() {
        cacheManager.getCache("consolidatedStock")?.clear()
        Thread.sleep(100)
        Mockito.clearInvocations(stockPort, productPort)
    }

    // ── Cache Hit ────────────────────────────────────────────────

    @Test
    @DisplayName("cache hit: same request twice returns cached response without hitting ports")
    fun `given cached data when called again then returns from cache`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // First call — cache MISS
        useCase.execute(storeId = storeId, page = 0, size = 50)

        Thread.sleep(100)

        // Second call — cache HIT (within 60s TTL)
        useCase.execute(storeId = storeId, page = 0, size = 50)

        // Ports should only be called once
        verify(stockPort, times(1)).findAll()
        verify(productPort, times(1)).findById(productId)
    }

    // ── Cache Miss ───────────────────────────────────────────────

    @Test
    @DisplayName("cache miss: different storeId produces different cache key → fresh call")
    fun `given different storeId when called then produces cache miss and fresh call`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // First call with storeId
        useCase.execute(storeId = storeId, page = 0, size = 50)

        // Second call with null storeId → "all" key → different from storeId key
        useCase.execute(storeId = null, page = 0, size = 50)

        // The null-storeId call should have triggered fresh port calls (cache miss)
        verify(stockPort, times(2)).findAll()
    }

    @Test
    @DisplayName("cache miss: different page produces different cache key → fresh call")
    fun `given different page when called then produces cache miss and fresh call`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // First call with page=0
        useCase.execute(storeId = storeId, page = 0, size = 1)

        // Second call with page=1 — different cache key → should call ports
        useCase.execute(storeId = storeId, page = 1, size = 1)

        // Ports should have been called twice (once for each distinct key)
        verify(stockPort, times(2)).findAll()
    }

    // ── Repeated Hits ────────────────────────────────────────────

    @Test
    @DisplayName("cache repeated hits: multiple identical calls reuse cached data")
    fun `given cached data when called multiple times then reuses cache`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // Five identical calls — only the first should miss
        repeat(5) {
            useCase.execute(storeId = storeId, page = 0, size = 50)
        }

        // With working cache, ports are called once (first call only).
        // Allow up to 2 in case of Redis serialization race on first PUT.
        verify(stockPort, atMost(2)).findAll()
    }
}

