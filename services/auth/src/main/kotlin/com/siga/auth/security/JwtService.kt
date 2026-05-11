package com.siga.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class JwtService(
    @Value("\${jwt.secret:default-secret-key-too-long-to-be-secure-enough-probably-123456}")
    private val secret: String
) {
    private val algorithm: Algorithm by lazy { Algorithm.HMAC256(secret) }
    private val verifier: JWTVerifier by lazy { JWT.require(algorithm).build() }

    fun generateToken(email: String, rol: String, tenantId: Int?, principalType: String): String {
        val builder = JWT.create()
            .withSubject(email)
            .withClaim("rol", rol)
            .withClaim("principalType", principalType)
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS))

        if (tenantId != null) {
            builder.withClaim("tenantId", tenantId)
        }

        return builder.sign(algorithm)
    }

    fun verify(token: String): DecodedJWT {
        return verifier.verify(token)
    }

    fun extractClaims(token: String): Map<String, Any> {
        val decoded = verify(token)
        val claims = mutableMapOf<String, Any>()
        claims["email"] = decoded.subject ?: ""
        claims["rol"] = decoded.getClaim("rol").asString() ?: ""
        claims["principalType"] = decoded.getClaim("principalType").asString() ?: ""

        val tenantId = decoded.getClaim("tenantId")
        if (!tenantId.isMissing) {
            claims["tenantId"] = tenantId.asInt()
        }

        return claims
    }
}
