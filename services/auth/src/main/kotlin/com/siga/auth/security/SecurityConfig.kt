package com.siga.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/v1/auth/register",
                        "/api/v1/auth/verify",
                        "/api/v1/auth/login",
                        "/api/auth/customers/**",
                        "/api/v1/auth/users/**",
                        "/actuator/health"
                    ).permitAll()
                    .anyRequest().permitAll() // TODO: restrict in future slices
            }
            .addFilterBefore(JwtLoggingFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    /**
     * Simple filter that logs JWT tokens but doesn't block requests.
     * Will be replaced by proper JwtAuthFilter in a future slice.
     */
    class JwtLoggingFilter : OncePerRequestFilter() {
        private val log = LoggerFactory.getLogger(JwtLoggingFilter::class.java)

        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
        ) {
            val authHeader = request.getHeader("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                log.debug("JWT token present: ${authHeader.take(50)}...")
            }
            filterChain.doFilter(request, response)
        }
    }
}
