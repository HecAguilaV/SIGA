package com.siga.auth.infrastructure.adapter

import com.siga.auth.domain.port.EmailSenderPort
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

/**
 * Email sender adapter that sends verification emails via JavaMailSender.
 * If SMTP is not configured, falls back to logging the email content.
 *
 * The `name` parameter may be an email prefix (e.g., "juan") when the user
 * registered without providing a name. In that case, `buildEmailBody`
 * sanitizes it: if the name looks like a full email (contains "@"),
 * the domain part is stripped. If the name is blank, "there" is used.
 */
@Service
class EmailSenderService : EmailSenderPort {

    private val log = LoggerFactory.getLogger(EmailSenderService::class.java)

    @Autowired(required = false)
    private var mailSender: JavaMailSender? = null

    override fun sendVerificationEmail(email: String, token: String, name: String) {
        val verificationLink = "/api/v1/auth/verify?token=$token"
        val greetingName = sanitizeGreetingName(name)
        val body = buildEmailBody(greetingName, verificationLink)

        if (mailSender == null) {
            log.info("[EMAIL] Verification for $email: token=$token, link=$verificationLink")
            log.info("[EMAIL] Body:\n$body")
            return
        }

        try {
            val message: MimeMessage = mailSender!!.createMimeMessage()
            val helper = MimeMessageHelper(message, false)
            helper.setTo(email)
            helper.setSubject("Verify your SIGA account")
            helper.setText(body, false)
            mailSender!!.send(message)
            log.info("Verification email sent to: $email")
        } catch (e: Exception) {
            log.warn("Failed to send email to $email: ${e.message}")
            log.warn("[EMAIL] Body:\n$body")
        }
    }

    /**
     * Sanitizes the greeting name for the email body.
     * - If blank or only whitespace → "there"
     * - If it contains "@" → extracts the part before "@" (email prefix)
     * - Otherwise → returns the name as-is
     */
    internal fun sanitizeGreetingName(name: String): String {
        if (name.isBlank()) return "there"
        val atIndex = name.indexOf('@')
        return if (atIndex > 0) name.substring(0, atIndex) else name
    }

    private fun buildEmailBody(name: String, verificationLink: String): String {
        return """
            Hello $name,
            
            Welcome to SIGA! Please verify your email address by clicking the link below:
            
            $verificationLink
            
            This link will expire in 24 hours.
            
            If you did not create an account, please ignore this email.
            
            Best regards,
            SIGA Team
        """.trimIndent()
    }
}
