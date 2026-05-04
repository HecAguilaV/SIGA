package com.siga.sales

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import com.siga.sales.event.SaleEventProducer

/**
 * Base class for Integration Tests in SIGA Sales Service.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    protected lateinit var saleEventProducer: SaleEventProducer
}
