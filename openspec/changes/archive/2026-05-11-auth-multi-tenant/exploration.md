# Exploration: Auth Multi-Tenant

## Current State

The auth service at `services/auth/` has a **full Hexagonal Architecture** but is **incomplete** — it has the structural skeleton but critical business logic is missing.

### What exists

- **Customer** (business owner) domain model + JPA entity: `id` (Int/IDENTITY), `email`, `passwordHash`, `name`, `lastName`, `taxId`, `phone`, `companyName`, `isActive`, `isOnTrial`, `trialStartAt`, `trialEndAt`, `role`, `planId`
- **User** (employee) domain model + JPA entity: `id` (UUID), `email`, `passwordHash`, `firstName`, `lastName`, `role` (ADMINISTRATOR/OPERATOR/CASHIER), `commercialUserId`, `isActive`
- Full port/adapter/mapper layers for both (hexagonal)
- **JwtService**: generates HMAC256 tokens with `subject` (email), `rol`, `tenantId` (Int?), 24h expiry
- Flyway V1 migration creating all tables in `auth` schema
- CRUD controllers: `/api/v1/auth/users/*` and `/api/auth/customers/*`
- Tests: unit (Mockito) for use cases, integration (H2) for adapters, HTTP (MockMvc) for controllers, unit for JWTs

### Key gaps

1. **NO authentication endpoints** — login, register, validate-token all documented in README but don't exist
2. **NO SecurityConfig** — `spring-boot-starter-security` in deps but no filter chain, no PasswordEncoder, no auth filter
3. **NO password hashing** — `passwordHash` field exists but no BCrypt usage anywhere
4. **NO `customerId` on User** — `User` domain model has no `customerId` linking it to a Customer (the core tenant relationship is MISSING)
5. **NO email verification** — no verification token, no verified flag, no email sender
6. **NO registration flow** — no cohesive Register→Verify→Login flow exists
7. **Controller path inconsistency** — users at `/api/v1/auth/users`, customers at `/api/auth/customers` (no v1)

### What the README claims (but doesn't exist)

| Endpoint | Status |
|----------|--------|
| `POST /api/v1/auth/login` | ❌ Not implemented |
| `POST /api/v1/auth/register` | ❌ Not implemented |
| `GET /api/v1/auth/validate` | ❌ Not implemented |

### Database schema (Flyway V1)

The `auth` schema has 5 tables: `customers`, `users`, `permissions`, `role_permissions`, `user_permissions`, `user_stores`. No email verification columns exist. No `customer_id` on `users`.

## Affected Areas

| Path | Why |
|------|-----|
| `domain/model/User.kt` | Missing `customerId: Int` field |
| `domain/model/Customer.kt` | Missing email verification fields |
| `entity/User.kt` | Missing `customer_id` column |
| `entity/Customer.kt` | Missing verification columns |
| `infrastructure/mapper/UserMapper.kt` | Must map new `customerId` |
| `infrastructure/mapper/CustomerMapper.kt` | Must map new verification fields |
| `application/usecase/ManageUserUseCase.kt` | May need scoping by customerId |
| `controller/UserController.kt` | May need tenant scoping |
| `controller/CustomerController.kt` | Register endpoint replaces raw create |
| `security/JwtService.kt` | Must add `verify()` method for validation |
| **New**: `AuthController.kt` | Login, Register, VerifyEmail, ValidateToken |
| **New**: `RegisterCustomerUseCase.kt` | Registration orchestration |
| **New**: `LoginUseCase.kt` | Authentication logic |
| **New**: `SecurityConfig.kt` | Spring Security filter chain |
| **New**: `JwtAuthenticationFilter.kt` | Token validation filter |
| **New**: `domain/port/EmailSenderPort.kt` | Email abstraction port |
| **New**: `infrastructure/adapter/GmailSmtpAdapter.kt` | Gmail SMTP implementation |
| `build.gradle.kts` | Add `spring-boot-starter-mail` |
| `resources/db/migration/V2__auth_multi_tenant.sql` | New Flyway migration |
| `resources/application.yml` | Add mail configuration |
| All test files | New tests for all new components |

## Approaches

### 1. Incremental — domain-first, then infrastructure
Start with domain model changes, add ports and use cases, add infrastructure last.
- **Pros**: Clean, testable at each layer, follows existing hexagonal pattern
- **Cons**: Takes longer to have a working endpoint
- **Effort**: Medium

### 2. End-to-end per feature
Build register, login, verify email as complete vertical slices.
- **Pros**: Each slice independently deployable and testable
- **Cons**: More context switching between layers
- **Effort**: Medium

### 3. RECOMMENDED: Hybrid — domain models first, then E2E slices
1. Add `customerId` to User + migration
2. Add email verification fields to Customer + migration
3. Build register flow E2E
4. Build verify email flow E2E
5. Build login flow E2E
6. Build validate token
7. Add BCrypt password encoding
8. Tests at every layer

## Recommendation

**Approach 3**: Fix domain models first (they're the foundation), then build each auth flow as a complete vertical slice. This keeps hexagonal integrity, makes testing possible at each layer, and delivers working endpoints incrementally.

## Risks

1. **Existing data**: Adding `customer_id` to users table is breaking. Migration must make it nullable initially.
2. **Dual auth principals**: Both Customer and User have `passwordHash`. Both can login. Need clear auth strategy.
3. **Spring Security from scratch**: Adding SecurityConfig can break existing endpoints if not carefully configured.
4. **Gmail SMTP**: Requires "App Passwords" or OAuth 2.0. Must be configurable via env vars.
5. **Password handling**: Passwords currently flow as plain text. Must add BCrypt to both register and login flows.
6. **Controller path inconsistency**: `/api/auth/customers` vs `/api/v1/auth/users`. Recommend standardizing to `/api/v1/auth/*`.
7. **Test H2 schema**: H2 tests use `PUBLIC` schema, not `auth`. New columns must work in both.

## Ready for Proposal
Yes — the codebase is well-structured and the gaps are clear.
