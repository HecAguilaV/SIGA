package com.siga.common.audit

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Auto-configuración del Audit Trail Starter.
 *
 * Cualquier microservicio que importe `siga-common` como dependencia
 * obtiene auditoría automática SIN escribir una sola línea de código.
 *
 * Spring Boot detecta esta clase vía META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class AuditAutoConfiguration(
    private val objectMapper: ObjectMapper,
    @Value("\${spring.application.name:unknown-service}") private val serviceName: String
) : WebMvcConfigurer {

    @Bean
    fun auditInterceptor(): AuditInterceptor {
        return AuditInterceptor(objectMapper, serviceName)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(auditInterceptor())
            .addPathPatterns("/api/**")       // Solo interceptar rutas de API
            .excludePathPatterns("/health")   // Excluir health checks (ruido)
    }
}
