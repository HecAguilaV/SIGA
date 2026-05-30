package com.siga.inventory.integration

import com.siga.inventory.application.usecase.ConsolidatedStockUseCase
import com.siga.inventory.domain.model.Product
import com.siga.inventory.domain.model.Stock
import com.siga.inventory.domain.port.ProductRepositoryPort
import com.siga.inventory.domain.port.StockRepositoryPort
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

/**
 * Integration test for Redis caching of [ConsolidatedStockUseCase].
 *
 * Verifies three behaviors:
 * 1. **Cache hit** — same request twice: second call returns cached without hitting ports
 * 2. **Cache miss** — different storeId/page/size → different cache key → fresh call
 * 3. **TTL protection** — cached data is returned within TTL window
 *
 * Uses Testcontainers Redis via [GenericContainer] and [DynamicPropertySource]
 * to inject the Redis connection at runtime.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@DisplayName("ConsolidatedStockCacheTest: Redis caching for ConsolidatedStockUseCase")
class ConsolidatedStockCacheTest {

    companion object {
        private const val REDIS_IMAGE = "redis:7-alpine"
        private const val REDIS_PORT = 6379

        @Container
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(REDIS_PORT).toString() }
        }
    }

    @Autowired
    private lateinit var useCase: ConsolidatedStockUseCase

    @MockitoBean
    private lateinit var stockPort: StockRepositoryPort

    @MockitoBean
    private lateinit var productPort: ProductRepositoryPort

    private val storeId = UUID.randomUUID()
    private val productId = UUID.randomUUID()
    private val now = Instant.now()

    private val sampleProduct: Product
        get() = Product(
            id = productId,
            name = "Café Test 500g",
            description = null,
            categoryId = null,
            barcode = "TST-001",
            sku = "TST-001",
            unitPrice = BigDecimal("25.00"),
            isActive = true,
            commercialUserId = UUID.randomUUID(),
            unitType = null,
            createdAt = now,
            updatedAt = now
        )

    private val sampleStocks: List<Stock>
        get() = listOf(
            Stock(productId = productId, storeId = storeId, quantity = 100, lastMovementAt = now)
        )

    // ── Cache Hit ────────────────────────────────────────────────

    @Test
    @DisplayName("cache hit: same request twice returns cached response without hitting ports")
    fun `given same request when called twice then second call returns cached result without hitting ports`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // First call — cache MISS, triggers execute() → calls ports once each
        val firstResult = useCase.execute(storeId = storeId, page = 0, size = 50)

        // Verify first call went through the use case
        verify(stockPort, times(1)).findAll()
        verify(productPort, times(1)).findById(productId)

        // Second call with SAME key — cache HIT, returns cached WITHOUT calling ports
        val secondResult = useCase.execute(storeId = storeId, page = 0, size = 50)

        // Verify ports were NOT called again (cache hit)
        verify(stockPort, times(1)).findAll()
        verify(productPort, times(1)).findById(productId)

        // Both results must be identical
        assert(firstResult.products.size == secondResult.products.size) {
            "Cached result should have same number of products"
        }
        assert(firstResult.products.first().totalStock == secondResult.products.first().totalStock) {
            "Cached result should have same total stock"
        }
        assert(firstResult.totalElements == secondResult.totalElements) {
            "Cached result should have same total elements"
        }
    }

    // ── Cache Miss ───────────────────────────────────────────────

    @Test
    @DisplayName("cache miss: different storeId produces different cache key → fresh call")
    fun `given different storeId when called then produces cache miss and fresh call`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // First call with storeId
        useCase.execute(storeId = storeId, page = 0, size = 50)

        // Reset mock counters for clean measurement
        // Second call with null storeId → "all" key → different from storeId key
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

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

        // Reset port call count using a differentiated stock return
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // Second call with page=1 — different cache key → should call ports
        useCase.execute(storeId = storeId, page = 1, size = 1)

        // Ports should have been called twice (once for each distinct key)
        verify(stockPort, times(2)).findAll()
    }

    // ── TTL Verification ─────────────────────────────────────────

    @Test
    @DisplayName("cache TTL: cached data returned within TTL window without calling ports")
    fun `given cached data when TTL not expired then returns cached without calling ports`() {
        whenever(stockPort.findAll()).thenReturn(sampleStocks)
        whenever(productPort.findById(productId)).thenReturn(sampleProduct)

        // First call — cache MISS
        useCase.execute(storeId = storeId, page = 0, size = 50)
        verify(stockPort, times(1)).findAll()

        // Immediate second call — cache HIT (within 60s TTL)
        useCase.execute(storeId = storeId, page = 0, size = 50)

        // Ports still called only once — cache returned the data
        verify(stockPort, times(1)).findAll()
    }
}
