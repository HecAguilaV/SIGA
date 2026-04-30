package com.siga.inventory.persistence

import com.siga.inventory.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

class ProductPersistenceTest : BaseIntegrationTest() {

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should create a product and return UUID`() {
        val productJson = """
            {
                "name": "Laptop Pro 16",
                "description": "High performance laptop",
                "unitPrice": 1500.00,
                "barcode": "LAP-001"
            }
        """.trimIndent()

        mockMvc.perform(post("/api/v1/inventory/products")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(productJson))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isString) // Here we expect UUID string
            .andExpect(jsonPath("$.name").value("Laptop Pro 16"))
    }
}
