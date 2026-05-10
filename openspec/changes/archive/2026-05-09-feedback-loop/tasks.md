# Tasks: Feedback Loop — verify → spec/apply traceability

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~30-50 (markdown only) |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | exception-ok |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Additive Feedback section to verify report (2 files) | Single PR | exception-ok; ~40 lines of markdown |

## Phase 1: Report Format Template

- [x] 1.1 Add `## Feedback` section template after `### Verdict` in `references/report-format.md` — include fields: `fed_back_to`, `reason`, `severity`, `evidence`; conditional emission (no Feedback when zero CRITICAL/WARNING issues)

## Phase 2: SKILL.md Integration

- [x] 2.1 Add Step 9 (classify failures, append Feedback) to Execution Steps in `sdd-verify/SKILL.md` — embed the 3-question decision tree as a code-comment-style block
- [x] 2.2 Add Feedback entries (`fed_back_to`, `reason`, `severity`, `evidence`) to the Output Contract section in `sdd-verify/SKILL.md`

## Phase 3: Verification

- [ ] 3.1 Verify Feedback section appears only when CRITICAL/WARNING issues exist (matches spec scenarios: "Feedback section present with issues" + "No feedback section without issues")
- [ ] 3.2 Verify all pre-existing report sections (Completeness, Build & Tests, Compliance Matrix, Correctness, Coherence, Issues, Verdict) remain structurally unchanged
