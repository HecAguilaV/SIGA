package com.siga.inventory.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private val algorithm: Algorithm by lazy { Algorithm.HMAC256(secret) }
    private val verifier: JWTVerifier by lazy { JWT.require(algorithm).build() }

    fun verify(token: String): DecodedJWT = verifier.verify(token)

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
