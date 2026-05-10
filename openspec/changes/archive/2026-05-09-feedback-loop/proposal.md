# Proposal: Feedback Loop — verify → spec/apply traceability

## Intent

`sdd-verify` reports failures but doesn't say *why* they happened or *where* to fix them. Phase 3 adds a Feedback section that traces each failure back to its originating phase (`sdd-spec` or `sdd-apply`), so the orchestrator/user knows exactly whether to re-spec or re-apply — no hunting required.

## Scope

### In Scope
- Add `## Feedback` section to `sdd-verify`'s report format (`references/report-format.md`)
- Add `## Feedback` step to `sdd-verify/SKILL.md` outlining how to classify each failure
- Define 2 severity levels for feedback entries: `SPEC_GAP` and `IMPL_DEVIATION`
- Persist feedback alongside the verify report (hybrid: Engram + openspec)
- Optional: register severity trends in `sdd-init` fragility registry (deferred — wire only)

### Out of Scope
- Auto-remediation (orchestrator decides re-execution)
- Changing sdd-spec, sdd-apply, sdd-design, or orchestrator
- Changing `strict-tdd.md` or `strict-tdd-verify.md`
- Any production code, tests, or build files

## Capabilities

Pure pipeline improvement — no spec-level capabilities change.

### New Capabilities
None.

### Modified Capabilities
None — existing specs (`tdd-enforcement`, `bdd-integration`, `database`) are unaffected.

## Approach

### Failure classification logic
For each failing item in the verify report, the agent checks two signals:
1. **Spec scenario exists but is ambiguous/incomplete** → `SPEC_GAP` — feedback maps to `sdd-spec`
2. **Spec scenario is clear but implementation does not match** → `IMPL_DEVIATION` — feedback maps to `sdd-apply`

### Report section format
```
## Feedback

| Failure | Type | Origin | Description |
|---------|------|--------|-------------|
| {REQ} | SPEC_GAP / IMPL_DEVIATION | sdd-spec / sdd-apply | {root cause} |

**Trend**: {escalating | stable | first occurrence}
```

### Fragility registry (optional)
Add a lightweight counter in `sdd-init` keyed by requirement ID. Increment on each `SPEC_GAP` or `IMPL_DEVIATION`. If count exceeds 3, tag as `fragile` in the report.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `~/.config/opencode/skills/sdd-verify/SKILL.md` | Modified | Add Feedback step + output section to Execution Steps |
| `~/.config/opencode/skills/sdd-verify/references/report-format.md` | Modified | Add Feedback section template, SPEC_GAP/IMPL_DEVIATION statuses |
| `~/.config/opencode/skills/sdd-init/SKILL.md` | Optional | Add fragility counter (deferred wiring) |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| False classification (SPEC_GAP vs IMPL_DEVIATION wrong) | Medium | Human review required — verify only REPORTS, orchestrator decides |
| Feedback noise for flaky tests | Low | Flaky tests detected by test runner, not feedback; feedback classifies spec/impl only |
| Feedback section too large for small changes | Low | Report template only emits Feedback when failures exist; empty section = no failures |
| Fragility counter over-engineering | Medium | Defer fragility registry — ship without it, add only if trends show it matters |

## Rollback Plan

- Revert `sdd-verify/SKILL.md` and `references/report-format.md` to pre-Phase 3 state
- Remove Feedback section from verify report format
- Remove any fragility-registry wiring from `sdd-init/SKILL.md` (if implemented)

## Dependencies

None. All changes are within skill configuration files in `~/.config/opencode/skills/`.

## Success Criteria

- [ ] Verify report includes `## Feedback` section with per-failure type and origin mapping
- [ ] Each failure is classified as `SPEC_GAP` (sdd-spec) or `IMPL_DEVIATION` (sdd-apply)
- [ ] Empty feedback section when no failures exist
- [ ] Fragility registry wiring in `sdd-init` is deferred unless trends demand it
