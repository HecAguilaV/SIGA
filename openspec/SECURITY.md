# ═══════════════════════════════════════════════════════════════════════════
# SIGA - Seguridad y OWASP
# Implementación práctica de cybersecurity para microservicios
# ═══════════════════════════════════════════════════════════════════════════

# Tabla de Contenidos
1. [Filosofía](#filosofía)
2. [OWASP Top 10 - Mitigaciones](#owasp-top-10---mitigaciones)
3. [Arquitectura de Seguridad](#arquitectura-de-seguridad)
4. [JWT Security](#jwt-security)
5. [Dependency Security](#dependency-security)
6. [Secret Management](#secret-management)
7. [API Security](#api-security)
8. [Logging de Seguridad](#logging-de-seguridad)

---

## Filosofía

> "Security is not a product, but a process." — Bruce Schneier

### Principios fundamentales:

1. **Defense in Depth**: Múltiples capas de seguridad
2. **Least Privilege**: Solo permisos necesarios
3. **Secure by Default**: La opción segura es la默认值
4. **Fail Securely**: Si algo falla, que falle de forma segura

---

## OWASP Top 10 - Mitigaciones

### A01 - Broken Access Control ⭐ CRÍTICO

```
Vulnerabilidad: Usuario puede acceder a recursos que no debería
```

**Mitigaciones implementadas:**

| Control | Implementación | Test requerido |
|---------|----------------|----------------|
| Authorization layer | Spring Security + JWT | ✅ |
| Resource ownership | Verificar `tenantId` en cada request | ✅ |
| API rate limiting | Spring Cloud Gateway + Bucket4j | ✅ |
| CORS | Configuración explícita de orígenes | ✅ |

**Tests obligatorios:**
```kotlin
@Test
fun `given_user_without_permission when_accessing_resource then_should_return_403`() {
    mockMvc.get("/admin/users")
        .andExpect(status().isForbidden)
}

@Test
fun `given_user_from_tenant_A when_accessing_tenant_B_resource then_should_return_403`() {
    val tokenTenantA = jwtService.generateToken("user@tenantA.cl", tenantId = 1)
    mockMvc.get("/api/stores/2")  // Store de tenant B
        .header("Authorization", "Bearer $tokenTenantA")
        .andExpect(status().isForbidden)
}
```

---

### A02 - Cryptographic Failures 🔒

```
Vulnerabilidad: Datos sensibles sin cifrado o con cifrado débil
```

**Mitigaciones:**

| Dato | Cifrado | Algoritmo |
|------|---------|-----------|
| Contraseñas | ✅ | bcrypt (cost=12) |
| Tokens JWT | ✅ | RS256 (asimétrico) |
| Tokens refresh | ✅ | RS256 + rotación |
| Datos en BD | ✅ | PostgreSQL column encryption |
| backups | ✅ | AES-256 |

**Tests obligatorios:**
```kotlin
@Test
fun `password_should_never_be_returned_in_responses`() {
    val user = userService.create(testUserDto)

    val response = mockMvc.get("/api/users/${user.id}")
        .andReturn()

    assertThat(response.response.contentAsString)
        .doesNotContain("password")
        .doesNotContain("secret")
}

@Test
fun `jwt_should_use_rs256_not_hs256`() {
    val token = jwtService.generateToken("admin@siga.cl", "ADMIN")

    // Verificar que usa RS256 (asimétrico)
    val decoded = com.auth0.jwt.JWT.decode(token)
    val algorithm = decoded.algorithm

    assertThat(algorithm).isEqualTo("RS256")
    assertThat(algorithm).isNotEqualTo("HS256")  // HS256 es peligroso para multi-tenant
}
```

---

### A03 - Injection ⚠️

```
Vulnerabilidad: SQL Injection, NoSQL Injection, Command Injection
```

**Mitigaciones:**

| Capa | Protección | Implementación |
|------|-----------|----------------|
| BD | Parameterized queries | JPA/Hibernate (siempre) |
| Input | Validation | Jakarta Validation |
| Input | Sanitization | HTML escaping para XSS |
| Query | Allowlist | Enum para campos sortables |

**Tests obligatorios:**
```kotlin
@Test
fun `sql_injection_attempt_should_be_prevented`() {
    val maliciousInput = "'; DROP TABLE users; --"

    // Intentar crear usuario con SQL injection
    mockMvc.post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "email": "$maliciousInput",
                "password": "test123"
            }
        """.trimIndent())
        .andExpect(status().isBadRequest)

    // Verificar que no se ejecutó ningún SQL
    verifyNoMoreInteractions(userRepository)
}

@Test
fun `xss_in_user_name_should_be_sanitized`() {
    val xssPayload = "<script>alert('XSS')</script>"

    val user = userService.create(
        testUserDto.copy(name = xssPayload)
    )

    // El nombre debería estar sanitizado
    assertThat(user.name)
        .doesNotContain("<script>")
        .contains("&lt;script&gt;")
}
```

---

### A04 - Insecure Design 🔧

```
Vulnerabilidad: Falta de controles de бизнес-логика
```

**Mitigaciones:**

| Control | Implementación | Test |
|---------|---------------|------|
| Rate limiting | Bucket4j + Redis | ✅ |
| Brute force protection | Lock después de 5 intentos | ✅ |
| MFA | TOTP/Email | ✅ (futuro) |
| Suspicious activity | Logging + alerts | ✅ |

**Tests:**
```kotlin
@Test
fun `should_block_after_5_failed_login_attempts`() {
    val email = "attacker@test.cl"

    repeat(5) {
        authService.login(email, "wrong-password")
    }

    // 6to intento debería ser bloqueado
    assertThatThrownBy { authService.login(email, "password123") }
        .isInstanceOf(AccountLockedException::class.java)
}

@Test
fun `rate_limit_should_block_excessive_requests`() {
    // 100 requests rápidos
    repeat(100) {
        mockMvc.get("/api/products")
    }

    // 101 debería ser bloqueado
    mockMvc.get("/api/products")
        .andExpect(status().isTooManyRequests)
}
```

---

### A05 - Security Misconfiguration ⚠️

```
Vulnerabilidad: Configs por defecto, headers faltantes, debug enabled
```

**Mitigaciones:**

| Config | Valor correcto | Verificación |
|--------|---------------|--------------|
| Debug mode | `false` en prod | ✅ |
| CORS | Orígenes explícitos | ✅ |
| HSTS | Enabled | ✅ |
| X-Frame-Options | DENY | ✅ |
| Content-Type sniffing | nosniff | ✅ |

**Tests:**
```kotlin
@Test
fun `security_headers_should_be_present`() {
    val response = mockMvc.get("/api/products")
        .andReturn()

    assertThat(response.response.getHeader("X-Content-Type-Options"))
        .isEqualTo("nosniff")
    assertThat(response.response.getHeader("X-Frame-Options"))
        .isEqualTo("DENY")
    assertThat(response.response.getHeader("Strict-Transport-Security"))
        .contains("max-age=")
}

@Test
fun `error_responses_should_not_leak_stack_traces`() {
    val response = mockMvc.get("/api/nonexistent")
        .andExpect(status().isNotFound)
        .andReturn()

    val body = response.response.contentAsString

    assertThat(body).doesNotContain("at com.siga.")
    assertThat(body).doesNotContain("Caused by:")
    assertThat(body).doesNotContain("localhost:5432")
}
```

---

### A06 - Vulnerable Components ⚠️

```
Vulnerabilidad: Dependencias con vulnerabilidades conocidas
```

**Mitigación:**

```yaml
# trivy.yaml
vulnerability:
  security-checks:
    - vuln
    - misconfig
  severity:
    - CRITICAL
    - HIGH
  ignore-unfixed: false
```

**CI Pipeline:**
```bash
# En cada PR
trivy fs --severity HIGH,CRITICAL --exit-code 1 .
trivy image --severity HIGH,CRITICAL --exit-code 1 ${IMAGE_TAG}
```

---

### A07 - Identification and Authentication Failures 🔑

```
Vulnerabilidad: Autenticación débil, gestión de sesiones deficiente
```

**Mitigaciones:**

| Control | Implementación | Test |
|---------|---------------|------|
| Password policy | 8+ chars, mayúscula, número, especial | ✅ |
| JWT expiration | 15min access, 7d refresh | ✅ |
| Session invalidation | Revocación en logout | ✅ |
| Password history | Últimos 5 no reutilizables | ✅ |

**Tests:**
```kotlin
@Test
fun `weak_password_should_be_rejected`() {
    assertThatThrownBy {
        authService.register(
            testUserDto.copy(password = "12345678")
        )
    }.isInstanceOf(WeakPasswordException::class.java)
}

@Test
fun `jwt_should_expire_after_15_minutes`() {
    val token = jwtService.generateToken("user@test.cl", tenantId = 1)
    val decoded = com.auth0.jwt.JWT.decode(token)

    val exp = decoded.expiresAt
    val now = Instant.now()

    val duration = Duration.between(now, exp)

    assertThat(duration.toMinutes()).isBetween(14, 16)
}

@Test
fun `logout_should_invalidate_token`() {
    val token = jwtService.generateToken("user@test.cl", tenantId = 1)

    authService.logout(token)

    assertThatThrownBy { jwtService.validate(token) }
        .isInstanceOf(TokenRevokedException::class.java)
}
```

---

### A08 - Software and Data Integrity Failures ⚠️

```
Vulnerabilidad: Código/datos sin verificar integridad
```

**Mitigaciones:**

| Control | Implementación |
|---------|---------------|
| Firmado de imágenes | Docker Content Trust |
| Verificación de hashes | SHA-256 en downloads |
| Firmado de commits | Require signed commits |
| SBOM | CycloneDX para dependencies |

---

### A09 - Security Logging and Monitoring 🔍

```
Vulnerabilidad: Ataques no detectados
```

**Eventos a loggear:**

| Evento | Severity | Datos |
|--------|----------|-------|
| Login exitoso | INFO | user, ip, timestamp |
| Login fallido | WARN | user, ip, reason |
| Logout | INFO | user, timestamp |
| Permiso denegado | WARN | user, resource, action |
| Rate limit triggered | WARN | ip, endpoint |
| Cuenta bloqueada | WARN | user, timestamp |

**Implementación:**
```kotlin
@Aspect
@Component
class SecurityAuditLogger {

    @AfterReturning(pointcut = "securityAnnotatedMethod()", returning = "result")
    fun logSecurityEvent(result: Any) {
        auditLogger.info("Security action: $result")
    }

    @AfterThrowing(pointcut = "securityAnnotatedMethod()", throwing = "ex")
    fun logSecurityFailure(ex: Exception) {
        auditLogger.warn("Security failure: ${ex.message}", ex)
    }
}
```

---

### A10 - Server-Side Request Forgery (SSRF) 🌐

```
Vulnerabilidad: App hace requests a URLs controladas por usuario
```

**Mitigaciones:**

```kotlin
fun validateUrl(inputUrl: String): Boolean {
    val url = URL(inputUrl)

    // Bloquear URLs internas
    val host = url.host.lowercase()
    if (host == "localhost" ||
        host == "127.0.0.1" ||
        host.startsWith("192.168.") ||
        host.startsWith("10.") ||
        host.startsWith("172.")) {
        throw SSRFException("Internal URLs not allowed")
    }

    // Allowlist de dominios permitidos
    val allowedDomains = config.allowedWebhooks
    if (allowedDomains.none { host.endsWith(it) }) {
        throw SSRFException("Domain not in allowlist")
    }

    return true
}
```

**Test:**
```kotlin
@Test
fun `ssrf_attempt_to_internal_ip_should_be_blocked`() {
    assertThatThrownBy {
        webhookService.fetch("${BASE_URL}http://169.254.169.254/latest/meta-data/")
    }.isInstanceOf(SSRFException::class.java)
}
```

---

## Arquitectura de Seguridad

```
┌─────────────────────────────────────────────────────────────────┐
│                         API Gateway                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Rate     │  │ Auth     │  │ CORS     │  │ Security │        │
│  │ Limiting │  │ Filter   │  │ Filter   │  │ Headers  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                       Auth Service                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ JWT Gen  │  │ Password │  │ Session  │  │ MFA      │        │
│  │ RS256    │  │ Hashing  │  │ Mgmt     │  │ TOTP     │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      Business Services                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Access   │  │ Input    │  │ Tenant   │  │ Audit    │        │
│  │ Control  │  │ Valid.   │  │ Filter   │  │ Logging  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└─────────────────────────────────────────────────────────────────┘
```

---

## JWT Security

### Flujo de tokens:

```
┌────────┐                    ┌────────────┐                    ┌────────┐
│ Client │                    │ Auth Svc   │                    │ DB     │
└───┬────┘                    └─────┬──────┘                    └───┬────┘
    │                                  │                            │
    │  1. POST /auth/login             │                            │
    │  ───────────────────────────────→│                            │
    │                                  │  2. Verify credentials     │
    │                                  │───────────────────────────→│
    │                                  │                            │
    │                                  │  3. Generate tokens        │
    │                                  │  ←─────────────────────────│
    │  4. {access_token,               │                            │
    │      refresh_token}              │                            │
    │←──────────────────────────────────│                            │
    │                                  │                            │
    │  5. GET /api/resource            │                            │
    │  Authorization: Bearer {access}   │                            │
    │  ───────────────────────────────→│                            │
    │                                  │  6. Validate JWT           │
    │                                  │───────────────────────────→│
    │                                  │                            │
    │  7. {data}                       │                            │
    │←──────────────────────────────────│                            │
    │                                  │                            │
    │  8. POST /auth/refresh           │                            │
    │  ───────────────────────────────→│                            │
    │                                  │  9. Revoke old refresh     │
    │                                  │───────────────────────────→│
    │                                  │                            │
    │  10. {new_access_token,          │                            │
    │       new_refresh_token}         │                            │
    │←──────────────────────────────────│                            │
```

### Rotación de keys (JWKS):

```kotlin
@Service
class JwksService {

    private val keyStore = KeyStore.getInstance("PKCS12")
    private var currentKeyId: String = UUID.randomUUID().toString()

    fun getPublicKey(): PublicKey { ... }
    fun rotateKeys() {
        // Generar nuevo par de keys
        // Mantener keys antiguas para validación
        // Actualizar currentKeyId
        // Log de auditoría
    }
}
```

---

## Dependency Security

### Herramientas:

| Herramienta | Propósito | Frecuencia |
|-------------|-----------|------------|
| **trivy** | Escaneo de vulnerabilidades | Cada PR |
| **OWASP Dependency-Check** | Análisis de dependencies | Cada build |
| **snyk** | Monitoreo continuo | Daily |
| **Renovate** | Auto-updates de deps | Weekly |

### Configuración trivy:

```yaml
# .trivy.yaml
format: table
severity:
  - CRITICAL
  - HIGH
security-checks:
  - vuln
ignore-unfixed: true
```

---

## Secret Management

### NO HACER:
```kotlin
// ❌ MAL - Secrets hardcoded
class MyService {
    private val apiKey = "sk-1234567890abcdef"  // PELIGRO!
}

// ❌ MAL - Secrets en git
// database password en credentials.md

// ❌ MAL - Secrets en logs
logger.info("Connecting with password: $password")
```

### HACER:
```kotlin
// ✅ BIEN - Variables de entorno
class MyService(
    @Value("\${api.key}") private val apiKey: String
)

// ✅ BIEN - Vault
class MyService(
    private val vaultClient: VaultClient
) {
    private val apiKey = vaultClient.getSecret("api-key")
}

// ✅ BIEN - Kubernetes Secrets
@Configuration
class SecretsConfig {
    @Bean
    fun dbCredentials(secrets: MutableMap<String, Secret>) =
        DbCredentials(
            username = secrets["db-username"]?.toString() ?: "",
            password = secrets["db-password"]?.toString() ?: ""
        )
}
```

---

## API Security

### Rate Limiting (Bucket4j + Redis):

```kotlin
@Configuration
class RateLimitingConfig {

    @Bean
    fun rateLimitFilter(
        registry: RedisRateLimiter
    ): FilterRegistrationBean<RateLimitFilter> {
        return FilterRegistrationBean<RateLimitFilter>().apply {
            filter = RateLimitFilter(registry)
            addUrlPatterns("/api/*")
            order = 1
        }
    }
}

class RateLimitFilter(private val registry: RedisRateLimiter) : OncePerRequestFilter() {

    override fun shouldNotFilter(req: HttpServletRequest): Boolean {
        return req.requestURI.startsWith("/actuator/health")
    }

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
        val clientIp = req.getClientIP()
        val bucket = registry.resolveBucket(clientIp)

        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res)
        } else {
            res.status = HttpStatus.TOO_MANY_REQUESTS.value()
            res.writer.write("Rate limit exceeded")
        }
    }
}
```

### CORS Configuración:

```kotlin
@Configuration
class CorsConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "https://siga.cl",
                "https://www.siga.cl",
                "https://app.siga.cl"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
```

---

## Logging de Seguridad

### Estructura de logs:

```json
{
  "timestamp": "2024-04-24T10:30:00Z",
  "level": "WARN",
  "event": "LOGIN_FAILED",
  "user": "attacker@test.cl",
  "ip": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "reason": "INVALID_PASSWORD",
  "attemptCount": 3,
  "correlationId": "abc123"
}
```

### Evento de security:

```kotlin
enum class SecurityEvent(
    val code: String,
    val severity: Severity,
    val includeUserData: Boolean
) {
    LOGIN_SUCCESS("AUTH_001", INFO, true),
    LOGIN_FAILED("AUTH_002", WARN, true),
    LOGOUT("AUTH_003", INFO, true),
    PASSWORD_RESET_REQUESTED("AUTH_004", INFO, true),
    PASSWORD_RESET_COMPLETED("AUTH_005", INFO, true),
    ACCOUNT_LOCKED("AUTH_006", WARN, true),
    ACCESS_DENIED("AUTH_007", WARN, true),
    PERMISSION_DENIED("AUTH_008", WARN, true),
    RATE_LIMIT_EXCEEDED("AUTH_009", WARN, false),
    SUSPICIOUS_ACTIVITY("AUTH_010", ERROR, true),
    TOKEN_REVOKED("AUTH_011", INFO, true),
    TOKEN_EXPIRED("AUTH_012", WARN, false);
}
```

### Servicio de auditoría:

```kotlin
@Service
class SecurityAuditService(
    private val auditLogger: SecurityAuditLogger
) {

    fun logLoginSuccess(user: User, request: HttpServletRequest) {
        auditLogger.log(
            event = SecurityEvent.LOGIN_SUCCESS,
            userId = user.id,
            userEmail = user.email,
            ip = request.getClientIP(),
            metadata = mapOf("tenantId" to user.tenantId)
        )
    }

    fun logLoginFailed(email: String, reason: String, request: HttpServletRequest) {
        auditLogger.log(
            event = SecurityEvent.LOGIN_FAILED,
            userEmail = email,
            ip = request.getClientIP(),
            metadata = mapOf("reason" to reason)
        )
    }

    fun logAccessDenied(user: User, resource: String, request: HttpServletRequest) {
        auditLogger.log(
            event = SecurityEvent.ACCESS_DENIED,
            userId = user.id,
            ip = request.getClientIP(),
            metadata = mapOf("resource" to resource)
        )
    }
}
```

---

## Checklist de Seguridad

### Antes de cada deploy:

- [ ] Todos los tests de seguridad pasando
- [ ] `trivy fs` no encuentra vulnerabilidades CRITICAL/HIGH
- [ ] No hay secrets en código (gitleaks)
- [ ] Semgrep no reporta issues críticos
- [ ] Security headers configurados
- [ ] Rate limiting habilitado
- [ ] Logs de auditoría funcionando
- [ ] JWKS rotation configurado
- [ ] Credenciales en vault/variables de entorno

### En cada PR:

- [ ] Tests unitarios pasando
- [ ] Tests de seguridad incluyendo nuevos
- [ ] Coverage >= 80%
- [ ] No nuevas vulnerabilidades en dependencies

---

## Recursos

- [OWASP Top 10](https://owasp.org/Top10/)
- [OWASP Cheat Sheets](https://cheatsheetseries.owasp.org/)
- [NIST Guidelines](https://csrc.nist.gov/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)