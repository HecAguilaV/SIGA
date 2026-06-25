package com.siga.auth.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    @PostConstruct
    fun validateSecret() {
        require(secret.isNotBlank()) { "JWT_SECRET must not be empty" }
        val knownInsecure = listOf("super-secret-key", "default-secret-key", "changeme")
        val match = knownInsecure.firstOrNull { secret.startsWith(it) }
        require(match == null) {
            "JWT_SECRET uses known insecure placeholder: '$match'. " +
            "Generate a 256+ bit random secret."
        }
    }
    private val algorithm: Algorithm by lazy { Algorithm.HMAC256(secret) }
    private val verifier: JWTVerifier by lazy { JWT.require(algorithm).build() }

    fun generateToken(email: String, rol: String, tenantId: Int?, principalType: String, permissions: List<String> = emptyList()): String {
        val builder = JWT.create()
            .withSubject(email)
            .withClaim("rol", rol)
            .withClaim("principalType", principalType)
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS))

        if (tenantId != null) {
            builder.withClaim("tenantId", tenantId)
        }

        if (permissions.isNotEmpty()) {
            builder.withClaim("permissions", permissions)
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

        val permissions = decoded.getClaim("permissions")
        if (!permissions.isMissing) {
            claims["permissions"] = permissions.asList(String::class.java)
        }

        return claims
    }
}
