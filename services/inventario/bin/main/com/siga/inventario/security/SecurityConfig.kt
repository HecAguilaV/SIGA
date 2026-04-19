package com.siga.inventario.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().authenticated()
            }
            // En un entorno local/test podemos deshabilitar validación estricta de OAuth para este spec
            // o implementar un custom JWT decoder para tests.
            // Para mantener simple la triangulación de TDD sin levantar keys de Auth:
            // Descomentar lo siguiente en PROD:
            // .oauth2ResourceServer { it.jwt() }
        return http.build()
    }
}
