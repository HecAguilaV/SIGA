package com.siga.notification.infrastructure.consumer

import com.siga.notification.domain.EmailEvent
import com.siga.notification.domain.EmailType
import com.siga.notification.infrastructure.entity.ProcessedEvent
import com.siga.notification.infrastructure.repository.ProcessedEventRepository
import com.siga.notification.infrastructure.service.EmailSenderService
import com.siga.notification.infrastructure.service.TemplateRenderer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
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
        verify(emailSenderService).send("test@example.com", "Verifica tu cuenta de SIGA", "<html>Test</html>")
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
        verify(emailSenderService).send("test@example.com", "Restablece tu contraseña de SIGA", "<html>Test</html>")
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

    // --- REQ-5: SMTP Retry Tests ---

    @Test
    fun `consume retries 4 times when sender always throws and does NOT save processed event`() {
        `when`(processedEventRepository.existsById(anyObject())).thenReturn(false)
        doThrow(RuntimeException("SMTP failure"))
            .`when`(emailSenderService)
            .send(anyString(), anyString(), anyString())

        val event = EmailEvent(
            eventId = UUID.randomUUID(),
            email = "retry@test.com",
            type = EmailType.WELCOME,
            name = "Retry User",
            token = "retry-token"
        )

        consumer.consume(event)

        // Must attempt 4 times (1 initial + 3 retries with backoff)
        verify(emailSenderService, times(4)).send(anyString(), anyString(), anyString())
        // Must NOT mark as processed — retries were exhausted
        verify(processedEventRepository, never()).save(anyObject())
    }

    @Test
    fun `consume retries then succeeds on second attempt and saves processed event`() {
        `when`(processedEventRepository.existsById(anyObject())).thenReturn(false)
        doThrow(RuntimeException("First attempt failure"))
            .doNothing()
            .`when`(emailSenderService)
            .send(anyString(), anyString(), anyString())

        val event = EmailEvent(
            eventId = UUID.randomUUID(),
            email = "retry-then-success@test.com",
            type = EmailType.PASSWORD_RESET,
            name = "Retry Success User",
            token = "retry-success-token"
        )

        consumer.consume(event)

        // First attempt failed, second succeeded — no more retries needed
        verify(emailSenderService, times(2)).send(anyString(), anyString(), anyString())
        // Must mark as processed after success
        verify(processedEventRepository).save(anyObject())
    }
}
