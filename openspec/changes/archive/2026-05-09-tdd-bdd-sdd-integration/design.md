# Design: TDD + BDD + SDD Integration — Phase 1

## Technical Approach

Phase 1 integrates executable BDD into the SDD pipeline via Kotest BehaviorSpec — zero new dependencies. Two file modifications:

1. **sdd-spec/SKILL.md**: Insert **Step 4a** after GWT scenario writing. For each domain with GWT scenarios, generate a `{Domain}BehaviorSpec.kts` stub with `given`/`` When ``/`then` chains wrapped in `pending { }`.
2. **TEST_CONVENTIONS.md**: Add a **BehaviorSpec** subsection after "Testing en Kotlin" with a compilable template and coexistence guidance.

No orchestrator changes, no Gradle changes, no existing test refactoring.

## Architecture Decisions

### Decision: Step 4a placement

| Option | Tradeoff | Decision |
|--------|----------|----------|
| After Step 4, before Step 5 | Generation depends on GWT scenarios (Step 4 output); runs before persistence so artifacts bundle cleanly | **Chosen** |
| After Step 5 | Would bundle incomplete output | Rejected |
| Separate skill file | Over-engineering for a single generation step | Rejected |

### Decision: File extension `.kts`

| Option | Tradeoff | Decision |
|--------|----------|----------|
| `.kt` | Standard Kotlin compilation — but files live in `openspec/` outside source tree | No practical difference |
| `.kts` | Signals "generated stub", not hand-written test. Kotlin script extension, valid in Kotest. | **Chosen** — readability signal |

### Decision: DSL style `given`/`` When ``/`then`

| Option | Tradeoff | Decision |
|--------|----------|----------|
| `given`/`` When ``/`then` | Stable Kotest API, no extra imports (`` When `` backtick-quoted because `when` is a keyword) | **Chosen** |
| `Given`/`When`/`Then` | Capitalized variant, requires `import io.kotest.core.spec.style.behaviorSpec.given` | Avoid extra import |
| Gherkin-style strings | Would need Cucumber dependency (violates NFR-1) | Rejected |

### Decision: `pending { }` for stubs

| Option | Tradeoff | Decision |
|--------|----------|----------|
| `pending { }` | Compiles, shows as "ignored" (yellow) in reports — clearly a stub | **Chosen** |
| `.config(enabled = false)` | Hides test entirely from report — invisible | Rejected |
| Empty body | Would pass (false positive) | Rejected |

### Decision: No-overwrite guard

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Skip if exists + warn | Protects developer modifications to generated stub | **Chosen** |
| Always overwrite | Would lose manual edits | Rejected |
| Always overwrite + backup | Unnecessary complexity for generated stubs | Rejected |

## Data Flow

```
sdd-spec Step 4: Write GWT scenarios (markdown)
    │
    ▼
sdd-spec Step 4a (NEW): Generate BehaviorSpec stubs
    │
    ├─ For each domain with GWT scenarios:
    │    ├─ 1. Parse scenario → GIVEN / WHEN / THEN / AND blocks
    │    ├─ 2. Check if openspec/changes/{change}/specs/{domain}/{Domain}BehaviorSpec.kts exists
    │    │   ├─ YES → log "WARN: skipping existing {file}", continue
    │    │   └─ NO  → render stub template
    │    └─ 3. Write .kts file
    │
    ▼
sdd-spec Step 5: Persist artifact (unchanged)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `~/.config/opencode/skills/sdd-spec/SKILL.md` | Modify | Insert Step 4a between Step 4 and Step 5 |
| `openspec/testing/TEST_CONVENTIONS.md` | Modify | Add `### BehaviorSpec` subsection after `## Testing en Kotlin` |

## Interfaces / Contracts

### BehaviorSpec Stub Template

