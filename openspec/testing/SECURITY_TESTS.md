# ═══════════════════════════════════════════════════════════════════════════
# SIGA - Guía de Tests de Seguridad
# Cómo escribir tests de seguridad efectivos
# ═══════════════════════════════════════════════════════════════════════════

# Tabla de Contenidos
1. [Filosofía](#filosofía)
2. [Estructura de Security Tests](#estructura-de-security-tests)
3. [Patrones de Tests por OWASP](#patrones-de-tests-por-owasp)
4. [Ejemplos por Servicio](#ejemplos-por-servicio)
5. [Integration Tests de Seguridad](#integration-tests-de-seguridad)
6. [Complemento con SAST](#complemento-con-sast)

---

## Filosofía

Los tests de seguridad NO son opcionales. Son parte del SDD.

```
┌──────────────────────────────────────────────────────────────┐
│  SDD Security Workflow                                        │
├──────────────────────────────────────────────────────────────┤
│  1. En spec.md:                                               │
│     security_requirements:                                    │
│       - "DEBE validar que el usuario solo acceda sus datos"   │
│       - "DEBE prevenir SQL injection en search"               │
│       - "DEBE bloquear após 5 intentos fallidos"             │
│                                                              │
│  2. En tasks.md:                                             │
│     [SEC-001] Implementar validación de ownership           │
│     [SEC-002] Agregar tests de ownership verification        │
│                                                              │
│  3. En *.test.kt:                                            │
│     @Test                                                     │
│     fun security_user_should_only_access_own_data() { ... }  │
│                                                              │
│  4. En verify:                                               │
│     ¿Hay tests para cada security_requirement? ✅           │
└──────────────────────────────────────────────────────────────┘
```

---

## Estructura de Security Tests

### Ubicación

```
services/
├── auth/
│   └── src/test/kotlin/com/siga/auth/
│       ├── service/
│       │   └── JwtServiceTest.kt
│       ├── controller/
│       │   └── AuthControllerTest.kt
│       └── security/                              ← NUEVO
│           ├── AccessControlTest.kt
│           ├── AuthenticationTest.kt
│           ├── InputValidationTest.kt
│           └── TenantIsolationTest.kt
```

### Naming Convention

```kotlin
// Formato: security_{threat}_should_be_{protection}
class SecurityAccessControlTest {
    fun security_unauthenticated_user_should_not_access_api()
    fun security_user_without_permission_should_get_403()
    fun security_user_from_tenant_a_should_not_access_tenant_b_data()
}

class SecurityInputValidationTest {
    fun security_sql_injection_in_search_should_be_prevented()
    fun security_xss_in_name_field_should_be_sanitized()
    fun security_path_traversal_in_file_upload_should_be_blocked()
}

class SecurityAuthenticationTest {
    fun security_weak_password_should_be_rejected()
    fun security_expired_token_should_be_rejected()
    fun security_account_should_lock_after_5_failed_attempts()
}
```

### Template de Test

```kotlin
package com.siga.auth.security

import com.siga.auth.service.JwtService
import com.siga.auth.repository.TokenRevocationRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.assertions.throwables.shouldThrow
import org.springframework.security.authentication.BadCredentialsException

class AuthenticationSecurityTest : DescribeSpec({

    describe("Authentication Security") {

        val jwtService = JwtService(secret)
        val authService = AuthService(userRepository, jwtService)

        // ═══════════════════════════════════════════════════════
        // A07: Identification and Authentication Failures
        // ═══════════════════════════════════════════════════════

        describe("Password Policy") {

            it("security_weak_password_should_be_rejected") {
                // Given: Un password que no cumple política
                val weakPasswords = listOf(
                    "12345678",           // Solo números
                    "abcdefgh",           // Solo minúsculas
                    "PASSWORD123",        // Sin minúsculas
                    "password",           // Muy común
                    "abc",                // Muy corto
                )

                // When/Then
                weakPasswords.forEach { password ->
                    shouldThrow<WeakPasswordException> {
                        authService.register(
                            userDto.copy(password = password)
                        )
                    }
                }
            }

            it("security_valid_password_should_be_accepted") {
                // Given: Password válido
                val validPassword = "SecureP@ssw0rd!2024"

                // When
                val user = authService.register(
                    userDto.copy(password = validPassword)
                )

                // Then
                user shouldNotBe null
                user.password shouldNotBe validPassword  // Nunca guardar plain text
            }
        }

        describe("Account Lockout") {

            it("security_account_should_lock_after_5_failed_attempts") {
                // Given
                val email = "locked-user@test.cl"

                // When: 5 intentos fallidos
                repeat(5) {
                    kotlin.runCatching {
                        authService.login(email, "wrong-password")
                    }
                }

                // Then: 6to intento debe fallar
                shouldThrow<AccountLockedException> {
                    authService.login(email, "correct-password")
                }
            }

            it("security_locked_account_should_unlock_after_30_minutes") {
                // Given: Usuario bloqueado hace 31 minutos
                val lockedUser = createLockedUser(lockedAt = Instant.now().minusSeconds(31 * 60))

                // When
                val result = authService.login(lockedUser.email, "correct-password")

                // Then: Login exitoso
                result shouldNotBe null
            }
        }

        // ═══════════════════════════════════════════════════════
        // A01: Broken Access Control
        // ═══════════════════════════════════════════════════════

        describe("JWT Security") {

            it("security_expired_token_should_be_rejected") {
                // Given: Token expirado (generado con fecha pasada)
                val expiredToken = jwtService.generate(
                    userId = 1,
                    expiresIn = Duration.ofMinutes(-15)
                )

                // Then
                shouldThrow<TokenExpiredException> {
                    jwtService.validate(expiredToken)
                }
            }

            it("security_tampered_token_should_be_rejected") {
                // Given: Token con firma alterada
                val validToken = jwtService.generate(userId = 1)
                val tamperedToken = validToken.replaceRange(50, 51, "X")

                // Then
                shouldThrow<JWTVerificationException> {
                    jwtService.validate(tamperedToken)
                }
            }

            it("security_token_from_other_tenant_should_be_rejected") {
                // Given: Token de tenant A intentando acceder a recurso de tenant B
                val tokenTenantA = jwtService.generate(userId = 1, tenantId = 1)

                // When/Then
                shouldThrow<TenantAccessDeniedException> {
                    storeService.getById(storeId = 2, tenantId = 2)  // Recurso de tenant B
                        .withToken(tokenTenantA)
                }
            }
        }
    }
})
```

---

## Patrones de Tests por OWASP

### A01 - Broken Access Control

```kotlin
// ─────────────────────────────────────────────────────────────────
// Access Control Tests
// ─────────────────────────────────────────────────────────────────

@Test
fun `should deny access without authentication`() {
    mockMvc.get("/api/protected-resource")
        .andExpect(status().isUnauthorized)
}

@Test
fun `should deny access with invalid token`() {
    mockMvc.get("/api/protected-resource")
        .header("Authorization", "Bearer invalid-token")
        .andExpect(status().isUnauthorized)
}

@Test
fun `should deny access to admin endpoint for regular user`() {
    val userToken = jwtService.generateToken("user@test.cl", role = "USER")

    mockMvc.get("/api/admin/users")
        .header("Authorization", "Bearer $userToken")
        .andExpect(status().isForbidden)
}

@Test
fun `should deny cross-tenant data access`() {
    val tenantAToken = jwtService.generateToken("user@tenantA.cl", tenantId = 1)

    // Intentar acceder a store del tenant B
    mockMvc.get("/api/stores/999")  // Este store pertenece a tenantId=2
        .header("Authorization", "Bearer $tenantAToken")
        .andExpect(status().isForbidden)
}

@Test
fun `should allow access to own tenant data`() {
    val tenantAToken = jwtService.generateToken("user@tenantA.cl", tenantId = 1)

    // Acceder a store propio
    mockMvc.get("/api/stores/1")  // Este store pertenece a tenantId=1
        .header("Authorization", "Bearer $tenantAToken")
        .andExpect(status().isOk)
}

@Test
fun `should enforce IDOR protection on resources`() {
    val userToken = jwtService.generateToken("user@test.cl", tenantId = 1)

    // Intentar acceder a orden de otro usuario
    mockMvc.get("/api/orders/999")
        .header("Authorization", "Bearer $userToken")
        .andExpect(status().isForbidden)
}
```

### A02 - Cryptographic Failures

```kotlin
// ─────────────────────────────────────────────────────────────────
// Cryptography Tests
// ─────────────────────────────────────────────────────────────────

@Test
fun `password should never be returned in any response`() {
    val createdUser = userService.create(testUserDto)

    // Get by ID
    val userResponse = mockMvc.get("/api/users/${createdUser.id}")
        .andExpect(status().isOk)
        .andReturn()

    assertThat(userResponse.response.contentAsString)
        .doesNotContain("password")
        .doesNotContain("secret")
        .doesNotContain("token")

    // List users
    val listResponse = mockMvc.get("/api/users")
        .andExpect(status().isOk)
        .andReturn()

    assertThat(listResponse.response.contentAsString)
        .doesNotContain("password")
}

@Test
fun `jwt should use RS256 algorithm`() {
    val token = jwtService.generateToken("admin@test.cl", "ADMIN")

    val decoded = JWT.decode(token)
    assertThat(decoded.algorithm).isEqualTo("RS256")
    assertThat(decoded.algorithm).isNotEqualTo("HS256")
}

@Test
fun `tokens should have appropriate expiration`() {
    val accessToken = jwtService.generateAccessToken(userId = 1)
    val refreshToken = jwtService.generateRefreshToken(userId = 1)

    val accessDecoded = JWT.decode(accessToken)
    val refreshDecoded = JWT.decode(refreshToken)

    // Access token: 15 minutes
    val accessExpiry = Duration.between(
        Instant.now(),
        accessDecoded.expiresAt.toInstant()
    )
    assertThat(accessExpiry.toMinutes()).isBetween(14, 16)

    // Refresh token: 7 days
    val refreshExpiry = Duration.between(
        Instant.now(),
        refreshDecoded.expiresAt.toInstant()
    )
    assertThat(refreshExpiry.toDays()).isBetween(6, 8)
}
```

### A03 - Injection

```kotlin
// ─────────────────────────────────────────────────────────────────
// Injection Prevention Tests
// ─────────────────────────────────────────────────────────────────

@Test
fun `sql injection attempt should be prevented`() {
    val maliciousInputs = listOf(
        "' OR '1'='1",
        "'; DROP TABLE users; --",
        "1; DELETE FROM users WHERE 1=1",
        " UNION SELECT * FROM users--",
        "1' AND '1'='1"
    )

    maliciousInputs.forEach { malicious ->
        mockMvc.get("/api/users?search=$malicious")
            .andExpect(status().isBadRequest)

        mockMvc.post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"email": "$malicious", "name": "test"}""")
            .andExpect(status().isBadRequest)
    }
}

@Test
fun `xss in user input should be sanitized`() {
    val xssPayloads = listOf(
        "<script>alert('XSS')</script>",
        "<img src=x onerror=alert('XSS')>",
        "javascript:alert('XSS')",
        "<svg onload=alert('XSS')>",
        "{{constructor.constructor('alert(1)')()}}"
    )

    xssPayloads.forEach { payload ->
        val user = userService.create(
            testUserDto.copy(name = payload)
        )

        // Verificar que está sanitizado
        assertThat(user.name)
            .doesNotContain("<script")
            .doesNotContain("javascript:")
            .doesNotContain("onerror")

        // Verificar que se guarda de forma segura
        val fromDb = userRepository.findById(user.id).get()
        assertThat(fromDb.name)
            .doesNotContain("<script")
    }
}

@Test
fun `command injection attempt should be blocked`() {
    val maliciousInputs = listOf(
        "; rm -rf /",
        "| cat /etc/passwd",
        "`whoami`",
        "$(ls)"
    )

    maliciousInputs.forEach { input ->
        mockMvc.post("/api/system/execute")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"command": "$input"}""")
            .andExpect(status().isForbidden)
    }
}

@Test
fun `path traversal attempt should be blocked`() {
    val pathTraversalAttempts = listOf(
        "../../../etc/passwd",
        "..\\..\\..\\windows\\system32\\config\\sam",
        "%2e%2e%2f%2e%2e%2f%2e%2e%2fetc%2fpasswd",
        "....//....//....//etc/passwd"
    )

    pathTraversalAttempts.forEach { path ->
        mockMvc.get("/api/files?path=$path")
            .andExpect(status().isForbidden)
    }
}
```

### A04 - Insecure Design

```────────────────────────────────────────────────────────────────
// Rate Limiting Tests
// ─────────────────────────────────────────────────────────────────

@Test
fun `rate limiting should block excessive requests`() {
    val clientIp = "192.168.1.100"

    // Ejecutar 101 requests (límite: 100/min)
    repeat(100) { i ->
        mockMvc.get("/api/products")
            .request { it.remoteAddr = clientIp }
            .andExpect(status().isOk)
    }

    // 101 debería ser bloqueado
    mockMvc.get("/api/products")
        .request { it.remoteAddr = clientIp }
        .andExpect(status().isTooManyRequests)
}

@Test
fun `rate limiting should be per-client not global`() {
    // Cliente A hace 100 requests - debe ser bloqueado
    repeat(100) {
        mockMvc.get("/api/products")
            .request { it.remoteAddr = "192.168.1.100" }
    }

    mockMvc.get("/api/products")
        .request { it.remoteAddr = "192.168.1.100" }
        .andExpect(status().isTooManyRequests)

    // Cliente B debería poder hacer requests (no debería estar bloqueado)
    mockMvc.get("/api/products")
        .request { it.remoteAddr = "192.168.1.101" }
        .andExpect(status().isOk)
}
```

### A05 - Security Misconfiguration

```kotlin
// ─────────────────────────────────────────────────────────────────
// Security Headers Tests
// ─────────────────────────────────────────────────────────────────

@Test
fun `security headers should be present on all responses`() {
    val response = mockMvc.get("/api/public/products")
        .andReturn()

    val headers = response.response

    // X-Content-Type-Options
    assertThat(headers.getHeader("X-Content-Type-Options"))
        .isEqualTo("nosniff")

    // X-Frame-Options
    assertThat(headers.getHeader("X-Frame-Options"))
        .isEqualTo("DENY")

    // X-XSS-Protection (deprecated but still good to have)
    assertThat(headers.getHeader("X-XSS-Protection"))
        .isEqualTo("1; mode=block")

    // Content-Security-Policy
    assertThat(headers.getHeader("Content-Security-Policy"))
        .contains("default-src 'self'")

    // Strict-Transport-Security
    assertThat(headers.getHeader("Strict-Transport-Security"))
        .contains("max-age=")
}

@Test
fun `error responses should not leak sensitive information`() {
    val response = mockMvc.get("/api/nonexistent-resource")
        .andExpect(status().isNotFound)
        .andReturn()

    val body = response.response.contentAsString.lowercase()

    assertThat(body).doesNotContain("at com.siga.")
    assertThat(body).doesNotContain("caused by:")
    assertThat(body).doesNotContain("localhost")
    assertThat(body).doesNotContain("127.0.0.1")
    assertThat(body).doesNotContain("postgresql")
    assertThat(body).doesNotContain("connection")
    assertThat(body).doesNotContain("stack trace")
    assertThat(body).doesNotContain(".sql")
}

@Test
fun `cors should only allow configured origins`() {
    val allowedOrigins = listOf(
        "https://siga.cl",
        "https://app.siga.cl"
    )

    mockMvc.options("/api/products")
        .header("Origin", "https://siga.cl")
        .header("Access-Control-Request-Method", "GET")
        .andExpect { result ->
            val response = result.response
            assertThat(response.getHeader("Access-Control-Allow-Origin"))
                .isIn(allowedOrigins)
        }

    // Origin no permitido
    mockMvc.options("/api/products")
        .header("Origin", "https://evil-site.com")
        .header("Access-Control-Request-Method", "GET")
        .andExpect { result ->
            val response = result.response
            assertThat(response.getHeader("Access-Control-Allow-Origin"))
                .isNull()
        }
}
```

### A06 - Vulnerable Components

```kotlin
// ─────────────────────────────────────────────────────────────────
// Dependency Security Tests
// ─────────────────────────────────────────────────────────────────

// Estos tests verifican que el proyecto no use componentes con vulnerabilidades conocidas
// Se ejecutan en CI, no en tests unitarios

// build.gradle.kts debería incluir:
// id("org.owasp.dependencycheck") version "8.4.0"

@Test
fun `no dependencies with known vulnerabilities`() {
    // Este test se ejecuta en CI con dependency-check
    // Aquí solo verificamos que el plugin esté configurado

    val dependencyCheckTask = project.tasks.named("dependencyCheckAnalyze")

    assertThat(dependencyCheckTask).isNotNull()
}
```

### A07 - Auth Failures

```kotlin
// ─────────────────────────────────────────────────────────────────
// Authentication Failure Tests
// ─────────────────────────────────────────────────────────────────

@Test
fun `invalid credentials should return generic error`() {
    // Nunca revelar si el email existe o no
    val response1 = mockMvc.post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""{"email": "nonexistent@test.cl", "password": "any"}""")
        .andReturn()

    val response2 = mockMvc.post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""{"email": "real@test.cl", "password": "wrong"}""")
        .andReturn()

    // Ambos deben retornar el mismo mensaje
    assertThat(response1.response.contentAsString)
        .isEqualTo(response2.response.contentAsString)

    // No debe indicar si el email existe
    assertThat(response1.response.contentAsString)
        .doesNotContain("user not found")
        .doesNotContain("email not found")
        .contains("invalid credentials")
}

@Test
fun `session should be invalidated on logout`() {
    val token = jwtService.generateToken("user@test.cl", tenantId = 1)

    // Logout
    mockMvc.post("/api/auth/logout")
        .header("Authorization", "Bearer $token")
        .andExpect(status().isOk)

    // Token debería estar revocado
    mockMvc.get("/api/protected")
        .header("Authorization", "Bearer $token")
        .andExpect(status().isUnauthorized)
}
```

---

## Ejemplos por Servicio

### Auth Service

```kotlin
// services/auth/src/test/kotlin/com/siga/auth/security/
// AuthSecurityTest.kt

class AuthSecurityTest : DescribeSpec({

    describe("Auth Service Security") {

        describe("Registration") {
            it("security_duplicate_email_should_not_reveal_existence")
            it("security_email_should_be_validated")
            it("security_password_should_not_be_logged")
        }

        describe("Login") {
            it("security_invalid_credentials_should_return_generic_error")
            it("security_account_should_lock_after_5_attempts")
            it("security_brute_force_should_be_detected")
        }

        describe("Token Management") {
            it("security_expired_token_should_be_rejected")
            it("security_revoked_token_should_be_rejected")
            it("security_tampered_token_should_be_rejected")
        }
    }
})
```

### Inventory Service

```kotlin
// services/inventory/src/test/kotlin/com/siga/inventory/security/
// InventorySecurityTest.kt

class InventorySecurityTest : DescribeSpec({

    describe("Inventory Service Security") {

        describe("Product Access") {
            it("security_user_should_only_access_own_tenant_products")
            it("security_products_should_not_leak_pricing_to_competitors")
        }

        describe("Stock Operations") {
            it("security_stock_update_requires_proper_authorization")
            it("security_stock_adjustment_requires_audit")
        }
    }
})
```

### Sales Service

```kotlin
// services/sales/src/test/kotlin/com/siga/sales/security/
// SalesSecurityTest.kt

class SalesSecurityTest : DescribeSpec({

    describe("Sales Service Security") {

        describe("POS Operations") {
            it("security_pos_should_verify_store_ownership")
            it("security_cash_movement_requires_manager_approval")
        }

        describe("Invoice Generation") {
            it("security_invoice_should_only_contain_own_tenant_data")
            it("security_invoice_numbers_should_be_sequential")
        }
    }
})
```

---

## Integration Tests de Seguridad

```kotlin
// services/common/src/test/kotlin/com/siga/common/security/
// SecurityIntegrationTest.kt

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var jwtService: JwtService

    @Test
    fun `end_to_end_attack_simulation`() {
        // Simular ataque completo

        // 1. Intentar acceso sin auth
        mockMvc.get("/api/products")
            .andExpect(status().isUnauthorized)

        // 2. Intentar con token inválido
        mockMvc.get("/api/products")
            .header("Authorization", "Bearer invalid-token")
            .andExpect(status().isUnauthorized)

        // 3. Intentar con SQL injection
        mockMvc.get("/api/products?search=1' OR '1'='1")
            .andExpect(status().isBadRequest)

        // 4. Intentar con XSS
        mockMvc.post("/api/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"name": "<script>alert(1)</script>"}""")
            .andExpect(status().isBadRequest)

        // 5. Todo debería pasar con token válido
        val validToken = jwtService.generateToken("user@test.cl", tenantId = 1)
        mockMvc.get("/api/products")
            .header("Authorization", "Bearer $validToken")
            .andExpect(status().isOk)
    }
}
```

---

## Complemento con SAST

### .semgrep.yaml

```yaml
rules:
  - id: kotlin-secrets-in-code
    pattern: |
      val $KEY = "$STRINGLIT"
      ...
      if ($KEY.contains(...))
    message: |
      Possible hardcoded secret detected. Use environment variables instead.
    severity: ERROR
    languages:
      - kotlin

  - id: kotlin-sql-injection
    pattern: |
      entityManager.createNativeQuery($SQL + $INPUT)
    message: |
      Possible SQL injection. Use parameterized queries instead.
    severity: ERROR
    languages:
      - kotlin

  - id: kotlin-weak-crypto
    pattern: |
      MessageDigest.getInstance("MD5")
      MessageDigest.getInstance("SHA1")
    message: |
      Weak cryptographic algorithm detected. Use SHA-256 or stronger.
    severity: WARNING
    languages:
      - kotlin
```

### Ejecución en CI

```bash
# .github/workflows/security.yml incluye:
- name: Run Semgrep
  run: |
    semgrep --config=.semgrep.yaml --json --output=semgrep.json .

- name: Check for critical issues
  run: |
    if grep -q '"severity": "ERROR"' semgrep.json; then
      echo "Critical security issues found!"
      exit 1
    fi
```

---

## Checklist de Security Tests

### Por cada feature nueva:

- [ ] `security_*` tests para cada OWASP category aplicable
- [ ] Tests de access control por tenant
- [ ] Tests de input validation
- [ ] Tests de rate limiting (si aplica)
- [ ] Tests de headers de seguridad

### Antes de merge:

- [ ] Todos los security tests pasando
- [ ] Semgrep sin errores críticos
- [ ] Trivy sin vulnerabilidades CRITICAL/HIGH
- [ ] Gitleaks no detecta secrets

### Cobertura mínima:

| Category | Cobertura requerida |
|----------|---------------------|
| A01 - Access Control | 100% |
| A02 - Crypto | 100% |
| A03 - Injection | 100% |
| A05 - Config | 85% |
| A07 - Auth | 100% |

---

## Recursos

- [OWASP Testing Guide](https://owasp.org/www-project-web-security-testing-guide/)
- [SAST Tools Comparison](https://owasp.org/www-communitySAMM_Tooling_SAST)
- [Kotest Documentation](https://kotest.io/docs/)
- [MockK Documentation](https://mockk.io/)