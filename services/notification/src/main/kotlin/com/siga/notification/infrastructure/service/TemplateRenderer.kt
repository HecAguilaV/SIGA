package com.siga.notification.infrastructure.service

import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Year
import java.util.stream.Collectors

/**
 * Simple HTML template renderer using string substitution.
 *
 * Reads templates from the classpath (e.g. `templates/welcome.html`),
 * replaces `{{name}}`, `{{actionUrl}}`, and `{{year}}` placeholders.
 *
 * DESIGN DECISION: No Thymeleaf or template engine dependency.
 * These templates are few, rarely change, and have minimal dynamic content.
 * String substitution is sufficient and easier to test.
 */
@Component
class TemplateRenderer {

    private val log = LoggerFactory.getLogger(TemplateRenderer::class.java)

    /**
     * Renders a template by reading it from the classpath and substituting
     * the given variables.
     *
     * @param templateName resource path relative to `src/main/resources/templates/`
     * @param name recipient display name
     * @param actionUrl the URL for the action link (verify or reset)
     * @return rendered HTML string
     */
    fun render(templateName: String, name: String, actionUrl: String): String {
        val template = loadTemplate(templateName)
        return template
            .replace("{{name}}", name)
            .replace("{{actionUrl}}", actionUrl)
            .replace("{{year}}", Year.now().toString())
    }

    /**
     * Loads a template from the classpath.
     * Templates are stored in `src/main/resources/templates/`.
     */
    private fun loadTemplate(templateName: String): String {
        val resource = ClassPathResource("templates/$templateName")
        if (!resource.exists()) {
            log.warn("Template not found: templates/$templateName")
            return "<html><body><p>Hello $templateName</p></body></html>"
        }

        return BufferedReader(InputStreamReader(resource.inputStream))
            .lines()
            .collect(Collectors.joining("\n"))
    }
}
