package com.siga.notification.infrastructure.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Unit test for [TemplateRenderer].
 *
 * Verifies that `{{name}}` and `{{actionUrl}}` placeholders are properly
 * substituted, and that `{{year}}` is replaced with the current year.
 */
class TemplateRendererTest {

    private val renderer = TemplateRenderer()

    @Test
    fun `render substitutes name and actionUrl placeholders`() {
        val html = renderer.render("welcome.html", "John Doe", "https://example.com/verify?token=abc")

        assertTrue(html.contains("John Doe"))
        assertTrue(html.contains("https://example.com/verify?token=abc"))
        assertFalse(html.contains("{{name}}"))
        assertFalse(html.contains("{{actionUrl}}"))
    }

    @Test
    fun `render substitutes year placeholder`() {
        val html = renderer.render("welcome.html", "Test", "https://example.com")
        val currentYear = java.time.Year.now().toString()

        assertTrue(html.contains(currentYear))
        assertFalse(html.contains("{{year}}"))
    }

    @Test
    fun `render handles multiple substitutions in password-reset template`() {
        val html = renderer.render("password-reset.html", "Jane Doe", "https://example.com/reset?token=xyz")

        assertTrue(html.contains("Jane Doe"))
        assertTrue(html.contains("https://example.com/reset?token=xyz"))
        assertTrue(html.contains("Password Reset"))
        assertFalse(html.contains("{{name}}"))
        assertFalse(html.contains("{{actionUrl}}"))
    }

    @Test
    fun `render returns fallback for missing template`() {
        val html = renderer.render("nonexistent.html", "Test", "https://example.com")
        // Should return a fallback HTML without throwing
        assertNotNull(html)
        assertTrue(html.isNotEmpty())
    }

    @Test
    fun `render escapes nothing for simple text values`() {
        val html = renderer.render("welcome.html", "John & Jane <Test>", "https://example.com?a=b&c=d")
        assertTrue(html.contains("John & Jane <Test>"))
        assertTrue(html.contains("https://example.com?a=b&c=d"))
    }
}
