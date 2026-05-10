# Verify Feedback Specification

## Purpose

Verify reports fail to distinguish ambiguous specs from deviant implementations. This spec defines how failures trace back to their originating phase (`sdd-spec` or `sdd-apply`), so the orchestrator knows where to fix without manual investigation.

## Requirements

### Requirement: Feedback Entries in Verify Report

When a verify report contains issues with severity CRITICAL or WARNING, the report MUST include a `## Feedback` section. Each issue entry in that section MUST include three fields:

- **fed_back_to**: the target phase — `sdd-spec` or `sdd-apply`
- **reason**: a textual explanation of why the failure was classified that way
- **severity**: inherited from the original issue — `CRITICAL` or `WARNING`

The system MUST NOT include a Feedback section when no CRITICAL or WARNING issues exist.

#### Scenario: Feedback section present with issues

- GIVEN a verify report contains 2 CRITICAL issues and 1 WARNING issue
- WHEN the report is generated
- THEN the report MUST include a `## Feedback` section
- AND each of the 3 issues MUST have `fed_back_to`, `reason`, and `severity` fields

#### Scenario: No feedback section without issues

- GIVEN a verify report has zero CRITICAL and zero WARNING issues
- WHEN the report is generated
- THEN the report MUST NOT include a `## Feedback` section

#### Scenario: Severity is preserved from original issue

- GIVEN a CRITICAL issue in the Issues section
- WHEN the Feedback entry is generated for it
- THEN the entry's `severity` MUST be `CRITICAL` (same for WARNING)

### Requirement: Failure Classification Logic

When classifying a failure, the system MUST apply the following rules:

- Spec scenario missing, ambiguous, or incomplete → `SPEC_GAP` → `fed_back_to: sdd-spec`
- Spec scenario clear but implementation diverges → `IMPL_DEVIATION` → `fed_back_to: sdd-apply`

#### Scenario: Ambiguous spec maps to sdd-spec

- GIVEN a spec scenario lacks clear preconditions or expected outcomes
- WHEN a test cannot be written from the spec alone
- THEN the failure MUST be classified as `SPEC_GAP`
- AND `fed_back_to` MUST be `sdd-spec`

#### Scenario: Clear spec but wrong implementation maps to sdd-apply

- GIVEN a spec scenario is complete and unambiguous
- AND a covering test exists but fails because the implementation does not match the spec
- WHEN the failure is classified
- THEN the classification MUST be `IMPL_DEVIATION`
- AND `fed_back_to` MUST be `sdd-apply`

#### Scenario: Missing scenario maps to sdd-spec

- GIVEN a requirement exists in the spec
- BUT no scenario covers the edge case that caused the failure
- WHEN the failure is classified
- THEN the classification MUST be `SPEC_GAP`

### Requirement: Human Review of Classification

The verify agent MUST NOT forward its classification to the next phase automatically. The `## Feedback` section is a report for the orchestrator to review and approve or override before action is taken.

#### Scenario: Orchestrator reviews before forwarding

- GIVEN a verify report contains a `## Feedback` section with classified issues
- WHEN the orchestrator receives the report
- THEN the orchestrator MUST explicitly review and approve each classification
- AND the verify agent MUST NOT auto-forward any classification

#### Scenario: Orchestrator overrides classification

- GIVEN the orchestrator disagrees with a `SPEC_GAP` classification
- WHEN the orchestrator reviews the report
- THEN the orchestrator MAY override `fed_back_to` to a different target
- AND the system MUST accept the override as authoritative

### Requirement: Additive to Existing Report Structure

The `## Feedback` section MUST be appended after the `### Verdict` section. No existing section of the verify report format SHALL be modified, removed, or reordered.

#### Scenario: Existing report fields unchanged

- GIVEN a verify report is generated
- WHEN the report includes the new `## Feedback` section
- THEN all existing sections (Completeness, Build & Tests, Spec Compliance Matrix, Correctness, Coherence, Issues, Verdict) MUST remain unchanged in structure and position
- AND the Feedback section MUST appear only as an addition
