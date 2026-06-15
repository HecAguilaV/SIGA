## Exploration: simplified-signup

### Current State

The current registration system requires **all four fields** (`email`, `password`, `name`, `companyName`) upfront in a single step:

- **Controller**: `AuthController.register()` → `POST /api/v1/auth/register`
- **DTO**: `RegisterRequest(email, password, name, companyName)` — all `val` (non-nullable, mandatory)
- **Use case**: `RegisterCustomerUseCase.register()` validates each field with `require(...isNotBlank())` — throws `IllegalArgumentException` if blank
- **Model**: `Customer.name` is `String` (non-nullable), `Customer.companyName` is `String?` (nullable)
- **DB schema**: `name VARCHAR(100) NOT NULL`, `company_name VARCHAR(255)` (nullable)
- **Email mode**: defaults to `async` (Kafka Event → Notification service). Template `welcome.html` uses `{{name}}` for personalization
- **Verification**: `GET /api/v1/auth/verify?token=<token>` — 24h token expiry, sets `isActive=true` + `emailVerified=true`
- **Frontend**: No register page exists in the SvelteKit dashboard. Only a login page at `(auth)/login/`
- **Seed data**: `scripts/seed/01_seed_auth.sql` creates 1 Customer (Elizabeth) + 5 Users (1 OWNER, 4 EMPLOYEE) with complete profiles
- **Login**: `LoginUseCase.login()` tries Customer first, then User. Requires `isActive=true` to proceed

### Affected Areas

- `services/auth/src/main/kotlin/com/siga/auth/controller/AuthController.kt` — `RegisterRequest` DTO (name/companyName mandatory), `register()` endpoint
- `services/auth/src/main/kotlin/com/siga/auth/application/usecase/RegisterCustomerUseCase.kt` — validates name/companyName with `require()`, passes them to Customer constructor
- `services/auth/src/main/kotlin/com/siga/auth/domain/model/Customer.kt` — `name` is non-nullable `String`, no "onboarding completed" flag
- `services/auth/src/main/kotlin/com/siga/auth/infrastructure/mapper/CustomerMapper.kt` — maps name between domain and entity
- `services/auth/src/main/kotlin/com/siga/auth/controller/CustomerController.kt` — existing PUT/POST endpoints could be used for post-verification onboarding
- `services/auth/src/main/kotlin/com/siga/auth/application/usecase/LoginUseCase.kt` — no check for "profile complete" (only checks `isActive`)
- `services/auth/src/main/resources/db/migration/V1__auth_init.sql` — `name VARCHAR(100) NOT NULL` constraint
- `services/notification/src/main/resources/templates/welcome.html` — uses `{{name}}` placeholder, needs fallback for when name is unavailable
- `services/notification/src/main/kotlin/com/siga/notification/infrastructure/service/TemplateRenderer.kt` — simple string substitution
- `services/auth/src/test/kotlin/com/siga/auth/application/usecase/RegisterCustomerUseCaseTest.kt` — all tests pass name+companyName
- `services/auth/src/test/kotlin/com/siga/auth/AuthRegistrationIntegrationTest.kt` — all integration tests pass name+companyName
- `scripts/seed/01_seed_auth.sql` — seed data (may need SIGA admin + test Customer + test User)
- `services/auth/src/main/resources/application.yml` — `app.email.mode` (async/sync), no impact but relevant context
- `apps/dashboard/src/routes/(auth)/` — no register page exists, will need new routes

### Approaches

1. **Simplest — Make name/companyName optional, skip onboarding**
   - Change `RegisterRequest` to make `name` and `companyName` nullable with `null` defaults
   - Relax `require()` checks in `RegisterCustomerUseCase` — only require email+password
   - Set default values (`name = "User"`, `companyName = null`) when not provided
   - Welcome email uses email address as fallback when name is null
   - Pros: Minimal changes, fast to implement, no new endpoints
   - Cons: Users have placeholder names until they manually update (via CustomerController), no guided onboarding flow
   - Effort: Low

2. **Two-phase registration with post-verification onboarding**
   - Phase 1: Register with email+password only → Customer created with null name/companyName
   - Make `name` nullable in DB (`ALTER COLUMN name DROP NOT NULL`)
   - Verification email uses email address or "there" as fallback name
   - Phase 2: After verification redirect to onboarding form (`GET /onboarding`) → `PUT /api/v1/auth/customers/{id}` 
   - Add frontend onboarding page at `(auth)/onboarding/`
   - Mark profile as complete (could add `profileCompleted` flag or just check if name is set)
   - Allow login even without completed profile (show onboarding reminder)
   - Pros: Clean UX, guided flow, proper data collection
   - Cons: More changes (DB migration, new endpoint, frontend work, email template update)
   - Effort: High

3. **Hybrid — Register with email+password, optional name on register, onboarding in frontend only**
   - Change `RegisterRequest` to accept optional name/companyName (default: null)
   - Accept them during registration if provided (backward compat)
   - If not provided, create Customer with `name = email` (temporary placeholder)
   - Welcome email uses email as greeting fallback
   - After verification, frontend checks if user has a real name; if not, redirects to onboarding
   - Onboarding: frontend calls `PUT /api/v1/auth/customers/{id}` with real name + companyName
   - Pros: Backward compatible, no DB migration needed if name stays NOT NULL, frontend-only flow for onboarding
   - Cons: Uses email as name placeholder (ugly), `name` constraint stays NOT NULL
   - Effort: Medium

### Recommendation

**Approach 2 (Two-phase registration)** is the cleanest long-term solution, but **Approach 3 (Hybrid)** is the pragmatic choice for this iteration:

1. It keeps backward compatibility — existing API clients still work
2. No DB migration needed if we use `email` as placeholder for name
3. The verification email works with either a real name or email fallback
4. The frontend handles the onboarding UX without new backend endpoints
5. The seed data already has full profiles — no impact there

However, Approach 2 is recommended if we want to do this RIGHT. The DB schema improvement (making name nullable) aligns with the domain concept that a "pending" customer doesn't have a real name yet.

**Recommended next step**: Proposal phase evaluating both approaches with concrete scope.

### Risks

- **DB constraint**: `name VARCHAR(100) NOT NULL` — currently blocks null names. Option 3 avoids this by using email placeholder; Option 2 needs a migration
- **Email personalization**: Welcome email uses `{{name}}` — without a real name, "Hello there" or "Hello user@email.com" is less personal but acceptable
- **Existing tests**: All 6+ tests pass name+companyName and assert they're non-blank — need updates regardless of approach
- **Login flow**: No current check for "profile completeness" — a user who registers but hasn't completed onboarding can still log in (OK behavior, just redirect to onboarding)
- **Seed data overlap**: Permissions are seeded via both Flyway V4 and `scripts/seed/01_seed_auth.sql` — the script approach should be the single source of truth for demo data
- **Seed test users**: Current seed has Elizabeth + 4 employees. A "pure" test Customer (not tied to the Elizabeth scenario) and a SIGA admin (for internal use) should be considered
- **Email port**: Auth service has SMTP config in `application-prod.yml` but the `async` mode sends via Notification service. Both need SMTP configured if `sync` mode is used, but only Notification needs it in `async` mode

### Ready for Proposal

Yes. The next step is `sdd-propose` to define scope, approach, and rollback plan.
