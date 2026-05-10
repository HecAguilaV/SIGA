# Design: TDD Strict Mode as Default

## Technical Approach

Add Kotest auto-detection as a middle tier in the strict TDD mode resolution chain. Both `sdd-apply` (Step 3) and `sdd-verify` (Decision Gates) gain a new detection step between config check and Standard Mode fallback. Detection queries three sources in order: cached testing capabilities, `openspec/config.yaml`, and `build.gradle.kts` scan.

## Architecture Decisions

### Decision: Detection Source Priority

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Only build.gradle.kts scan | Reliable but slow — reads filesystem every time | ❌ Rejected |
| Cached capabilities only | Fastest but can be stale | ❌ Rejected |
| Tiered: cache → config → filesystem | Fast path (cache) + fallback accuracy | ✅ **Chosen** |

**Rationale**: Cached capabilities (Engram `sdd/{project}/testing-capabilities`) already contain `Framework: JUnit 5 + Kotest 6.0.0` — zero I/O for the common case. Config.yaml is a local file read. `build.gradle.kts` grep is the slowest path and only triggers when both cache and config are absent or ambiguous.

### Decision: Logging Auto-Detection

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Silent activation | Clean but confusing — developer wonders why strict mode is on | ❌ Rejected |
| Log at each resolution step | Verbose — clutters output | ❌ Rejected |
| Log once when tier 3 triggers | Single info line: "Kotest detected — strict TDD mode auto-activated" | ✅ **Chosen** |

**Rationale**: When strict mode activates via auto-detection (not explicit flag/config), the agent logs it once in the return summary. This makes activation transparent without noise.

### Decision: No Changes to strict-tdd.md / strict-tdd-verify.md

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Add detection logic inside strict-tdd.md | Would couple detection to the TDD module itself | ❌ Rejected |
| Keep detection outside, in SKILL.md Step 3 / Decision Gates | Clean separation: SKILL.md resolves mode, strict-tdd.md enforces it | ✅ **Chosen** |

**Rationale**: The existing strict-tdd.md already correctly says "If you are reading this, orchestrator already verified conditions." Detection belongs upstream in the resolution logic — the TDD module is a consumer, not a decider.

## Data Flow

```
Orchestrator launches sdd-apply / sdd-verify
    │
    ▼
Resolution Chain (Step 3 / Decision Gates):
    │
    ├── 1. Orchestrator passed explicit strict_tdd flag?
    │       └── HONOR directly — highest priority
    │
    ├── 2. openspec/config.yaml has strict_tdd key?
    │       └── HONOR directly
    │
    ├── 3. Kotest detected?  ← NEW TIER
    │       ├── Check cached capabilities (fast path)
    │       ├── Check config.yaml testing.framework == "kotest"
    │       └── Fallback: grep build.gradle.kts for io.kotest
    │       └── If detected → STRICT TDD MODE + log
    │
    └── 4. ELSE → Standard Mode
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `~/.config/opencode/skills/sdd-apply/SKILL.md` | Modify | Step 3: add Kotest auto-detection tier between config check and Standard Mode |
| `~/.config/opencode/skills/sdd-verify/SKILL.md` | Modify | Decision Gates: add Kotest auto-detection row between config check and fallback |

## Files Not Changing

- `~/.config/opencode/skills/sdd-apply/strict-tdd.md` — TDD cycle logic is correct
- `~/.config/opencode/skills/sdd-verify/strict-tdd-verify.md` — TDD verification logic is correct
- `~/.config/opencode/skills/_shared/*` — shared protocol unchanged
- `openspec/config.yaml` — already has `strict_tdd: true` and `testing.kotlin.framework: kotest`
- Any project-level code or tests

## Rollback

1. Revert both SKILL.md files to previous resolution logic:
   - `sdd-apply/SKILL.md` Step 3: remove the Kotest detection tier, keep only `strict_tdd` flag + config check
   - `sdd-verify/SKILL.md` Decision Gates: remove the Kotest detection row
2. No data migration — strict-tdd.md and strict-tdd-verify.md remain untouched

## Interfaces / Contracts

No new interfaces. The existing `strict_tdd` boolean contract is extended by the detection logic — the mode resolution now considers Kotest presence as a truthy input.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Detection logic operates correctly | Manual verification by reading updated SKILL.md resolution steps |
| Integration | Full chain: empty config → Kotest detected → module loads | Trace through the resolution pseudocode |

Testability note: These are pipeline skill files (Markdown instructions to agents), not code. Testing is by human review and trace verification.

## Open Questions

None.
