# Proposal: Auth Multi-Tenant

## Intent

Add register, email verification, login, and tenant-scoped user management to the existing auth skeleton — it has models and controllers but zero auth flows.

## Scope

- `customerId: Int?` on User (domain+entity+mapper)
- Email verification on Customer
- `POST /api/v1/auth/register` (Customer + company, sends email)
- `GET /api/v1/auth/verify?token=`
- `POST /api/v1/auth/login` (Customer or User, returns JWT)
- User CRUD scoped to customerId
- SecurityFilterChain + JwtAuthFilter + BCrypt + V2 + mail dep

**Out**: OAuth, password reset, MFA, Customer→UUID, rate limiting.

## Capabilities

- **New**: `customer-auth` (register, verify, login)
- **New**: `user-tenant-scope` (user CRUD per tenant)
- **Modified**: None

## Approach

1. Domain: `customerId: Int?` on User, verification on Customer + V2 migration
2. Register: `RegisterCustomerUseCase` → `EmailSenderPort` → `AuthController`
3. Verify: `VerifyEmailUseCase` → `AuthController`
4. Login: `LoginUseCase` → `SecurityConfig` (BCrypt, filter) → `JwtAuthFilter`
5. Validate: `JwtService.verify()` → `AuthController`
6. Scope: filter user queries by JWT's customerId
7. Tests: Kotest BehaviorSpec per use case, MockMvc, H2

## Key Decisions

| Decision | Choice | Why |
|----------|--------|-----|
| Customer ID | Keep Int | Avoids breaking migration. UUID deferred. |
| Dual principals | Single `/login`, try Cx then User | JWT carries `principalType`. |
| User.customerId | `Int?` nullable | Matches Customer PK. Null for existing rows. |
| Test DB | H2-compatible V2 DDL | No schema-qualified names. |

## Key Files

- **Domain**: `User.kt` (+customerId), `Customer.kt` (+verification)
- **Entity**: `User.kt`, `Customer.kt` (new columns)
- **Mapper**: `UserMapper.kt`, `CustomerMapper.kt` (new mappings)
- **Security**: `JwtService.kt` (+verify), **new** `SecurityConfig.kt`, `JwtAuthFilter.kt`
- **Controllers**: `CustomerController.kt` (path→`/api/v1/auth/`), **new** `AuthController.kt`
- **Use Cases (new)**: `RegisterCustomer`, `VerifyEmail`, `Login`
- **Email (new)**: `EmailSenderPort`, `GmailSmtpAdapter`
- **Config**: `build.gradle.kts` (+mail), `V2__auth_multi_tenant.sql`

## OWASP

A01: User CRUD filtered by customerId | A02: BCrypt+HMAC256 | A05: Explicit filter chain | A07: Generic errors

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| H2 V2 fails on `auth` schema | Med | H2-compatible DDL |
| Raw Customer POST bypasses register | Low | Deprecate old endpoint |
| Gmail SMTP blocks email | Low | Fallback, env vars |
| Null customerId in existing users | Med | Null = admin scope |

## Rollback

1. `DELETE FROM flyway_schema_history WHERE version = '2'`, drop columns
2. Revert changed files, remove new files
3. Restore old CustomerController path, revert deps

## Success Criteria

- [ ] All tests pass (BehaviorSpec + MockMvc + H2)
- [ ] Register creates Customer, sends email (test double)
- [ ] Verify activates account
- [ ] Login returns JWT with `tenantId`, `principalType`, `rol`
- [ ] JwtAuthFilter rejects expired/malformed tokens (401)
- [ ] User CRUD scoped to caller's customerId
- [ ] No plain-text passwords stored/returned
