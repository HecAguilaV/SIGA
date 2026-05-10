# Tasks: Auth Multi-Tenant

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 800-1200 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (Foundation: Models + Migration + Config) → PR 2 (Security + JWT) → PR 3 (Use Cases + Controllers) |
| Delivery strategy | exception-ok |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: Yes
Chain strategy: size-exception
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Foundation: Models, Migration, Config | PR 1 | Base branch: main; includes Flyway migration, domain/entity/mapper updates, mail config |
| 2 | Security & JWT | PR 2 | Base: PR 1; includes JwtService updates, JwtAuthFilter, SecurityConfig |
| 3 | Use Cases & Controllers | PR 3 | Base: PR 2; includes Register/Verify/Login use cases, AuthController, UserController scoping |

## Phase 1: Foundation (Database + Models + Config)

- [ ] 1.1 Write test: Verify Flyway migration applies cleanly to H2
- [ ] 1.2 Create `db/migration/V2__auth_multi_tenant.sql` (add verification columns to customers, customerId to users)
- [ ] 1.3 Modify `domain/model/Customer.kt` — add `verificationToken`, `verificationTokenExpiresAt`, `emailVerified`
- [ ] 1.4 Modify `entity/Customer.kt` — add matching JPA columns
- [ ] 1.5 Modify `infrastructure/mapper/CustomerMapper.kt` — map new fields bidirectionally
- [ ] 1.6 Modify `domain/model/User.kt` — add `customerId: Int?`
- [ ] 1.7 Modify `entity/User.kt` — add `customer_id` column
- [ ] 1.8 Modify `infrastructure/mapper/UserMapper.kt` — map `customerId`
- [ ] 1.9 Modify `build.gradle.kts` — add `spring-boot-starter-mail` dependency
- [ ] 1.10 Modify `application.yml` — add Gmail SMTP config
- [ ] 1.11 Create `domain/port/EmailSenderPort.kt` — hexagonal interface for email sending

## Phase 2: Security & JWT

- [ ] 2.1 Write test: `JwtService.generateToken()` includes `principalType` claim
- [ ] 2.2 Write test: `JwtService.verify()` validates token signature and expiry
- [ ] 2.3 Modify `security/JwtService.kt` — add `verify()` method, update `generateToken()` to include `principalType`, `tenantId`, `rol`
- [ ] 2.4 Write test: `JwtAuthFilter` sets SecurityContext for valid token
- [ ] 2.5 Write test: `JwtAuthFilter` rejects invalid/expired tokens
- [ ] 2.6 Create `security/JwtAuthFilter.kt` — OncePerRequestFilter for JWT validation
- [ ] 2.7 Write test: Public endpoints (`/register`, `/verify`, `/login`, `/actuator/health`) accessible without auth
- [ ] 2.8 Write test: Protected endpoints require valid JWT
- [ ] 2.9 Create `security/SecurityConfig.kt` — SecurityFilterChain with permitAll paths and JWT filter

## Phase 3: User Repository Extensions

- [ ] 3.1 Modify `domain/port/UserRepositoryPort.kt` — add `findByCustomerId(customerId: Int): List<User>`
- [ ] 3.2 Modify `repository/UserRepository.kt` — add Spring Data method `findByCustomerId`
- [ ] 3.3 Modify `infrastructure/adapter/UserJpaAdapter.kt` — implement `findByCustomerId`
- [ ] 3.4 Write test: `ManageUserUseCase.findByCustomerId()` returns only tenant users
- [ ] 3.5 Modify `application/usecase/ManageUserUseCase.kt` — add `findByCustomerId` method

## Phase 4: Core Use Cases (TDD: RED→GREEN)

### RegisterCustomerUseCase
- [ ] 4.1 Write test: Valid registration creates pending Customer with hashed password + verification token
- [ ] 4.2 Write test: Duplicate email returns 409 Conflict
- [ ] 4.3 Write test: Missing fields returns 400 Bad Request
- [ ] 4.4 Create `application/usecase/RegisterCustomerUseCase.kt` — implement all scenarios

### VerifyEmailUseCase
- [ ] 4.5 Write test: Valid token activates Customer (`isActive=true`)
- [ ] 4.6 Write test: Expired token (>24h) returns 410 Gone
- [ ] 4.7 Write test: Invalid token returns 404 Not Found
- [ ] 4.8 Create `application/usecase/VerifyEmailUseCase.kt` — implement all scenarios

### LoginUseCase
- [ ] 4.9 Write test: Active Customer with valid credentials returns JWT with `principalType=customer`
- [ ] 4.10 Write test: User with valid credentials returns JWT with `principalType=user`
- [ ] 4.11 Write test: Inactive Customer returns 403 Forbidden
- [ ] 4.12 Write test: Wrong password returns 401 Unauthorized (no principal disclosure)
- [ ] 4.13 Write test: No matching principal returns 401 Unauthorized
- [ ] 4.14 Create `application/usecase/LoginUseCase.kt` — implement dual lookup (Customer→User)

## Phase 5: Adapters & Controllers

- [ ] 5.1 Create `infrastructure/adapter/GmailSmtpAdapter.kt` — implement `EmailSenderPort` using `JavaMailSender`
- [ ] 5.2 Write integration test: `POST /api/v1/auth/register` → `GET /api/v1/auth/verify` → `POST /api/v1/auth/login` full flow
- [ ] 5.3 Create `controller/AuthController.kt` — register, verify, login endpoints
- [ ] 5.4 Write test: `GET /api/v1/auth/users` returns only users with `customerId` from JWT
- [ ] 5.5 Write test: `POST /api/v1/auth/users` auto-sets `customerId` from JWT
- [ ] 5.6 Write test: Non-Customer principal (User) gets 403 for User CRUD
- [ ] 5.7 Modify `controller/UserController.kt` — add auth check, scope all CRUD by JWT `customerId`
