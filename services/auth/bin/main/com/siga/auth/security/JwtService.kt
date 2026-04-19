package com.siga.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class JwtService(
    @Value("\${jwt.secret:default-secret-key-too-long-to-be-secure-enough-probably-123456}")
    private val secret: String
) {
    fun generateToken(email: String, rol: String, tenantId: Int?): String {
        val algorithm = Algorithm.HMAC256(secret)
        
        val builder = JWT.create()
            .withSubject(email)
            .withClaim("rol", rol)
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
            
        if (tenantId != null) {
            builder.withClaim("tenant_id", tenantId)
        }
        
        return builder.sign(algorithm)
    }
}
