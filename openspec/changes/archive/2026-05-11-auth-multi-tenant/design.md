# Design: Auth Multi-Tenant

## Technical Approach

Add registration, email verification, and dual-principal login (Customer or User) to the existing auth skeleton. Hexagonal architecture: domain model changes ripple through entity → mapper → use case → controller. JWT carries `principalType` to distinguish login origin. Spring Security filter chain protects all endpoints except register/verify/login.

## Architecture Decisions

| # | Decision | Choice | Alternatives | Rationale |
|---|----------|--------|-------------|-----------|
| 1 | Principal identity in JWT | `principalType: "customer"\|"user"` claim | Separate endpoints per type | Single `/login` + claim keeps routing simple; client logic based on `principalType` |
| 2 | Login lookup order | Customer first, User second | User first, union query | Customers are business owners (higher privilege); fail-fast on inactive Customer |
| 3 | Verification storage | `verificationToken`, `verificationTokenExpiresAt`, `emailVerified` on Customer entity | Separate `verification_tokens` table | Avoids join for the most common path; columns nullable to avoid migration pain. Revisit if token rotation becomes complex |
| 4 | Email integration | `EmailSenderPort` hexagonal interface → `GmailSmtpAdapter` | Direct JavaMailSender in use case | Port isolates mail dependency; test double in unit tests, real adapter in prod |
| 5 | User CRUD scoping | Extract `customerId` from JwtAuthFilter's SecurityContext | Pass customerId as request param | Prevents tenant spoofing; the token IS the auth source |
| 6 | Existing CustomerController | Keep as-is at `/api/auth/customers` | Remove or protect | Backward compat (NFR-1); new register goes to `/api/v1/auth/register` |
| 7 | Testing framework | JUnit 5 + Mockito (existing convention) | Kotest (per config.yaml) | Existing tests all use JUnit5 + Mockito; consistency > config aspirational goal |

## Authentication Flow

```
Registration:
  Client ──POST /api/v1/auth/register──→ AuthController
       ↓
  RegisterCustomerUseCase
       ↓ (hash password, generate UUID token, set isActive=false)
  CustomerRepositoryPort.save()
       ↓
  EmailSenderPort.sendVerification()
       ↓
  201 { status: "pending" }

Verification:
  Client ──GET /api/v1/auth/verify?token=xxx──→ AuthController
       ↓
  VerifyEmailUseCase
       ↓ (find by token, check expiry vs Instant.now, set isActive=true)
  CustomerRepositoryPort.save()
       ↓
  200 OK

Login:
  Client ──POST /api/v1/auth/login──→ AuthController
       ↓
  LoginUseCase
       ↓ 1. CustomerRepositoryPort.findByEmail()
       ├── found, isActive=true, BCrypt matches → JWT(principalType=customer)
       ├── found, isActive=false → 403
       ├── found, BCrypt fails → 401
       └── not found → 2. UserRepositoryPort.findByEmail()
            ├── found, BCrypt matches → JWT(principalType=user)
            └── not found/mismatch → 401
```

## Data Flow — Registration → Verification → Login

```
[Client]                  [AuthController]          [Use Case]              [EmailSender]         [DB]
   │                           │                       │                       │                    │
   ├─ POST /register ─────────→│                       │                       │                    │
   │                           ├── RegisterCustomer ──→│                       │                    │
   │                           │                       ├── hashPassword()      │                    │
   │                           │                       ├── generateToken()     │                    │
   │                           │                       ├── save(pending) ──────│─────────────────→  │
   │                           │                       ├── sendVerification ──→│                    │
   │                           │                       │                       ├── SMTP ──────────→ [Gmail]
   │                           │                       │                       │← OK                │
   │                           │←── 201 pending ──────│                       │                    │
   │← 201 {status: "pending"} │                       │                       │                    │
   │                           │                       │                       │                    │
   ├─ GET /verify?token= ─────→│                       │                       │                    │
   │                           ├── VerifyEmail ──────→│                       │                    │
   │                           │                       ├── findByToken() ─────│─────────────────→  │
   │                           │                       │← Customer             │                    │
   │                           │                       ├── checkExpiry()       │                    │
   │                           │                       ├── activate()          │                    │
   │                           │                       ├── save(active) ──────│─────────────────→  │
   │                           │←── 200 OK ──────────│                       │                    │
   │← 200                     │                       │                       │                    │
   │                           │                       │                       │                    │
   ├─ POST /login ───────────→│                       │                       │                    │
   │                           ├── Login ────────────→│                       │                    │
   │                           │                       ├── findByEmail(Cx) ───│─────────────────→  │
   │                           │                       │← Customer             │                    │
   │                           │                       ├── BCrypt.verify()     │                    │
   │                           │                       ├── generateJWT()       │                    │
   │                           │←── 200 + JWT ───────│                       │                    │
   │← 200 {token, ...}        │                       │                       │                    │
```

