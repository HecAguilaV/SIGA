package com.siga.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

/**
 * Base class for Integration Tests in SIGA Inventory Service.
 * Provides MockMvc and common testing infrastructure.
 * 
 * Uses Testcontainers for Redis to ensure portable and professional tests.
 */
@SpringBootTest(properties = ["eureka.client.enabled=false"])
@ContextConfiguration(initializers = [RedisTestContainer.Initializer::class])
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    protected lateinit var stockEventProducer: com.siga.inventory.event.StockEventProducer
}
