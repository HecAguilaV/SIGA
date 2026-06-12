package com.siga.auth

import com.siga.auth.controller.ResetPasswordConfirmRequest
import com.siga.auth.controller.ResetPasswordRequest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

/**
 * Integration tests for the password reset HTTP endpoints in AuthController.
 */
class AuthPasswordResetIntegrationTest : BaseIntegrationTest() {

    @Test
    fun `reset request with empty email returns 400`() {
        val requestBody = objectMapper.writeValueAsString(ResetPasswordRequest(email = ""))
        mockMvc.perform(
            post("/api/v1/auth/reset-password/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `reset request with valid email returns 200`() {
        val requestBody = objectMapper.writeValueAsString(ResetPasswordRequest(email = "test@test.com"))
        mockMvc.perform(
            post("/api/v1/auth/reset-password/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isOk)
    }

    @Test
    fun `reset confirm with empty token returns 400`() {
        val requestBody = objectMapper.writeValueAsString(ResetPasswordConfirmRequest(token = "", newPassword = "123"))
        mockMvc.perform(
            post("/api/v1/auth/reset-password/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `reset confirm with empty password returns 400`() {
        val requestBody = objectMapper.writeValueAsString(ResetPasswordConfirmRequest(token = "abc", newPassword = ""))
        mockMvc.perform(
            post("/api/v1/auth/reset-password/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `reset confirm with invalid token returns 404`() {
        val requestBody = objectMapper.writeValueAsString(ResetPasswordConfirmRequest(token = "invalid-token", newPassword = "NewPassword123!"))
        mockMvc.perform(
            post("/api/v1/auth/reset-password/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        ).andExpect(status().isNotFound)
    }
}
