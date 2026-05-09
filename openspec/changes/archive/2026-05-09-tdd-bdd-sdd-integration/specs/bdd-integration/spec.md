# BDD Integration Specification

## Purpose

Define requirements for integrating executable BDD into SIGA's SDD pipeline via Kotest BehaviorSpec. The goal is zero-new-dependency executable GWT scenarios — BehaviorSpec is built into Kotest, which is already a project dependency.

## Requirements

### Functional

#### FR-1: BehaviorSpec stub generation

After writing GWT scenarios in spec markdown, sdd-spec MUST generate a `.behavior.kts` Kotest BehaviorSpec stub file in `openspec/changes/{change-name}/specs/{domain}/`. The stub MUST mirror each GIVEN/WHEN/THEN block as a `given`-`When`-`Then` chain.

#### FR-2: BehaviorSpec template in TEST_CONVENTIONS

TEST_CONVENTIONS.md MUST include a BehaviorSpec subsection with a complete compilable template showing Given/When/Then usage alongside DescribeSpec, plus coexistence guidance.

### Non-Functional

#### NFR-1: Zero new dependencies

Generated stubs MUST compile with Kotest's built-in `BehaviorSpec` — no additions to `build.gradle.kts`.

#### NFR-2: Generated stubs must not pass without implementation

Every scenario body MUST be wrapped in `pending { }` so the stub compiles but never produces a passing or failing test.

#### NFR-3: No overwrite of existing stubs

sdd-spec MUST NOT overwrite an existing `.behavior.kts` file. If one exists, it MUST log a warning and skip generation for that domain.

## BDD Scenarios

### Feature: BDD Integration via Kotest BehaviorSpec

#### Scenario: sdd-spec generates BehaviorSpec stubs from GWT markdown

- GIVEN a spec file with GWT scenarios
- WHEN sdd-spec completes
- THEN a `.behavior.kts` file is created in the spec directory
- AND the stub contains Given/When/Then matching the GWT

#### Scenario: BehaviorSpec stub compiles but does not run

- GIVEN a generated `.behavior.kts` stub
- WHEN the test suite executes
- THEN the stub is compiled
- AND all scenarios are marked as pending

#### Scenario: TEST_CONVENTIONS documents BehaviorSpec

- GIVEN the TEST_CONVENTIONS.md file
- WHEN a developer reads the Testing section
- THEN they find a BehaviorSpec template
- AND the template shows Given/When/Then usage

#### Scenario: Existing stub is preserved on re-run

- GIVEN an existing `.behavior.kts` file in the target directory
- WHEN sdd-spec runs again
- THEN the existing file is NOT overwritten
- AND a warning is logged

## Acceptance Criteria

- [ ] sdd-spec/SKILL.md has an explicit step for `.behavior.kts` generation after GWT scenarios
- [ ] Generated stub compiles clean with `kotest-runner-junit5` — zero new deps
- [ ] Generated stub uses `pending { }` — no scenario body executes
- [ ] TEST_CONVENTIONS.md contains a BehaviorSpec subsection with a compilable template
- [ ] Template documents `given`-`When`-`Then` as first-class Kotest words
- [ ] Template explains coexistence with existing DescribeSpec tests
- [ ] sdd-spec warns (does not overwrite) if `.behavior.kts` already exists

## File-Level Specification

### `~/.config/opencode/skills/sdd-spec/SKILL.md` (modified)

- After Step 4 (Write Delta Specs), insert **Step 4a: Generate BehaviorSpec stubs**.
- For each domain with GWT scenarios, create `openspec/changes/{change-name}/specs/{domain}/{Domain}BehaviorSpec.kts`.
- Map each scenario's GIVEN → `given`, WHEN → `When`, THEN → `Then`.
- Wrap each `Then` body in `pending { }`.
- Skip if target file already exists.

### `openspec/testing/TEST_CONVENTIONS.md` (modified)

- Add new subsection **BehaviorSpec** after the existing "Testing en Kotlin" section.
- Content: imports (`io.kotest.core.spec.style.BehaviorSpec`), class naming convention (`{Feature}BehaviorSpec`), template with `given`-`When`-`Then` blocks, coexistence note with DescribeSpec.
