# Archive Report: Feedback Loop — verify → spec/apply traceability

## Summary

**Change**: feedback-loop
**Phase**: 3 — Feedback loop from verify to spec/apply
**Archived at**: 2026-05-09
**Archived to**: `openspec/changes/archive/2026-05-09-feedback-loop/`
**Mode**: hybrid (Engram + openspec)
**Status**: Complete — PASS

## Artifact Traceability

| Artifact | Engram ID | Filesystem |
|----------|-----------|------------|
| Proposal | #618 ✅ | `openspec/changes/archive/2026-05-09-feedback-loop/proposal.md` ✅ |
| Spec | #620 ✅ | `openspec/specs/verify-feedback/spec.md` (main spec — no delta existed) ✅ |
| Design | #621 ✅ | `openspec/changes/archive/2026-05-09-feedback-loop/design.md` ✅ |
| Tasks | #622 ✅ | `openspec/changes/archive/2026-05-09-feedback-loop/tasks.md` ✅ |
| Apply Progress | #623 ✅ | (Engram-only — inline observation) |
| Verify Report | #625 ✅ | (Engram-only — inline observation; no filesystem file) |

## What Was Implemented

Additive `## Feedback` section to sdd-verify's report format with deterministic failure classification:

- **SPEC_GAP** → fed_back_to: sdd-spec (ambiguous/incomplete/missing spec scenario)
- **IMPL_DEVIATION** → fed_back_to: sdd-apply (clear spec but wrong implementation)

### Files Changed (system-level skills, not in project repo)
- `~/.config/opencode/skills/sdd-verify/SKILL.md` — Added Step 9 (classification decision tree), extended Output Contract
- `~/.config/opencode/skills/sdd-verify/references/report-format.md` — Added `## Feedback` template after `### Verdict`

## Verification Results

- **9/9 spec scenarios compliant** ✅
- **FR-1**: Feedback entries in verify report ✅
- **FR-2**: Failure classification logic (SPEC_GAP/IMPL_DEVIATION) ✅
- **FR-3**: Human review of classification ✅
- **NFR-1**: Additive to existing report structure ✅
- **Verdict**: PASS ✅
- **Issues**: Zero CRITICAL, zero WARNING, zero SUGGESTION

## Specs Synced

| Domain | Action | Details |
|--------|--------|---------|
| verify-feedback | Already in place | Spec delta was written directly to main spec (`openspec/specs/verify-feedback/spec.md`). No separate delta spec existed in the change folder. |

## Spec Source of Truth

`openspec/specs/verify-feedback/spec.md` — contains 4 requirements with 9 scenarios. No changes needed during archive — the spec was already at its final location.

## Key Decisions

1. **Deterministic 3-question tree** (not ML/heuristics) — simple, auditable, trivially correct
2. **Feedback after Verdict** — preserves report as self-contained judgment; feedback is contextual advisory
3. **Evidence as inline markdown** — survives refactors better than file/line references
4. **Human review required** — orchestrator MUST review classifications; no auto-forwarding
5. **Fragility registry deferred** — not implemented; wire only if trends show it matters

## Lessons Learned

- Spec was written directly to main specs location, bypassing the delta pattern. Future changes should ensure delta specs land in `openspec/changes/{change-name}/specs/{domain}/` for proper delta merge during archive.
- Verify-report.md was not written to the filesystem although the openspec convention specifies it. Future sdd-verify phases should ensure filesystem persistence to maintain the audit trail in the archive.
- The implementation is purely additive (markdown-only template changes in `~/.config/opencode/skills/`), so there's no project code impact.

## Current State

- ✅ Feedback section is part of the verify report format
- ✅ Classification decision tree is embedded in sdd-verify/SKILL.md
- ✅ Conditional emission (no Feedback when no CRITICAL/WARNING issues)
- ✅ Human review requirement documented
- 🟡 Fragility registry in sdd-init is deferred (proposal explicitly deferred it)

## Next Steps

- **Phase 4**: (from TDD+BDD+SDD Integration plan) — continue with next planned integration phase
- If feedback trends show repeated SPEC_GAP on the same requirement, implement fragility registry in sdd-init
