## Exploration: TDD + BDD + SDD Integration in SIGA

### Current State

SIGA has a **complete SDD pipeline** with all 8 phases as executable skills (sdd-init through sdd-archive). The project uses Kotlin/Spring Boot with microservices (auth, billing, inventory, sales, gateway, registry), fully migrated to Hexagonal Architecture.

**What already works well:**

- **SDD**: Full pipeline with 8 skills, openspec directory, config.yaml, persistent artifacts
- **TDD**: `strict-tdd.md` module exists with RED→GREEN→TRIANGULATE→REFACTOR cycle, assertion quality rules, and `strict-tdd-verify.md` for verification. Config says `strict_tdd: true`
- **Testing**: 37+ Kotest `DescribeSpec` tests across 4 microservices, JaCoCo coverage, MockK for mocking, Testcontainers for integration
- **Spec format**: GIVEN/WHEN/THEN used in openspec specs (database, saga-sales-inventory, billing-uuid-hexagonal)
- **Guide**: `ACADEMIC/TDD-BDD-SDD_INTEGRATION/README.md` documents the full pipeline vision

**What's missing or weak:**

- **BDD**: No executable BDD framework. Specs use GWT markdown but they're not automated. Kotest `BehaviorSpec` (which has native `Given`, `When`, `Then` words) is unused despite Kotest being a dependency
- **TDD enforcement**: `strict_tdd: true` in config, but sdd-apply checks conditionally. If orchestrator doesn't pass it, TDD mode silently falls back to standard mode
- **Feedback loop**: sdd-verify has no protocol to feed results back to sdd-spec or sdd-apply. Issues are reported but not actionable
- **Skill registry**: No `.atl/skill-registry.md`. The `.atl/` directory doesn't exist
- **BDD in spec phase**: sdd-spec generates GWT scenarios but doesn't produce executable test stubs
- **Archive learning**: sdd-archive doesn't feed lessons learned back into future explorations

### BCG Matrix Analysis

```
Market Growth (Value/Impact)
        ^
        |   Question Marks          Stars
        |   BDD                     TDD
        |   (High potential,        (High value, well implemented,
        |    needs investment)       needs enforcement)
        |
        |   Dogs                    Cash Cows
        |   (N/A)                   SDD
        |                           (Well established, maintain)
        +-------------------------------->
                                     Relative Market Share (How covered)
```

| Methodology | Quadrant | Rationale |
|-------------|----------|-----------|
| **SDD** | Cash Cow | Complete pipeline, well-documented, openspec established. Maintain, optimize for size budgets. |
| **TDD** | Star | Excellent implementation (strict-tdd.md), but not enforced at orchestrator level. Move to Cash Cow by hardening enforcement. |
| **BDD** | Question Mark | Huge opportunity: Kotest supports `BehaviorSpec` natively (zero new dependencies). Currently using GWT in markdown only — not executable. Invest now. |

**Strategic order**: BDD first (easiest win, zero new deps) → TDD enforcement (config change, orchestration hardening) → Feedback loop (cross-phase protocol) → Skill registry (consolidation).

### Affected Areas

| File | Impact |
|------|--------|
| `services/*/build.gradle.kts` | Already has Kotest — no changes needed for BDD |
| `services/*/src/test/**/*.kt` | Add `BehaviorSpec` tests alongside existing `DescribeSpec` tests |
| `.config/opencode/skills/sdd-spec/SKILL.md` | Add instruction to generate Kotest BehaviorSpec stubs from GWT scenarios |
| `.config/opencode/skills/sdd-apply/SKILL.md` | Step 3: make strict_tdd discovery aggressive, not conditional. Add BDD test execution |
| `.config/opencode/skills/sdd-verify/SKILL.md` | Add BDD scenario validation step. Add feedback loop protocol |
| `.config/opencode/skills/sdd-tasks/SKILL.md` | Already mentions RED/GREEN (line 232). Make explicit when strict_tdd is active |
| `.config/opencode/skills/sdd-init/SKILL.md` | Add skill registry creation. Better Kotest `BehaviorSpec` detection |
| `.config/opencode/skills/sdd-archive/SKILL.md` | Add "lessons learned" feedback to future explorations |
| `.atl/skill-registry.md` | **Create** — compact rules for all three methodologies |
| `openspec/config.yaml` | Already has `strict_tdd: true`. Add `bdd_framework: kotest-behaviorspec` |
| `openspec/testing/TEST_CONVENTIONS.md` | Add BehaviorSpec section with GWT templates |

