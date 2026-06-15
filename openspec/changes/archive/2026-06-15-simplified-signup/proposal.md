# Proposal: simplified-signup

## Intent

Remove friction from signup by requiring only email+password upfront. Defer name/companyName collection to a post-verification onboarding flow, reducing abandonment at the first step while keeping backward compatibility.

## Scope

### In Scope
- Backend: `RegisterRequest` makes `name`/`companyName` optional (nullable defaults); `RegisterCustomerUseCase` uses email as `name` placeholder when omitted
- Email: Welcome template falls back to email prefix when `name` is placeholder
- Frontend: New onboarding page after verification (`GET /onboarding` → `PUT /api/v1/auth/customers/{id}` with name + companyName)
- Seed: Add SIGA admin user + standalone test Customer (verified email) + test User employee
- SMTP: Document prod config; dev fallback logs token to console
- Tests: Update all existing registration tests for optional name/companyName

### Out of Scope
- DB migration (`name` stays `VARCHAR(100) NOT NULL` — email placeholder avoids this)
- Profile-complete gate on login (redirect to onboarding from frontend only)
- CompanyName onboarding (accepted if provided on register, deferred on onboarding page)
- Password reset / user management changes

## Capabilities

### New Capabilities
- `simplified-registration`: Email+password-only registration, optional name/companyName accepted for backward compat, email as placeholder
- `post-verification-onboarding`: Frontend page collecting name + companyName, PUT to customer profile

### Modified Capabilities
- `customer-auth`: R1 (Customer Registration) — name/companyName become optional; "Missing required fields" scenario updated; new "Minimal registration" scenario added

## Approach

Hybrid (Approach 3 from exploration): No DB migration. Make `name`/`companyName` nullable in `RegisterRequest` with `null` defaults. Relax `require()` checks in `RegisterCustomerUseCase` — only `email`/`password` are mandatory. If `name` is null, use `email.substringBefore("@")` as placeholder. Welcome email uses the same fallback. Frontend: after verification redirect → onboarding page → `PUT /api/v1/auth/customers/{id}`.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `AuthController.kt` | Modified | RegisterRequest DTO — name/companyName nullable |
| `RegisterCustomerUseCase.kt` | Modified | Relax validation, email-as-name fallback |
| `Customer.kt` | Unchanged | name stays non-nullable (email placeholder) |
| `Welcome email template` | Modified | Fallback when name is email prefix |
| `CustomerController.kt` | Unchanged | Reused by onboarding page as-is |
| `Seed SQL` | Modified | Add SIGA admin + test Customer + test User |
| `Login page (SvelteKit)` | Unchanged | No changes needed |
| `New onboarding page` | New | Frontend SvelteKit route |
| `Registration tests` | Modified | Adapt to optional name/companyName |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Email as name leaks email prefix in UI | Medium | Acceptable — placeholder replaced on first onboarding |
| Existing clients sending name break | Low | Backward compat — accepted if provided |
| Seed data overlap (Flyway vs script) | Medium | Script is single source of truth for demo data |

## Rollback Plan

1. Revert `RegisterRequest` to mandatory `name`/`companyName`
2. Revert `RegisterCustomerUseCase` validation
3. Revert welcome email template
4. Remove onboarding page
5. Revert seed SQL

## Dependencies

- SMTP config documented for prod; dev logs token to console (no SMTP needed)

## Success Criteria

- [ ] Registration accepts email+password only, returns 201
- [ ] Registration with name/companyName still works (backward compat)
- [ ] Welcome email sent with email-as-name fallback
- [ ] Post-verification onboarding page collects name + companyName
- [ ] Seed data: SIGA admin + test Customer (verified) + test User present
- [ ] All existing registration tests pass with optional name
