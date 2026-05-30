package com.siga.notification.infrastructure.service

import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

/**
 * Email sender that dispatches emails via JavaMailSender.
 * If SMTP is not configured, falls back to logging the email content.
 *
 * Mirrors the pattern from auth's existing EmailSenderService but is
 * a standalone component in the notification service.
 */
@Service
class EmailSenderService {

    private val log = LoggerFactory.getLogger(EmailSenderService::class.java)

    @Autowired(required = false)
    private var mailSender: JavaMailSender? = null

    /**
     * Sends an email with the given parameters.
     *
     * @param to recipient email address
     * @param subject email subject line
     * @param body HTML body content
     */
    fun send(to: String, subject: String, body: String) {
        val sender = mailSender
        if (sender == null || (sender is org.springframework.mail.javamail.JavaMailSenderImpl && sender.host.isNullOrBlank())) {
            log.info("[EMAIL] To: $to, Subject: $subject")
            log.info("[EMAIL] Body:\n$body")
            return
        }

        val message: MimeMessage = sender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true) // true = HTML
        mailSender!!.send(message)
        log.info("Email sent to: $to (subject: $subject)")
    }
}
