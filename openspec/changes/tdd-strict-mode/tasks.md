# Tasks: TDD Strict Mode — Kotest Auto-Detection

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~30 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single commit |
| Delivery strategy | exception-ok |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

## Phase 1: Kotest Detection in sdd-apply (SKILL.md Step 3)

- [ ] 1.1 Add 4-tier resolution chain to `~/.config/opencode/skills/sdd-apply/SKILL.md` Step 3: explicit flag → config.yaml → Kotest detection → Standard Mode
- [ ] 1.2 Add detection sources block to Step 3's "Read Testing Capabilities": cached capabilities (fast path), `config.yaml testing.framework == kotest`, and `build.gradle.kts` grep fallback
- [ ] 1.3 Add auto-detection log instruction: log `"Kotest detected — strict TDD mode auto-activated"` when tier 3 resolves to strict TDD

## Phase 2: Kotest Detection in sdd-verify (Decision Gates)

- [ ] 2.1 Add Kotest detection row to `~/.config/opencode/skills/sdd-verify/SKILL.md` Decision Gates between existing config check and Standard Mode fallback
- [ ] 2.2 Add resolution pseudocode to sdd-verify Step 3 matching sdd-apply's 4-tier priority chain (flag → config → detection → fallback)

## Phase 3: Trace Verification

- [ ] 3.1 Trace-read sdd-apply resolution chain against FR-1 (Kotest→strict), FR-2 (strict_tdd=false overrides), FR-3 (mode forwarded to apply agent)
- [ ] 3.2 Trace-read sdd-verify Decision Gates: confirm Kotest row preserves NFR-1 (DescribeSpec untouched) and NFR-2 (config-only/rename tasks skip TDD cycle)