## Model Changes

### Customer (domain) — new fields
```kotlin
data class Customer(
    // ... existing fields unchanged ...
    val verificationToken: String? = null,
    val verificationTokenExpiresAt: Instant? = null,
    val emailVerified: Boolean = false
)
```

### Customer (entity) — new fields
```kotlin
@Column(name = "verification_token", length = 255)
var verificationToken: String? = null,

@Column(name = "verification_token_expires_at")
var verificationTokenExpiresAt: Instant? = null,

@Column(name = "email_verified", nullable = false)
var emailVerified: Boolean = false
```

### User (domain) — new field
```kotlin
data class User(
    // ... existing fields unchanged ...
    val customerId: Int? = null
)
```

### User (entity) — new field
```kotlin
@Column(name = "customer_id")
var customerId: Int? = null
```

## Spring Security Configuration

| Path | Method | Auth | Notes |
|------|--------|------|-------|
| `/api/v1/auth/register` | POST | permitAll | |
| `/api/v1/auth/verify` | GET | permitAll | Query param token |
| `/api/v1/auth/login` | POST | permitAll | |
| `/api/v1/auth/users/**` | ANY | authenticated | Scoped by JWT customerId |
| `/api/auth/customers/**` | ANY | permitAll | Legacy, kept for backward compat |
| Swagger/OpenAPI | GET | permitAll | `/swagger-ui/**`, `/v3/api-docs/**` |
| Health | GET | permitAll | `/actuator/health` |
| All others | ANY | authenticated | |

SecurityFilterChain order: permitAll paths → JwtAuthFilter → authenticate remaining.

## Endpoint Mapping

### New endpoints in `AuthController`

| Method | Path | Auth | Request | Response | Errors |
|--------|------|------|---------|----------|--------|
| POST | `/api/v1/auth/register` | None | `{ email, password, name, companyName }` | `201 { status: "pending" }` | 400, 409 |
| GET | `/api/v1/auth/verify` | None | Query: `?token=uuid` | `200 OK` | 404, 410 |
| POST | `/api/v1/auth/login` | None | `{ email, password }` | `200 { token, principalType, tenantId }` | 401, 403 |

### Modified endpoints in `UserController`

| Method | Path | Auth Change | Behavior Change |
|--------|------|-------------|-----------------|
| GET | `/api/v1/auth/users` | Now authenticated | Filter by `customerId` from JWT |
| POST | `/api/v1/auth/users` | Now authenticated | Auto-set `customerId` from JWT |
| GET/PUT | `/api/v1/auth/users/{id}` | Now authenticated | Only if user belongs to same customerId |

## New Files to Create

| File | Purpose |
|------|---------|
| `application/usecase/RegisterCustomerUseCase.kt` | Validate, hash, create pending Customer, send verification |
| `application/usecase/VerifyEmailUseCase.kt` | Find by token, check expiry, activate |
| `application/usecase/LoginUseCase.kt` | Dual lookup (Customer→User), BCrypt verify, JWT generation |
| `domain/port/EmailSenderPort.kt` | Hexagonal port for email sending |
| `infrastructure/adapter/GmailSmtpAdapter.kt` | JavaMailSender implementation of EmailSenderPort |
| `security/SecurityConfig.kt` | SecurityFilterChain, BCrypt bean, permitAll paths |
| `security/JwtAuthFilter.kt` | OncePerRequestFilter: extract JWT, validate, set SecurityContext |
| `controller/AuthController.kt` | register, verify, login endpoints |
| `db/migration/V2__auth_multi_tenant.sql` | Add verification columns to customers, customerId to users |

