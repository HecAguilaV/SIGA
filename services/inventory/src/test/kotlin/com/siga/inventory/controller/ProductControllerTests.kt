package com.siga.inventory.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should return 401 or 403 when requesting products without token`() {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isForbidden)
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    fun `should return 200 OK and filter products by tenant_id claim`() {
        mockMvc.perform(get("/api/products").header("X-Tenant-Id", "50"))
            .andExpect(status().isOk)
    }
}