```kotlin
package com.siga.bdd.{domain}

import io.kotest.core.spec.style.BehaviorSpec

class {Domain}BehaviorSpec : BehaviorSpec({

    // ═══════════════════════════════════════════════
    // Generated from spec scenarios — STUB ONLY
    // Replace pending { } with real assertions
    // ═══════════════════════════════════════════════

    given("{GIVEN precondition}") {
        `When`("{WHEN action}") {
            then("{THEN expected outcome}") {
                pending { }
            }
        }
    }

    // When a scenario has AND outcomes:
    given("{GIVEN precondition with AND}") {
        `When`("{WHEN action}") {
            then("{THEN primary outcome}") {
                pending { }
            }
            then("{AND secondary outcome}") {
                pending { }
            }
        }
    }
})
```

**Mapping rules:**
- Each `- GIVEN` → `given("{value}")`
- Each `- WHEN` → `` `When`("{value}") ``
- Each `- THEN` → `then("{value}")`
- Each `- AND` after a THEN → additional `then()` in the same `` `When` `` scope
- First letter of the precondition/action/outcome is capitalized inside the string
- Package: `com.siga.bdd.{domain}` (lowercase domain)

### TEST_CONVENTIONS.md — BehaviorSpec Section

Placed as subsection `### BehaviorSpec` under `## Testing en Kotlin`, after line 577.

```markdown
### BehaviorSpec (Given/When/Then)

For BDD-style tests that mirror GWT scenarios from SDD specs. Coexists with DescribeSpec — use BehaviorSpec when scenarios map directly to GIVEN/WHEN/THEN from a spec document.

```kotlin
package com.siga.auth

import io.kotest.core.spec.style.BehaviorSpec

class LoginBehaviorSpec : BehaviorSpec({

    given("a registered user") {
        `When`("they log in with valid credentials") {
            then("the system should return a JWT token") {
                // given → Arrange
                val credentials = LoginRequest("user@siga.cl", "correct-password")
                // When → Act
                val result = authService.login(credentials)
                // Then → Assert
                result.token shouldNotBe null
                result.token shouldBe ofType<String>::class
            }
        }

        `When`("they log in with an invalid password") {
            then("the system should return 401 Unauthorized") {
                shouldThrow<AuthenticationException> {
                    authService.login(LoginRequest("user@siga.cl", "wrong-password"))
                }
            }
        }
    }
})
```

> **Coexists with DescribeSpec**: BehaviorSpec and DescribeSpec can live in the same project and same test suite. Use BehaviorSpec for scenarios that have explicit GIVEN/WHEN/THEN structure. Use DescribeSpec for unit/service tests with nested `describe`/`it` blocks. Both run under `kotest-runner-junit5` — no separate configuration needed.

**Naming**: `{Feature}BehaviorSpec.kts` (e.g., `LoginBehaviorSpec.kts`, `RegistrationBehaviorSpec.kts`).
```

## Implementation Order

1. **Modify TEST_CONVENTIONS.md** — add BehaviorSpec subsection (documentation only, no risk)
2. **Modify sdd-spec/SKILL.md** — insert Step 4a with:
   - The BehaviorSpec generation step
   - Template rendering instructions
   - No-overwrite guard
   - Package convention (`com.siga.bdd.{domain}`)
3. **Verify**: Run a dry SDD spec cycle with a test change to confirm stubs generate correctly

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Manual | Step 4a generates correct `.kts` from GWT | Create a dummy change with GWT scenarios, run sdd-spec, inspect output |
| Manual | No-overwrite guard | Re-run sdd-spec, verify warning log and no file change |
| Manual | Compilation | Copy generated stub to test directory, verify `gradlew test` compiles |
| Manual | Pending status | Verify test report shows stub tests as "ignored" (not passing, not failing) |

## Open Questions

- None. Phase 1 is fully specified.

## Migration / Rollout

No migration required. BehaviorSpec stubs are generated in `openspec/` — they don't affect existing DescribeSpec tests or CI pipeline. Developers adopt incrementally by copying stubs to `src/test/kotlin/`.
