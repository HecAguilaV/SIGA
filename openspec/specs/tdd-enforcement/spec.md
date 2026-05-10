# TDD Enforcement Specification

## Purpose

Define requirements for making strict TDD (RED→GREEN→TRIANGULATE→REFACTOR) the default mode in the SDD pipeline when Kotest is detected, with explicit opt-out support. This is a pure process/tooling change — no new business domain.

## Requirements

### FR-1: Kotest auto-detection for strict TDD

When sdd-apply or sdd-verify starts, the system MUST detect Kotest presence from `build.gradle.kts` (or cached testing capabilities) and default to strict TDD mode without requiring an explicit orchestrator flag.

#### Scenario: Kotest detected → strict TDD activates

- GIVEN a project with Kotest in its `build.gradle.kts`
- WHEN sdd-apply launches with no explicit `strict_tdd` flag
- THEN the resolution logic selects strict TDD mode
- AND `sdd-apply` receives the strict TDD module instruction

#### Scenario: No Kotest → Standard Mode

- GIVEN a project without Kotest dependencies
- WHEN sdd-apply launches with no explicit `strict_tdd` flag
- THEN the resolution falls through to Standard Mode
- AND no TDD enforcement module is loaded

### FR-2: Explicit opt-out overrides detection

The system MUST honor `strict_tdd=false` from orchestrator flag or `openspec/config.yaml` even when Kotest is present. Explicit false MUST take priority over auto-detection.

#### Scenario: strict_tdd=false overrides Kotest presence

- GIVEN a project with Kotest in its dependencies
- AND the orchestrator passes `strict_tdd=false`
- WHEN sdd-apply launches
- THEN Standard Mode is used
- AND the override is logged for audit

### FR-3: Mode forwarding to apply agent

When strict TDD mode resolves to true, the orchestrator MUST inject the instruction `"STRICT TDD MODE IS ACTIVE"` into the sdd-apply launch context.

#### Scenario: Strict mode communicated downstream

- GIVEN strict TDD mode resolves to true
- WHEN the orchestrator constructs the sdd-apply launch instruction
- THEN the instruction body contains `"STRICT TDD MODE IS ACTIVE"`
- AND sdd-apply loads the strict TDD module

### NFR-1: Zero breaking changes to DescribeSpec tests

Strict TDD mode MUST NOT modify, refactor, or alter execution of existing DescribeSpec tests. Detection operates at the framework level — individual spec styles are irrelevant.

#### Scenario: DescribeSpec tests remain untouched

- GIVEN a project with existing DescribeSpec tests
- WHEN strict TDD mode is active
- THEN all DescribeSpec tests continue to compile and pass
- AND no DescribeSpec file is touched by the pipeline

### NFR-2: Config-only/rename tasks exempt from TDD cycle

Tasks classified as config-only or structural rename MUST skip the strict TDD cycle entirely, as defined by existing `strict-tdd.md` skip rules.

#### Scenario: Config-only task bypasses TDD

- GIVEN a task classified as config-only or rename
- WHEN sdd-apply processes that task in strict TDD mode
- THEN the RED→GREEN→TRIANGULATE→REFACTOR cycle is skipped
- AND the task proceeds directly to Standard Mode handling

## Resolution Priority

| Priority | Source | Example |
|----------|--------|---------|
| 1 (highest) | Orchestrator explicit flag | `strict_tdd=false` |
| 2 | `openspec/config.yaml` | `strict_tdd: false` |
| 3 | Kotest detection | `build.gradle.kts` has `io.kotest` |
| 4 (fallback) | Standard Mode | No Kotest, no config |

## Affected Pipeline Files

- `~/.config/opencode/skills/sdd-apply/SKILL.md` — Step 3 resolution logic
- `~/.config/opencode/skills/sdd-verify/SKILL.md` — Decision Gates resolution logic
