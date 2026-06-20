# Proposal: Frontend Emoji Cleanup

## Intent

Eradicate pictographic emojis from the frontend UI and docs, replacing them with Phosphor Svelte icons for visual consistency and to satisfy the academic criterion "nada de emojis". `ACADEMIC/Frontend.md` claims "se erradicó por completo el uso de emojis" — **false** until this change lands.

## Scope

### In Scope
- Replace pictographic emojis in 8 UI components with Phosphor icons (mapping below).
- Remove emojis from `apps/README.md`, `apps/dashboard/README.md`, `apps/dashboard/STATUS.md` preserving structure.
- Verify `ACADEMIC/Frontend.md` "erradicó" claim becomes true.

### Out of Scope
- `ACADEMIC/` beyond `Frontend.md` (~600 emojis; templates).
- `stitch_ui/` prototypes; code-comment arrows; `(landing)` CSS `content: '✓'` (text checkmark, accepted).
- Charting or icon library change.

## Capabilities

### New Capabilities
- `ui-icon-consistency`: frontend UI MUST render Phosphor Svelte icons for pictographic indicators and MUST NOT render pictographic emojis in user-facing UI. Covers the mapping, severity/trend rendering, doc-consistency.

### Modified Capabilities
- None. `ui-a2ui`, `dashboard-insights`, `sales-analytics` keep functional behavior; the rule is additive.

## Approach

No new dependency: `phosphor-svelte@^3.0.1` is installed (95 imports, tree-shakeable, `duotone` matches glassmorphism, accepts a11y attrs). Mapping:

| Emoji | Phosphor |
|-------|----------|
| 📈 📊 🏪 | `ChartLineUp` `ChartBar` `Storefront` |
| ⚠️ ℹ | `Warning` `Info` |
| 📦 📝 💰 📋 | `Package` `NotePencil` `CurrencyDollar` `Clipboard` |
| ✨ ← | `Sparkle` `CaretLeft` |
| ↑ ↓ → | `TrendUp` `TrendDown` `ArrowRight` |
| 🔴 🟡 | `WarningCircle` red / `WarningCircle` yellow |

Borderline `↑↓→` arrows ARE replaced (Phosphor standard); code-comment arrows stay. Strict TDD: tests asserting icon presence first (RED), then swap (GREEN).

## Affected Areas

| Area | Impact |
|------|--------|
| `a2ui/{ContextualAssistant,A2UIRenderer,AhorremosTiempoButton}.svelte` | Modified |
| `dashboard/{InsightPanel,AnomalyList}.svelte`; `(dashboard)/dashboard/+page.svelte`; `analytics/+page.svelte`; `analytics/predictive/+page.svelte` | Modified |
| `apps/README.md`, `apps/dashboard/{README,STATUS}.md`, `ACADEMIC/Frontend.md` | Modified |

Security impact: none (pure visual presentation).

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Visual regression in chips, badges, severity | Medium | Playwright e2e + visual review; `duotone` preserves glassmorphism |
| TDD tests must precede swap | Medium | RED then GREEN; no existing tests assert emoji text (confirmed) |
| Severity dots lose color semantics | Low | Colored `WarningCircle` (CSS red/yellow), keep `aria-label` |

## Rollback Plan

Revert the commit. Pure presentation tokens — no migration, no state, no API contract. Each file is an independent revertible unit.

## Dependencies

- `phosphor-svelte@^3.0.1` (installed, no version change).

## Success Criteria

- [ ] Zero pictographic emojis in user-facing frontend routes (Playwright scan + grep audit).
- [ ] 8 UI components use Phosphor per mapping; 3 docs emoji-free with structure preserved.
- [ ] `ACADEMIC/Frontend.md` "erradicó" claim verifiable as true.
- [ ] Vitest unit + Playwright e2e pass under strict TDD.
