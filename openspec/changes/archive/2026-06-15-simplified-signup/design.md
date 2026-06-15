# Design: Simplified Signup

## Technical Approach

Modify `RegisterRequest` DTO and `RegisterCustomerUseCase` to make `name`/`companyName` optional with email-as-name fallback. Add frontend onboarding route for post-verification profile completion. No DB migration — `name VARCHAR(100) NOT NULL` satisfied by email prefix placeholder. Seed SQL adds SIGA admin + test customer for dev/demo.

## Architecture Decisions

### Decision: Nullable fields in RegisterRequest with null defaults

| Option | Tradeoff | Decision |
|--------|----------|----------|
| `String? = null` for name/companyName | Jackson deserializes missing JSON fields as null; existing clients sending values still work | ✅ Chosen — minimal change, backward compatible |
| Keep non-nullable, change frontend to always send | Would break API contract for any external consumer | ❌ Rejected — breaks backward compat |
| Overloaded endpoint (e.g., `/register-minimal`) | Duplicate controller logic, two endpoints to maintain | ❌ Rejected — unnecessary complexity |

### Decision: Name validation relaxed to email prefix fallback

Only `email` and `password` remain mandatory via `require()`. If `name` is null or blank, use `email.substringBefore("@")` as placeholder. `companyName` stays nullable in domain model — no validation required.

### Decision: Placeholder name detection

Any `Customer.name` equal to `email.substringBefore("@")` is a placeholder. Backend does NOT expose a special flag — the frontend derives it client-side after login by comparing the JWT `name` claim against `email.substringBefore("@" )`. This avoids adding a new field to the API contract.

### Decision: Verification endpoint unchanged

`GET /api/v1/auth/verify` still returns `{"status": "verified"}` JSON. The frontend verification page (when added) will call this endpoint then redirect — either to `/onboarding` if the user has a placeholder name, or to `/dashboard` otherwise. No backend change needed for the redirect decision.

### Decision: Seed data — SIGA admin + test Customer + test User

- **SIGA admin**: `admin@siga.cl`, role `ADMINISTRATOR`, no `customer_id` (platform-level user)
- **Test Customer**: `test@siga.cl`, pre-verified (`is_active=true`, `email_verified=true`), name `Test User`
- **Test User**: Attached to test Customer (customer_id=2), role `EMPLOYEE`

## Data Flow

```
Registration (minimal):
  Register Form → POST /api/v1/auth/register { email, password }
    → AuthController → RegisterCustomerUseCase
      → name = email.substringBefore("@")  // fallback
      → CustomerRepository.save()
      → EmailEventProducer.publish()       // async mode
    → 201 { status: "pending" }
    → EmailSenderService logs token to console (dev) or sends SMTP (prod)

Post-verification onboarding:
  User clicks verify link → GET /api/v1/auth/verify?token=...
    → 200 { status: "verified" }
  User logs in → POST /api/v1/auth/login
    → JWT with name = email prefix (placeholder)
  Frontend detects: name == email.split("@")[0]
    → true → redirect /onboarding
  Onboarding form → PUT /api/v1/auth/customers/{id} { name, companyName }
    → CustomerController → ManageCustomerUseCase
    → 200 → redirect /dashboard
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `services/auth/.../controller/AuthController.kt` | Modify | `RegisterRequest`: name/companyName → `String? = null` |
| `services/auth/.../usecase/RegisterCustomerUseCase.kt` | Modify | Relax `require()` for name/companyName; add email-as-name fallback |
| `services/auth/.../infrastructure/adapter/EmailSenderService.kt` | Modify | Update `buildEmailBody()` call — no code changes needed (name param already flexible), but add javadoc noting fallback |
| `services/auth/.../event/EmailEvent.kt` | No change | `name` field is String — placeholder works as-is |
| `services/notification/.../templates/welcome.html` | No change | `{{name}}` renders any string — placeholder fine |
| `scripts/seed/01_seed_auth.sql` | Modify | Add SIGA admin user, test Customer (verified), test User employee |
| `apps/dashboard/src/routes/(auth)/register/+page.svelte` | Create | Registration form: email + password (required), name + companyName (optional) |
| `apps/dashboard/src/routes/(auth)/register/+page.server.ts` | Create | Server action: POST /api/v1/auth/register, redirect to verify-pending |
| `apps/dashboard/src/routes/(auth)/onboarding/+page.svelte` | Create | Onboarding form: name + companyName, skip option |
| `apps/dashboard/src/routes/(auth)/onboarding/+page.server.ts` | Create | Server action: PUT /api/v1/auth/customers/{id} |
| `apps/dashboard/src/lib/server/auth.server.ts` | Modify | Add `register()` and `updateCustomer()` helpers |
| `apps/dashboard/src/lib/types/auth.ts` | Modify | Add `RegisterRequest` type (email+password, optional name/companyName) |
| `services/auth/src/test/.../RegisterCustomerUseCaseTest.kt` | Modify | Update tests: remove blank-name/company-name failure tests; add email-as-name fallback test |
| `services/auth/src/test/.../AuthRegistrationIntegrationTest.kt` | Modify | Update request bodies in existing tests; add minimal registration test |

## Interfaces / Contracts

### Modified RegisterRequest (Kotlin)

```kotlin
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null,       // ← nullable
    val companyName: String? = null  // ← nullable
)
```

### RegisterCustomerUseCase.register signature (unchanged)

```kotlin
fun register(email: String, rawPassword: String, name: String, companyName: String): Customer
```

No API contract change — name/companyName are still `String` in the use case; the controller extracts them from the DTO with fallback applied.

### New Frontend types

```typescript
export interface RegisterRequest {
    email: string;
    password: string;
    name?: string;
    companyName?: string;
}
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | RegisterCustomerUseCase with null name | Mock repo, verify `name = email prefix` |
| Unit | RegisterCustomerUseCase with provided name | Verify name passed through unchanged |
| Unit | Backward compat — all 4 fields | Existing tests pass unchanged |
| Integration | POST /api/v1/auth/register minimal | MockMvc test with only email+password |
| Integration | POST /api/v1/auth/register full | Existing 201 test still passes |
| Integration | Seed data present | Verify admin@siga.cl and test@siga.cl exist |
| E2E | Registration → verification → onboarding redirect | SvelteKit + Auth service |
| E2E | Onboarding form → PUT customer → redirect dashboard | Full frontend flow |

## Migration / Rollout

No migration required. The `name VARCHAR(100) NOT NULL` constraint is satisfied by the email prefix fallback at the domain level before persistence. Feature flag: `app.email.mode=async` (already deployed) controls email delivery path.

**Rollback**:
1. Revert `RegisterRequest` to non-nullable name/companyName
2. Revert `RegisterCustomerUseCase` validation
3. Remove onboarding route
4. Revert seed SQL

## Open Questions

- [ ] Register page: should name/companyName be hidden or visible-but-optional? Spec says visible-but-optional — confirm with product.
- [ ] Should the onboarding page appear after every login with a placeholder name, or only once? Spec implies once — but needs verification that the flow prevents re-triggering after update.
- [ ] Test customer password? All existing seed users use `demo123` — propose same for test@siga.cl.
