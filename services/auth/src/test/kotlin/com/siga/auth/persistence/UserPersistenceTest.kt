package com.siga.auth.persistence

import com.siga.auth.BaseIntegrationTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

class UserPersistenceTest : BaseIntegrationTest() {

    @Test
    @WithMockUser(roles = ["ADMIN"])
    @DisplayName("REQ-UUID-001: Should create a user and return a valid UUID")
    fun shouldCreateUserAndReturnUUID() {
        val userJson = """
            {
                "firstName": "Test",
                "lastName": "User UUID",
                "email": "test_uuid@siga.cl",
                "passwordHash": "Password123!",
                "role": "ADMINISTRATOR",
                "isActive": true
            }
        """.trimIndent()

        mockMvc.perform(post("/api/v1/auth/users")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(userJson))
            .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            // This is the "killer" assertion: checks if it's a valid UUID string
            .andExpect { result ->
                val id = result.response.contentAsString.let { 
                    objectMapper.readTree(it).get("id").asText() 
                }
                UUID.fromString(id) // Throws exception if not a valid UUID
            }
    }
}