### Approaches

1. **Kotest BehaviorSpec (Recommended)** — Use Kotest's native `BehaviorSpec` for executable BDD
   - Pros: Zero new dependencies, native Kotlin, integrates with existing test runner, `Given`/`When`/`Then` words are first-class
   - Cons: No Gherkin parser, no `.feature` file portability, less tooling (no reporting dashboards)
   - Effort: Low

2. **Cucumber Kotlin** — Add Cucumber with `.feature` files and Kotlin step definitions
   - Pros: True Gherkin standard, portable `.feature` files, rich reporting, language-agnostic specs
   - Cons: New dependency, Gherkin/maintenance overhead, step definition boilerplate
   - Effort: Medium

3. **Plain GWT in markdown + manual verify (current)** — Keep using GWT in spec markdown
   - Pros: No overhead, flexible
   - Cons: Not executable, no automation, no enforcement, manual verification only
   - Effort: None (already like this)

4. **Hybrid: BehaviorSpec for executable + GWT markdown for documentation**
   - Pros: Best of both — specs remain human-readable, BehaviorSpec tests validate behavior
   - Cons: Two representations to keep in sync
   - Effort: Low-Medium

### Recommendation

**Adopt Approach 1 (Kotest BehaviorSpec) as the primary BDD layer**, combined with maintaining GWT markdown in spec docs. This is the path of least resistance:

1. **Phase 1 (BDD)**: Modify `sdd-spec` to generate Kotest `BehaviorSpec` test stubs alongside spec markdown. Add a BehaviorSpec reference/template section to `TEST_CONVENTIONS.md`. No code changes to gradle files.
2. **Phase 2 (TDD Enforcement)**: Modify `sdd-apply` Step 3 to default to strict_tdd mode when Kotest is detected (instead of checking conditionally). The gate in strict-tdd.md already works — just activate it by default.
3. **Phase 3 (Feedback Loop)**: Add a `Feedback` section to `sdd-verify` report format that maps failing spec scenarios back to `sdd-spec` (for spec gaps) or `sdd-apply` (for implementation gaps).
4. **Phase 4 (Skill Registry)**: Create `.atl/skill-registry.md` via `sdd-init` with compact rules for TDD/BDD/SDD. Update `sdd-init` to detect Kotest `BehaviorSpec` capability.

### Risks

1. **Overhead of dual representation**: BehaviorSpec tests + GWT markdown specs could drift apart. Mitigation: sdd-verify validates both are in sync during verification.
2. **TDD enforcement backlash**: Forcing strict TDD on all tasks may slow down simple changes. Mitigation: exceptions for config-only, rename, or migration tasks (already handled in strict-tdd.md triangulation rules).
3. **Feedback loop complexity**: If verify tries to modify spec or apply artifacts, we risk circular dependencies. Mitigation: verify only REPORTS — it never modifies. Orchestrator decides which phase to re-execute.
4. **Skill registry ownership**: Who updates `.atl/skill-registry.md`? Mitigation: sdd-init creates it, sdd-archive updates the "lessons learned" section.
5. **Existing tests not in BehaviorSpec**: Refactoring 37+ existing `DescribeSpec` tests to `BehaviorSpec` is not needed. Only NEW scenarios use BehaviorSpec. Coexistence is valid in Kotest.

### Ready for Proposal

Yes. The exploration found concrete, actionable next steps. The highest-value, lowest-effort change is adopting Kotest BehaviorSpec for executable BDD — zero new dependencies, matches existing tooling, immediate value.
