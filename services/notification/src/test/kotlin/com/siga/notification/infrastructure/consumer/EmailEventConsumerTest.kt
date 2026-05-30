package com.siga.notification.infrastructure.consumer

import com.siga.notification.domain.EmailEvent
import com.siga.notification.domain.EmailType
import com.siga.notification.infrastructure.entity.ProcessedEvent
import com.siga.notification.infrastructure.repository.ProcessedEventRepository
import com.siga.notification.infrastructure.service.EmailSenderService
import com.siga.notification.infrastructure.service.TemplateRenderer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.anyString
import java.util.*

/**
 * Unit test for [EmailEventConsumer] dispatch logic.
 *
 * Mocks [ProcessedEventRepository], [TemplateRenderer], and [EmailSenderService]
 * to verify correct dispatch by email type and idempotency behavior.
 */
class EmailEventConsumerTest {

    private val processedEventRepository: ProcessedEventRepository = mock(ProcessedEventRepository::class.java)
    private val templateRenderer: TemplateRenderer = mock(TemplateRenderer::class.java)
    private val emailSenderService: EmailSenderService = mock(EmailSenderService::class.java)
    private val consumer = EmailEventConsumer(
        processedEventRepository,
        templateRenderer,
        emailSenderService
    )

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        any<T>()
        return null as T
    }

    @BeforeEach
    fun setUp() {
        reset(processedEventRepository, templateRenderer, emailSenderService)
        `when`(templateRenderer.render(anyString(), anyString(), anyString())).thenReturn("<html>Test</html>")
    }

    @Test
    fun `consume handles WELCOME event and sends verification email`() {
        `when`(processedEventRepository.existsById(anyObject())).thenReturn(false)

        val event = EmailEvent(
            eventId = UUID.randomUUID(),
            email = "test@example.com",
            type = EmailType.WELCOME,
            name = "Test User",
            token = "verify-token-123"
        )

        consumer.consume(event)

        verify(templateRenderer).render("welcome.html", "Test User", "/api/v1/auth/verify?token=verify-token-123")
        verify(emailSenderService).send("test@example.com", "Verify your SIGA account", "<html>Test</html>")
        verify(processedEventRepository).save(anyObject())
    }

    @Test
    fun `consume handles PASSWORD_RESET event and sends reset email`() {
        `when`(processedEventRepository.existsById(anyObject())).thenReturn(false)

        val event = EmailEvent(
            eventId = UUID.randomUUID(),
            email = "test@example.com",
            type = EmailType.PASSWORD_RESET,
            name = "Test User",
            token = "reset-token-456"
        )

        consumer.consume(event)

        verify(templateRenderer).render("password-reset.html", "Test User", "/api/v1/auth/reset-password/confirm?token=reset-token-456")
        verify(emailSenderService).send("test@example.com", "Reset your SIGA password", "<html>Test</html>")
        verify(processedEventRepository).save(anyObject())
    }

    @Test
    fun `consume skips duplicate event based on eventId`() {
        val eventId = UUID.randomUUID()
        `when`(processedEventRepository.existsById(eventId)).thenReturn(true)

        val event = EmailEvent(
            eventId = eventId,
            email = "test@example.com",
            type = EmailType.WELCOME,
            name = "Test User"
        )

        consumer.consume(event)

        verify(templateRenderer, never()).render(anyString(), anyString(), anyString())
        verify(emailSenderService, never()).send(anyString(), anyString(), anyString())
        verify(processedEventRepository, never()).save(anyObject())
    }

    @Test
    fun `consume builds correct action URL when token is null for WELCOME`() {
        `when`(processedEventRepository.existsById(anyObject())).thenReturn(false)

        val event = EmailEvent(
            eventId = UUID.randomUUID(),
            email = "test@example.com",
            type = EmailType.WELCOME,
            name = "Test User",
            token = null
        )

        consumer.consume(event)

        verify(templateRenderer).render("welcome.html", "Test User", "/api/v1/auth/login")
    }

    @Test
    fun `consume builds correct action URL when token is null for PASSWORD_RESET`() {
        `when`(processedEventRepository.existsById(anyObject())).thenReturn(false)

        val event = EmailEvent(
            eventId = UUID.randomUUID(),
            email = "test@example.com",
            type = EmailType.PASSWORD_RESET,
            name = "Test User",
            token = null
        )

        consumer.consume(event)

        verify(templateRenderer).render("password-reset.html", "Test User", "/api/v1/auth/login")
    }
}
