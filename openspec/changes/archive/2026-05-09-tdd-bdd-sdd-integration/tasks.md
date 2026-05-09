# Tasks: TDD + BDD + SDD Integration — Phase 1

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 100–150 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | N/A — direct commit |
| Delivery strategy | exception-ok (direct commit, no PR) |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Insert BehaviorSpec subsection in TEST_CONVENTIONS.md | N/A (direct commit) | Independent — can be done in parallel |
| 2 | Insert Step 4a (stub generation) in sdd-spec/SKILL.md | N/A (direct commit) | Independent — can be done in parallel |
| 3 | Verify stubs compile and show as pending | N/A (direct commit) | Depends on 1 & 2 |

## Phase 1: BDD Integration (Executable GWT)

- [x] 1.1 Insert `### BehaviorSpec` subsection in `openspec/testing/TEST_CONVENTIONS.md` after line 577 (end of `## Testing en Kotlin` section)
  - Include `import io.kotest.core.spec.style.BehaviorSpec`
  - Include compilable template with `given` / `` `When` `` / `then` DSL
  - Include coexistence guidance: BehaviorSpec + DescribeSpec in same suite
  - Include file naming convention: `{Feature}BehaviorSpec.kts`
- [x] 2.1 Insert `Step 4a: Generate BehaviorSpec stubs` in `~/.config/opencode/skills/sdd-spec/SKILL.md` between Step 4 (Write Delta Specs) and Step 5 (Persist Artifact)
  - Guard: only for `openspec` / `hybrid` modes
  - For each domain with GWT scenarios, render a `.kts` file to `openspec/changes/{change-name}/specs/{domain}/{Domain}BehaviorSpec.kts`
  - Map GIVEN → `given`, WHEN → `` `When` ``, THEN/AND → `then`
  - Wrap each `then` body in `pending { }`
  - No-overwrite guard: skip with warning if file exists
  - Package convention: `com.siga.bdd.{domain}`
- [ ] 3.1 Verify: dry-run sdd-spec on a dummy change with GWT scenarios, confirm stub is generated, compiles, and shows as "ignored" (pending) in test report

## Dependencies

| Task | Depends On |
|------|-----------|
| 1.1 | None |
| 2.1 | None |
| 3.1 | 1.1, 2.1 |

Tasks 1.1 and 2.1 are **independent** and can be executed in any order or in parallel. Task 3.1 requires both modifications to be in place.

## Risk Assessment

| Task | Risk | Rationale |
|------|------|-----------|
| 1.1 | Low | Pure documentation — adds new subsection, no existing content changed |
| 2.1 | Medium | Modifies a skill file that orchestrator invokes. Wrong placement breaks pipeline. Step numbers must be re-verified. |
| 3.1 | Low | Manual verification — no automation risk |
