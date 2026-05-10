# Proposal: TDD Strict Mode as Default

## Intent

Make strict TDD the default when Kotest is detected. Currently, `strict_tdd=true` activates conditionally (orchestrator flag or config). Phase 2 removes the silent fallback: if Kotest is the test framework, TDD strict mode activates automatically. Developers opt *out* explicitly, not the other way around.

## Scope

### In Scope
- Modify `sdd-apply/SKILL.md` Step 3 — when Kotest detected, default `strict_tdd=true` even without explicit orchestrator flag or config
- Modify `sdd-verify/SKILL.md` Decision Gates — same default logic for verify phase
- Keep explicit opt-out: `strict_tdd=false` in config or orchestrator flag still overrides

### Out of Scope
- Changes to `strict-tdd.md` or `strict-tdd-verify.md` (already correct)
- Modifying the orchestrator's SDD flow or phases
- Refactoring existing 37+ DescribeSpec tests (coexistence is valid)
- Creating new skill files

## Capabilities

None — pure pipeline behavior change. No spec-level requirements change.

### New Capabilities
None.

### Modified Capabilities
None.

## Approach

**Detection**: Read Kotest detection from (1) cached testing capabilities (`sdd/{project}/testing-capabilities`), (2) `openspec/config.yaml` testing section, (3) fallback to `build.gradle.kts` / `pom.xml` scan for `io.kotest`.

**Default logic** (sdd-apply Step 3, updated):
```
├── IF explicit strict_tdd flag from orchestrator → honor it (true or false)
├── IF openspec/config.yaml has strict_tdd → honor it
├── IF Kotest detected (from any source) → default strict_tdd=true
├── ELSE → Standard Mode (no TDD module loaded)
```

**sdd-verify** mirror the same logic in its Decision Gates.

**Opt-out**: Setting `strict_tdd: false` in `openspec/config.yaml` or passing `strict_tdd=false` from orchestrator disables it — even with Kotest present.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `~/.config/opencode/skills/sdd-apply/SKILL.md` | Modified | Step 3 resolution logic — add Kotest auto-detection default |
| `~/.config/opencode/skills/sdd-verify/SKILL.md` | Modified | Decision Gates — mirror same auto-detection |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Config-only/rename tasks forced into TDD cycle | Low | `strict-tdd.md` already exempts structural tasks (triangulation skip rules) |
| Kotest false positive (e.g., dependency unused) | Low | Kotest detection from `build.gradle.kts` is reliable; opt-out exists |
| Existing DescribeSpec tests confuse detection | None | Detection is framework-level, not spec-level |

## Rollback Plan

Revert `sdd-apply/SKILL.md` and `sdd-verify/SKILL.md` to the previous resolution logic (orchestrator flag or config only). No data loss — strict-tdd.md files remain untouched.

## Dependencies

None. Kotest is already a project dependency across all services.

## Success Criteria

- [ ] sdd-apply activates strict TDD mode when Kotest is detected (no explicit flag needed)
- [ ] sdd-verify mirrors same auto-detection in its Decision Gates
- [ ] Explicit `strict_tdd=false` in config still disables strict mode
- [ ] Existing non-Kotest projects remain in Standard Mode
