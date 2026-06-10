package com.siga.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * Base class for Integration Tests in SIGA Inventory Service.
 * Provides MockMvc and common testing infrastructure.
 * 
 * NOTE: Currently configured to use local Redis (siga-redis) due to Testcontainers issues.
 */
@SpringBootTest(properties = ["eureka.client.enabled=false"])
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { "localhost" }
            registry.add("spring.data.redis.port") { "6379" }
        }
    }

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    protected lateinit var stockEventProducer: com.siga.inventory.event.StockEventProducer
}
