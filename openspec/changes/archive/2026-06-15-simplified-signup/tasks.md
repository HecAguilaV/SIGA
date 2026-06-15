# Tasks: Simplified Signup

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~400-450 |
| 400-line budget risk | Medium |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | auto-forecast |
| Chain strategy | pending |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: pending
400-line budget risk: Medium

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Backend + seed data + frontend (all) | PR 1 | Tightly coupled — back + front must ship together |

## Phase 1: Backend — DTO & Use Case

- [x] 1.1 `AuthController.kt`: Change `RegisterRequest.name`/`companyName` to `String? = null`
- [x] 1.2 `RegisterCustomerUseCase.kt`: Remove `require()` for name/companyName; add `name = email.substringBefore("@")` fallback
- [x] 1.3 `RegisterCustomerUseCaseTest.kt`: Remove blank-name/company-name test; add email-as-name fallback test

## Phase 2: Seed Data

- [x] 2.1 `01_seed_auth.sql`: Insert SIGA admin user (`admin@siga.cl`, role OWNER, all permissions)
- [x] 2.2 `01_seed_auth.sql`: Insert test Customer (`test@siga.cl`, verified, password test1234, name "Test PYME")
- [x] 2.3 `01_seed_auth.sql`: Insert test User (`empleado@siga.cl`, role EMPLOYEE, assigned to test Customer store)

## Phase 3: Frontend — Shared Infrastructure

- [x] 3.1 `types/auth.ts`: Add `RegisterRequest` interface (email, password required; name/companyName optional)
- [x] 3.2 `auth.server.ts`: Add `register()` helper — POST /api/v1/auth/register
- [x] 3.3 `auth.server.ts`: Add `updateCustomer()` helper — PUT /api/v1/auth/customers/{id}

## Phase 4: Frontend — Register Route

- [x] 4.1 Create `(auth)/register/+page.server.ts` — validate, call register API, redirect to verify-pending
- [x] 4.2 Create `(auth)/register/+page.svelte` — form: email + password required, name + companyName optional

## Phase 5: Frontend — Onboarding Route

- [x] 5.1 Create `(auth)/onboarding/+page.server.ts` — call PUT /api/v1/auth/customers/{id}, redirect to dashboard
- [x] 5.2 Create `(auth)/onboarding/+page.svelte` — form: name + companyName, skip button, detects placeholder name from JWT

## Phase 6: Integration Tests

- [x] 6.1 `AuthRegistrationIntegrationTest.kt`: Add minimal registration test (only email+password, expect 201)
- [x] 6.2 `AuthRegistrationIntegrationTest.kt`: Add test verifying existing full-registration tests still pass

## Phase 7: Cleanup / Docs

- [x] 7.1 `EmailSenderService.kt`: Add javadoc noting email prefix fallback for name parameter
- [x] 7.2 Remove any temporary dev stubs or debug logs
