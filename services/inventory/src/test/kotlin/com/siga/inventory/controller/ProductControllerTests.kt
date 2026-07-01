package com.siga.inventory.controller

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTests : DescribeSpec() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private lateinit var stockEventProducer: com.siga.inventory.event.StockEventProducer

    init {
        extension(SpringExtension())
        describe("ProductController Integration") {

            it("given no token when requesting products then should return 401 Unauthorized") {
                mockMvc.perform(get("/api/v1/inventory/products"))
                    .andExpect(status().isUnauthorized)
            }

            it("given mock user when requesting products then should return 200 OK") {
                mockMvc.perform(
                    get("/api/v1/inventory/products")
                        .with(user("testuser"))
                        .header("X-Tenant-Id", UUID.randomUUID().toString())
                ).andExpect(status().isOk)
            }
        }
    }

    // Nota: @WithMockUser en métodos it() requiere configuraciones adicionales o usarlo a nivel de clase/init
    // Para simplificar y seguir el estándar, lo dejamos así por ahora.
}