## Existing Files to Modify

| File | Changes |
|------|---------|
| `domain/model/Customer.kt` | Add `verificationToken: String?`, `verificationTokenExpiresAt: Instant?`, `emailVerified: Boolean` |
| `entity/Customer.kt` | Add matching JPA columns |
| `infrastructure/mapper/CustomerMapper.kt` | Map new fields both directions |
| `domain/model/User.kt` | Add `customerId: Int?` |
| `entity/User.kt` | Add `customerId` column |
| `infrastructure/mapper/UserMapper.kt` | Map new field both directions |
| `security/JwtService.kt` | Add `verify(token: String): DecodedJWT` method; add `principalType` to `generateToken()` |
| `application/usecase/ManageUserUseCase.kt` | Add `findByCustomerId(customerId: Int): List<User>` |
| `domain/port/UserRepositoryPort.kt` | Add `findByCustomerId(customerId: Int): List<User>` |
| `infrastructure/adapter/UserJpaAdapter.kt` | Implement `findByCustomerId` |
| `repository/UserRepository.kt` | Add Spring Data method `findByCustomerId` |
| `controller/UserController.kt` | Add auth check; scope CRUD by JWT customerId |
| `build.gradle.kts` | Add `spring-boot-starter-mail` dependency |
| `application.yml` | Add `spring.mail` config for Gmail SMTP |

## Email Integration

- **Port**: `EmailSenderPort` with `sendVerification(email: String, token: String, name: String)`
- **Adapter**: `GmailSmtpAdapter` using `JavaMailSender` with Gmail SMTP config:
  ```
  spring.mail.host=smtp.gmail.com
  spring.mail.port=587
  spring.mail.username=${SMTP_USER}
  spring.mail.password=${SMTP_PASSWORD}
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
  ```
- **Template**: Plain-text or simple HTML: "Click to verify: {baseUrl}/api/v1/auth/verify?token={token}"
- **Async**: Send sync for now; defer to Spring `@Async` if latency becomes an issue

## Migration Strategy

- **V2 Flyway migration**: `ALTER TABLE auth.customers ADD COLUMN ...` (all nullable → existing rows not affected)
- **ALTER TABLE auth.users ADD COLUMN customer_id INTEGER NULL** → existing users get null (admin scope)
- **Existing CustomerController** at `/api/auth/customers` remains untouched — backward compatible
- **Existing users** with null `customerId` are treated as global/admin — no customer scope filter applied
- **Existing customers** have `emailVerified=false` — they remain active via existing `isActive=true` until email verification is enforced later

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit (Use Cases) | RegisterCustomerUseCase, VerifyEmailUseCase, LoginUseCase | JUnit 5 + Mockito (existing convention). Mock ports, verify interactions. Login dual-path scenarios |
| Unit (Security) | JwtAuthFilter (valid/invalid/expired token), JwtService.verify() | JUnit 5, test token creation and verification |
| Integration | AuthController (register→verify→login chain) | MockMvc with `@AutoConfigureMockMvc`, H2 in-memory DB. Security filter ON for auth flow tests |
| Integration | UserController scoped by customerId | MockMvc with JWT in Authorization header |
| Adapter | GmailSmtpAdapter | GreenMail or similar test mail server; defer if low priority |

## Open Questions

None — all decisions resolved in proposal/spec.

## Threat Model

| Threat | Mitigation |
|--------|------------|
| Token replay | 24h JWT expiry; no token refresh in scope |
| Email verification bypass | Customer starts `isActive=false`; login rejects inactive |
| Tenant spoofing in User CRUD | `customerId` extracted from JWT, never from request body |
| Password exposure | BCrypt everywhere; `passwordHash` never returned by any endpoint |
| Verification token brute-force | UUID v4 tokens (122 bits entropy); 24h expiry limits window |
