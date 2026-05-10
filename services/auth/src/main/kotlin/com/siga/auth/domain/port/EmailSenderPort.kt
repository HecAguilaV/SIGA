package com.siga.auth.domain.port

/**
 * Port for sending emails (hexagonal architecture).
 * Implementation may use SMTP, SendGrid, etc.
 */
interface EmailSenderPort {
    fun sendVerificationEmail(email: String, token: String, name: String)
}
