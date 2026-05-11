package com.siga.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant
import java.time.temporal.ChronoUnit

class JwtAuthFilterTest {

    private val secret = "test-secret-jwt-auth-filter-1234567890"
    private val jwtService = JwtService(secret)
    private val filter = JwtAuthFilter(jwtService)
    private val request = mock(HttpServletRequest::class.java)
    private val response = mock(HttpServletResponse::class.java)
    private val filterChain = mock(FilterChain::class.java)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `valid token sets SecurityContext authentication`() {
        val token = jwtService.generateToken("user@test.com", "ADMIN", 1, "user")
        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertNotNull(authentication)
        assertEquals("user@test.com", authentication!!.principal)
        assertTrue(authentication.authorities.any { it.authority == "ROLE_ADMIN" })
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `missing Authorization header does not set authentication`() {
        `when`(request.getHeader("Authorization")).thenReturn(null)

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertNull(authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `non Bearer Authorization header does not set authentication`() {
        `when`(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz")

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertNull(authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `token with invalid signature does not set authentication`() {
        val otherSecret = "different-secret-not-matching-123456789"
        val algorithm = Algorithm.HMAC256(otherSecret)
        val token = JWT.create()
            .withSubject("hacker@test.com")
            .withClaim("rol", "ADMIN")
            .withClaim("principalType", "customer")
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
            .sign(algorithm)

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertNull(authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `expired token does not set authentication`() {
        val algorithm = Algorithm.HMAC256(secret)
        val token = JWT.create()
            .withSubject("expired@test.com")
            .withClaim("rol", "USER")
            .withClaim("principalType", "customer")
            .withIssuedAt(Instant.now().minus(48, ChronoUnit.HOURS))
            .withExpiresAt(Instant.now().minus(24, ChronoUnit.HOURS))
            .sign(algorithm)

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertNull(authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }

    @Test
    fun `filter does not throw exception and continues chain when token is invalid`() {
        `when`(request.getHeader("Authorization")).thenReturn("Bearer invalid-token-format")

        filter.doFilter(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertNull(authentication)
        verify(filterChain, times(1)).doFilter(request, response)
    }
}
