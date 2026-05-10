# Design: Feedback Loop — verify → spec/apply traceability

## Technical Approach

ADDITIVE modification to `sdd-verify`'s report format. After verifying, the agent classifies each CRITICAL/WARNING issue using a deterministic 3-question decision tree, appends a `## Feedback` section after `### Verdict`, and returns the enriched report to the orchestrator. Classification stays in-report — no new databases, storage layers, or auto-routing.

## Architecture Decisions

### Decision: Append after Verdict, not before

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Before Verdict (after Issues) | Verdict is the natural conclusion; feedback after it reads as postscript | Rejected — breaks reader expectation |
| **After Verdict** | Verdict closes the report; feedback after it is contextual advisory, not structural | ✅ Chosen — spec mandates it, and it preserves the report as a self-contained judgment |
| Separate file | Clean separation | Rejected — breaks `report-format.md` template contract; adds coordination overhead |

### Decision: Deterministic 3-question tree, not ML/heuristics

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Classification via ML/stats | Over-engineered for a binary decision | Rejected — not justified |
| **Deterministic rules in verify step** | Simple, auditable, trivially correct | ✅ Chosen — fits in a code comment; orchestrator overrides if wrong |

### Decision: Evidence as inline markdown, not file links

| Option | Tradeoff | Decision |
|--------|----------|----------|
| File/line references | Precise but brittle (lines shift) | Rejected — maintenance burden |
| **Inline text excerpt** | Slightly more verbose but survives refactors | ✅ Chosen — `reason` carries the core explanation; `evidence` is optional structured context (spec quote, impl diff snippet) |

## Classification Decision Tree

```
For each CRITICAL/WARNING issue I:
  1. Does a spec scenario exist for I?
     NO  → SPEC_GAP → fed_back_to: sdd-spec
     YES → continue
  2. Is the spec scenario clear (preconditions + expected outcome)?
     NO  → SPEC_GAP → fed_back_to: sdd-spec
     YES → continue
  3. Does the implementation match the spec?
     NO  → IMPL_DEVIATION → fed_back_to: sdd-apply
     YES → (shouldn't reach here — classification error)
```

## Feedback Fields

| Field | Type | Required | Example |
|-------|------|----------|---------|
| `fed_back_to` | `sdd-spec` / `sdd-apply` | yes | `sdd-spec` |
| `reason` | free text | yes | `"Scenario missing for null-input edge case"` |
| `severity` | `CRITICAL` / `WARNING` | yes | `CRITICAL` — inherited verbatim from original issue |
| `evidence` | free text (optional) | no | `"Spec covers happy path but says nothing about null inputs. No GWT scenario defines expected behavior."` |

## Data Flow

```
verify-agent builds standard report (sections 1-7)
         │
         ▼
  For each CRITICAL/WARNING issue:
    apply 3-question tree → SPEC_GAP | IMPL_DEVIATION
    build Feedback entry {fed_back_to, reason, severity, evidence?}
         │
         ▼
  Append "## Feedback" after "### Verdict"
         │
         ▼
  Persist hybrid report (Engram topic_key + openspec file)
         │
         ▼
  Orchestrator receives → reviews entries →
    ├── approves → re-runs sdd-spec (if SPEC_GAP) or sdd-apply (if IMPL_DEVIATION)
    └── overrides → changes fed_back_to manually → re-runs target phase
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `skills/sdd-verify/references/report-format.md` | Modify | Add `## Feedback` template block after the Verdict section |
| `skills/sdd-verify/SKILL.md` | Modify | Add Step 9 (classify failures, append Feedback) to Execution Steps; add Feedback entries to Output Contract |

## What Stays Unchanged

- All existing report sections: Completeness, Build & Tests, Compliance Matrix, Correctness, Coherence, Issues, Verdict — identical structure and ordering
- Compliance statuses (`COMPLIANT`, `FAILING`, `UNTESTED`, `PARTIAL`) — untouched
- Decision gates in `sdd-verify/SKILL.md` — no new gates, no modified conditions
- Strict TDD mode and `strict-tdd-verify.md` — not touched
- `sdd-spec`, `sdd-apply`, `sdd-design`, orchestrator skills — zero changes
- Fragility registry — deferred per spec/proposal (wire only if trends demand it)
- `sdd-init` — unchanged (fragility counter is deferred)

## Open Questions

None.
