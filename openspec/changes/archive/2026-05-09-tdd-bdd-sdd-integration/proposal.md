# Proposal: TDD + BDD + SDD Integration

## Intent

SIGA's SDD pipeline is mature but its three methodologies operate in isolation. TDD is conditionally enforced, BDD exists only as GWT markdown (not executable), and verify reports issues without feeding them back. This change integrates all three into a unified quality pipeline: **specs drive behavior → behavior drives tests → verification closes the loop**.

## Scope

### In Scope
- **Phase 1 (implement now):** Kotest BehaviorSpec as executable BDD layer — modify `sdd-spec` to generate `BehaviorSpec` stubs from GWT scenarios; add template to `TEST_CONVENTIONS.md`
- **Phase 2 (documented):** TDD strict mode hardened at orchestrator level — `sdd-apply` defaults to `strict_tdd=true` when Kotest is detected
- **Phase 3 (documented):** Feedback loop protocol — `sdd-verify` reports map failures back to `sdd-spec` (spec gaps) or `sdd-apply` (impl gaps)
- **Phase 4 (documented):** Skill registry — `.atl/skill-registry.md` created via `sdd-init` with compact rules for TDD/BDD/SDD

### Out of Scope
- Refactoring existing 37+ `DescribeSpec` tests to `BehaviorSpec` (coexistence is valid)
- Adding Cucumber/Gherkin as alternative BDD framework
- Building a BDD reporting dashboard
- Changing the orchestrator's SDD flow or phases

## Capabilities

No spec-level capabilities change — this is a pure process/tooling improvement. Pipeline behavior is unchanged from the user/system perspective.

### New Capabilities
None.

### Modified Capabilities
None — existing specs (`database`) are unaffected.

## Approach

**Phase 1 (implement now):** Modify `sdd-spec/SKILL.md` — after writing GWT scenarios in markdown, generate Kotest `BehaviorSpec` test stubs in the change directory. Add a `BehaviorSpec` section with GWT templates to `openspec/testing/TEST_CONVENTIONS.md`. No gradle changes needed — Kotest is already a dependency and `BehaviorSpec` is built-in.

**Phase 2 (future):** Modify `sdd-apply/SKILL.md` Step 3 — remove conditional fallback for `strict_tdd`. Default to `true` when Kotest is detected. Honor exceptions for config-only/rename/migration tasks (already in `strict-tdd.md`).

**Phase 3 (future):** Add `## Feedback` section to `sdd-verify` report format. Map each failing spec scenario to its originating phase. Verify only REPORTS — orchestrator decides re-execution.

**Phase 4 (future):** Modify `sdd-init/SKILL.md` to create `.atl/skill-registry.md` with compact rules. Detect Kotest `BehaviorSpec` capability during init.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `~/.config/opencode/skills/sdd-spec/SKILL.md` | Modified | Add BehaviorSpec stub generation step |
| `openspec/testing/TEST_CONVENTIONS.md` | Modified | Add BehaviorSpec section + GWT templates |
| `~/.config/opencode/skills/sdd-apply/SKILL.md` | Modified (P2) | Hardened strict TDD default |
| `~/.config/opencode/skills/sdd-verify/SKILL.md` | Modified (P3) | Add feedback loop protocol |
| `.atl/skill-registry.md` | New (P4) | Skill registry creation |
| `~/.config/opencode/skills/sdd-init/SKILL.md` | Modified (P4) | Add registry + BehaviorSpec detection |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| BehaviorSpec tests + GWT markdown drift apart | Medium | sdd-verify validates both are in sync |
| Strict TDD slows trivial changes | Low | Exceptions exist for config-only/rename/migration |
| Feedback loop creates circular phase dependencies | Low | Verify only REPORTS — orchestrator decides |
| New BehaviorSpec tests increase CI runtime | Low | Runs alongside DescribeSpec — minimal overhead |

## Rollback Plan

- **Phase 1**: Remove BehaviorSpec generation step from sdd-spec. Delete generated stubs.
- **Phase 2**: Revert strict_tdd default to conditional fallback.
- **Phase 3**: Remove Feedback section from verify report format.
- **Phase 4**: Delete `.atl/skill-registry.md`. Revert sdd-init changes.

## Dependencies

None. Kotest is already a project dependency. All changes are within skill configuration files and markdown documentation.

## Success Criteria

- [ ] sdd-spec generates Kotest `BehaviorSpec` stubs from GWT scenarios
- [ ] `TEST_CONVENTIONS.md` documents `BehaviorSpec` with a working GWT template
- [ ] Phases 2–4 are documented as deferred work, ready for continuation
