# Archive Report: notification-service-async-email

**Archived**: 2026-06-02
**Verification**: PASS WITH WARNINGS
**Archive path**: `openspec/changes/archive/2026-06-02-notification-service-async-email/`

## Engram Artifact Lineage

| Artifact | Observation ID | Created |
|----------|---------------|---------|
| `sdd/notification-service-async-email/proposal` | #779 | 2026-05-30 |
| `sdd/notification-service-async-email/spec` | #780 | 2026-05-30 |
| `sdd/notification-service-async-email/design` | #781 | 2026-05-30 |
| `sdd/notification-service-async-email/tasks` | #782 | 2026-05-30 |
| `sdd/notification-service-async-email/apply-progress` | #783 | 2026-05-30 |
| `sdd/notification-service-async-email/verify-report` | #852 | 2026-06-02 |
| `sdd/notification-service-async-email/archive-report` | (current) | 2026-06-02 |

## Specs Synced

| Domain | Action | Details |
|--------|--------|---------|
| async-email | Created (new domain) | `openspec/specs/async-email/spec.md` — REQ-1 through REQ-5 |
| customer-auth | Updated (R1 modified, R11+R12 added) | R1: async email instead of sync SMTP. R11: password reset request. R12: password reset confirm |

### customer-auth Merge Details

| Requirement | Action | Description |
|-------------|--------|-------------|
| R1 | Modified | Registration now publishes WELCOME EmailEvent instead of calling `EmailSenderPort.sendVerificationEmail`. Added "Event published instead of SMTP" scenario. Removed "AND verification email sent" from success scenario. |
| R11 | Added | Password Reset Request endpoint — generates token (15min expiry), publishes PASSWORD_RESET event, returns 200 always. 3 scenarios. |
| R12 | Added | Password Reset Confirm — validates token, checks expiry, hashes new password, invalidates token. 3 scenarios. |

## Archive Contents

- `proposal.md` ✅ — 69 lines, intent, scope, approach, risks, rollback
- `specs/customer-auth/spec.md` ✅ — Delta spec (R1 mod, R11, R12)
- `design.md` ✅ — 155 lines, architecture decisions, data flow, file changes
- `tasks.md` ✅ — 36/36 tasks complete across 6 phases
- `verify-report.md` ✅ — PASS WITH WARNINGS (202 tests pass, 6/8 core spec scenarios compliant)

## Source of Truth Updated

- `openspec/specs/customer-auth/spec.md` — R1 modified, R11 and R12 appended (3 new scenarios each)
- `openspec/specs/async-email/spec.md` — Already in place as new domain (no merge needed)

## SDD Cycle Complete

The change has been fully planned (proposal), specified (delta specs), designed, implemented (36 tasks), verified (202 tests passing), and archived.

### Key Metrics

| Metric | Value |
|--------|-------|
| Total tasks | 36 |
| Tasks completed | 36 |
| Tests (notification) | 13 passed |
| Tests (auth) | 189 passed |
| Coverage (notification) | 77.3% |
| Verification verdict | PASS WITH WARNINGS |
