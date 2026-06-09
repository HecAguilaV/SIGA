package com.siga.inventory.persistence

import com.siga.inventory.BaseIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class ProductPersistenceTest : BaseIntegrationTest() {

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `should create a product and return UUID`() {
        val barcode = "PERSISTENCE-${UUID.randomUUID().toString().substring(0, 8)}"
        val productJson = """
            {
                "name": "Laptop Pro 16",
                "description": "High performance laptop",
                "barcode": "$barcode",
                "sku": null,
                "categoryId": null,
                "unitType": null,
                "force": true
            }
        """.trimIndent()

        mockMvc.perform(post("/api/v1/inventory/products")
            .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(productJson))
            .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.productId").isString) // Here we expect UUID string
            .andExpect(jsonPath("$.name").value("Laptop Pro 16"))
    }
}
