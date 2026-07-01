package com.siga.inventory.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT authentication filter — validates Bearer tokens issued by the auth
 * service and propagates the principal + tenant + permissions into the
 * Spring SecurityContext so downstream code can authorize per-tenant.
 */
@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.removePrefix("Bearer ")

        try {
            val decoded = jwtService.verify(token)
            val email = decoded.subject
            val role = decoded.getClaim("rol").asString()
            val principalType = decoded.getClaim("principalType").asString() ?: ""

            val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

            val details = mutableMapOf<String, Any?>()
            details["email"] = email ?: ""
            details["rol"] = role ?: ""
            details["principalType"] = principalType
            val tenantIdClaim = decoded.getClaim("tenantId")
            if (!tenantIdClaim.isMissing) {
                details["tenantId"] = tenantIdClaim.asInt()
            }
            val permissionsClaim = decoded.getClaim("permissions")
            if (!permissionsClaim.isMissing) {
                details["permissions"] = permissionsClaim.asList(String::class.java)
            }

            val authentication = UsernamePasswordAuthenticationToken(
                email,
                null,
                authorities
            ).apply { this.details = details }

            SecurityContextHolder.getContext().authentication = authentication
            log.debug("Authenticated user: $email with role: $role, principalType: $principalType")
        } catch (e: Exception) {
            log.debug("Failed to validate JWT token: ${e.message}")
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}
