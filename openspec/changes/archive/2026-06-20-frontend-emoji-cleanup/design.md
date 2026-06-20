# Design: Frontend Emoji Cleanup

## Context

SIGA's `ACADEMIC/Frontend.md` claims "se erradicó por completo el uso de emojis" — false until this change lands. Pictographic emojis survive in 8 UI components and 3 frontend docs. `phosphor-svelte@^3.0.1` is already installed (~95 deep imports, tree-shakeable, `duotone` matches the glassmorphism language). This change is pure visual presentation: no API, no state, no migration. Rollback = revert. See `proposal.md` and the `ui-icon-consistency` capability.

Source of truth for color tokens is `apps/dashboard/src/app.css` (M3 palette), NOT the project-context "Deep Teal/Cyan" description. Relevant tokens: `--color-error: #ba1a1a` (red), `--color-warning: #f97316` (orange — not yellow), `--color-success: #10B981`. Both severity tokens **exist**, so no fallback hex is needed.

## Technical Approach

Swap each pictographic emoji for a Phosphor Svelte component using the project's existing deep-import convention `phosphor-svelte/lib/{Name}`. Keep existing styled host elements (`<span class="chip-icon">`, `.anomaly-severity`, `.a2ui-empty-icon`, `.kpi-icon`) and drop the Phosphor SVG inside, preserving all current CSS sizing/borders. Severity color is carried by the host via inline `style="color: var(--color-*)"` (Phosphor uses `currentColor`). Strict TDD: RED test asserting the Phosphor SVG renders (or aria-label present) → GREEN swap. Unit layer for 5 lib components, e2e layer for 3 route pages (no `+page.svelte` has a unit test in this repo).

## Architecture Decisions

### Decision: Final emoji → Phosphor mapping, weight, size

**Choice** — Weights: `duotone` for feature-card icons (glassmorphism), `bold` for inline indicators in chips/badges/toggles, `regular` where the emoji was flat. Sizes follow the host context.

| Emoji | Phosphor | Weight | Size | Context |
|-------|----------|--------|------|---------|
| 📈 | `ChartLineUp` | bold / duotone | 14 chip / 28 kpi | chip / KPI card |
| 📊 | `ChartBar` | bold / duotone | 14 / 28 | chip / KPI card |
| 🏪 | `Storefront` | duotone | 28 | KPI card |
| ⚠️ / ⚠ | `Warning` | bold / duotone | 14 / 28 | chip / badge / KPI |
| ℹ | `Info` | bold | 14 | insight badge |
| 📦 | `Package` | bold / duotone | 14 / 28 | chip / KPI |
| 📝 | `NotePencil` | bold | 14 | chip |
| 💰 | `CurrencyDollar` | bold / duotone | 14 / 28 | chip / KPI |
| 📋 | `Clipboard` | regular | 40 | empty-state (flat) |
| ✨ | `Sparkle` | bold | 16 | toggle |
| ← | `CaretLeft` | bold | 16 | toggle |
| ↑ | `TrendUp` | bold | 12–14 | trend inline / insight |
| ↓ | `TrendDown` | bold | 12–14 | trend inline |
| → | `ArrowRight` | bold | 14 | flat-trend badge |
| 🔴 | `WarningCircle` | bold | 16 | severity red |
| 🟡 | `WarningCircle` | bold | 16 | severity yellow |

**Alternatives considered** — One weight everywhere (rejected: flattens the glassmorphism hierarchy); `thin` for flat emojis (rejected: too light vs. current emoji visual weight).

**Rationale** — Matches the proposal mapping and the design system's duotone-for-glass pattern; sizes preserve current visual rhythm (chip ~14, KPI ~28, severity ~16, empty-state ~40 = 2.5rem host).

### Decision: Severity rendering (🔴 / 🟡)

**Choice** — Single `WarningCircle` component; color driven by the host span's inline `style="color: var(--color-error)"` (critical/high) or `var(--color-warning)"` (medium/low). Host span keeps/gets `aria-label="Severidad: {severity}"`; the Phosphor SVG is `aria-hidden="true"` (host label is the accessible name).

**Rationale** — `--color-warning` is orange `#f97316` (M3), not yellow — semantically correct for "warning" and keeps a single source of truth. Color alone is not accessible, so the host `aria-label` MUST remain/stay. Two icons would be redundant; one icon + CSS color is the Phosphor-idiomatic pattern.

### Decision: Trend arrows (↑ ↓ →) replaced

**Choice** — Replace with `TrendUp` / `TrendDown` / `ArrowRight` inline, size matching surrounding text (12–14). `aria-hidden="true"` when adjacent numeric text (e.g. `{trendValue}%`) is present; `aria-label` otherwise.

**Rationale** — Borderline-typographic, but replaced for full Phosphor consistency per proposal. The adjacent `%` value is read by AT; adding an `up`/`down` label next to `8%` is chatty, so `aria-hidden` wins when a number is adjacent. (Minor a11y tradeoff flagged in Risks.)

### Decision: Chip-icon host pattern (standard for this change)

**Choice** — KEEP `<span class="chip-icon">` as the styled host; put the Phosphor component inside:
```svelte
<span class="chip-icon"><ChartLineUp size={14} weight="bold" aria-hidden="true" /></span> ¿Ventas de hoy?
```
**Rationale** — Preserves existing `.chip-icon` CSS (sizing/border/gap) and minimizes diff. Same pattern applies to `.kpi-icon`, `.anomaly-severity`, `.a2ui-empty-icon`, `.a2ui-toggle-icon`.

### Decision: A11y rules

**Choice** — State-conveying icons (severity, insight type, anomaly) get `aria-label` (on the icon or its host); decorative icons (chips where button text labels, KPI cards where `<p class="kpi-title">` labels, empty-state where adjacent text labels, toggle where the button has `aria-label`) get `aria-hidden="true"`.

**Rationale** — Color/emoji must not be the sole carrier of meaning; where adjacent text already labels the element, the icon is decorative and hiding it avoids double-announcement.

### Decision: CSS `content: '✓'` OUT OF SCOPE (accepted)

**Choice** — No change to `(landing)/+page.svelte` L852 `content: '✓'`.
**Rationale** — A CSS pseudo-element `content` cannot host a Svelte component (Phosphor renders an SVG component), and `✓` (U+2713) is a text checkmark, not a pictographic emoji. Matches proposal out-of-scope.

### Decision: Test strategy (strict TDD)

**Choice** — Two-layer:
- **Unit (Vitest, jsdom, `@testing-library/svelte`)** for the 5 `lib/components/*` files. RED: assert Phosphor SVG renders in the host (`.chip-icon svg`, `.anomaly-severity svg`, `.a2ui-empty-icon svg`) or assert accessible name via `getByLabelText`/host `aria-label`. GREEN: swap emoji → component. Phosphor SVGs carry no stable name attribute, so unit asserts presence/aria-label, not icon identity (visual identity covered by e2e).
- **E2e (Playwright)** for the 3 route pages — no `+page.svelte` has a unit test in this repo. RED: new `tests/e2e/emoji-audit.spec.ts` logs in (reuse `auth.spec.ts` flow) and visits `/dashboard`, `/analytics`, `/analytics/predictive`, asserting `document.body.textContent` contains no pictographic emoji range. GREEN: swap. Existing `auth.spec.ts` MUST keep passing.

**Alternatives considered** — Unit-mount the `+page.svelte` files (rejected: they import `$app/*`, load functions, BFF data — no repo precedent and high mock cost). Grep-only audit (rejected: not a runtime proof).

### Decision: Doc update approach

**Choice** — Remove pictographic emojis from `apps/README.md`, `apps/dashboard/README.md`, `apps/dashboard/STATUS.md` preserving markdown structure. Section headers lose the emoji prefix (`## 📂 Estructura` → `## Estructura`). Tree-diagram inline comments drop the emoji (`# 📖 Este archivo` → `# Este archivo`). `STATUS.md` trailing `✅` → ` — DONE` (e.g. `## F1: Scaffold + Auth + Design System — DONE`). Typographic `→` arrows in prose (e.g. `1s → 2s → 4s`) STAY — markdown cannot host Phosphor and `→` is not pictographic. `ACADEMIC/Frontend.md` is NOT modified; apply phase only verifies the "erradicó" claim becomes true.

**Rationale** — `cognitive-doc-design`: lead with structure, preserve scannability, no emoji-as-decoration. Replacing `✅` with ` — DONE` keeps the completion signal without pictographs.

## File-by-file change list

### UI components (8)

| File | Line | Current | Target (name / weight / size) | a11y | Test file |
|------|------|---------|-------------------------------|------|-----------|
| `lib/components/a2ui/ContextualAssistant.svelte` | 221 | `📈` | `ChartLineUp` bold 14 | `aria-hidden` | extend `tests/unit/components/a2ui/ContextualAssistant.test.ts` |
| same | 224 | `⚠️` | `Warning` bold 14 | `aria-hidden` | same |
| same | 227 | `📊` | `ChartBar` bold 14 | `aria-hidden` | same |
| same | 231 | `📦` | `Package` bold 14 | `aria-hidden` | same |
| same | 234 | `📝` | `NotePencil` bold 14 | `aria-hidden` | same |
| same | 237 | `💰` | `CurrencyDollar` bold 14 | `aria-hidden` | same |
| `lib/components/a2ui/A2UIRenderer.svelte` | 67 | `📋` | `Clipboard` regular 40 | `aria-hidden` | extend `tests/unit/components/a2ui/A2UIRenderer.test.ts` |
| `lib/components/a2ui/AhorremosTiempoButton.svelte` | 48 | `←` | `CaretLeft` bold 16 | `aria-hidden` (button has aria-label) | CREATE `tests/unit/components/a2ui/AhorremosTiempoButton.test.ts` |
| same | 50 | `✨` | `Sparkle` bold 16 | `aria-hidden` | same (new) |
| `lib/components/dashboard/InsightPanel.svelte` | 45 | `↑` / `⚠` / `🔴` / `ℹ` (ternary) | `TrendUp` / `Warning` / `WarningCircle`(red,16) / `Info` bold 14 | `aria-label="{type} insight"` | extend `tests/unit/components/dashboard/InsightPanel.test.ts` |
| `lib/components/dashboard/AnomalyList.svelte` | 32–33 | `🔴` / `🟡` (ternary) | `WarningCircle` bold 16, host `style="color: var(--color-error\|warning)"` | host keeps `aria-label="Severidad: {severity}"`; SVG `aria-hidden` | extend `tests/unit/components/dashboard/AnomalyList.test.ts` |
| `routes/(dashboard)/dashboard/+page.svelte` | 175 | `📊`/`🏪`/`⚠️`/`💰`/`📈` (5-way) | `ChartBar`/`Storefront`/`Warning`/`CurrencyDollar`/`ChartLineUp` duotone 28 | `aria-hidden` (kpi-title labels) | e2e `tests/e2e/emoji-audit.spec.ts` |
| same | 179 | `↑`/`↓`/`→` (ternary) | `TrendUp`/`TrendDown`/`ArrowRight` bold 14 | `aria-hidden` (adjacent %) | same e2e |
| `routes/analytics/+page.svelte` | 146 | `↑`/`⚠`/`🔴`/`ℹ` (ternary) | `TrendUp`/`Warning`/`WarningCircle`(red,16)/`Info` bold 14 | `aria-label="{type} insight"` | e2e `tests/e2e/emoji-audit.spec.ts` |
| same | 171 | `🔴`/`🟡` (ternary) | `WarningCircle` bold 16, host `style="color: var(--color-error\|warning)"` | ADD `aria-label="Severidad: {anomaly.severity}"` (gap-fix); SVG `aria-hidden` | same e2e |
| `routes/(dashboard)/analytics/predictive/+page.svelte` | 188 | `↑`/`↓` (ternary) | `TrendUp`/`TrendDown` bold 12 | `aria-hidden` (adjacent %) | e2e `tests/e2e/emoji-audit.spec.ts` |

**New imports required per file** (deep-import `phosphor-svelte/lib/{Name}`):
- `ContextualAssistant`: `ChartLineUp`, `Warning`, `ChartBar`, `Package`, `NotePencil`, `CurrencyDollar` (6 new)
- `A2UIRenderer`: `Clipboard` (1 new)
- `AhorremosTiempoButton`: `CaretLeft`, `Sparkle` (2 new)
- `InsightPanel`: `TrendUp`, `Warning`, `WarningCircle`, `Info` (4 new)
- `AnomalyList`: `WarningCircle` (1 new)
- `dashboard/+page.svelte`: `Warning`, `CurrencyDollar`, `ChartLineUp` (3 new; `ChartBar`, `Storefront`, `Package`, `ArrowRight`, `Sparkle` already imported)
- `analytics/+page.svelte`: `TrendUp`, `Warning`, `WarningCircle`, `Info` (4 new; none currently)
- `predictive/+page.svelte`: `TrendDown` (1 new; `TrendUp`, `Warning`, `ArrowRight` already imported)

### Docs (3)

| File | Lines | Change |
|------|-------|--------|
| `apps/README.md` | 7, 13, 14, 29, 42 | Drop `📂` `📖` `⚡` `🏛️` `🛠️` from headers + tree comments (keep text) |
| `apps/dashboard/README.md` | 7, 16, 22, 26, 31, 36, 39, 42, 43, 44, 45, 50, 85 | Drop `🚀` `📂` `🖥️` `🔐` `📊` `🤖` `📡` `📦` `🏷️` `🏪` `👥` `🛠️` `🏛️` (keep text) |
| `apps/dashboard/STATUS.md` | 3, 6, 9, 12, 15 | `✅` → ` — DONE`; leave `→` prose arrows on L10 |

### Out of scope (no change)

- `(landing)/+page.svelte` L852 `content: '✓'` (text checkmark, CSS pseudo-element).
- `ACADEMIC/Frontend.md` (apply phase only verifies the "erradicó" claim).
- Code-comment `→` arrows across `*.ts` (typographic, not pictographic).

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Vitest unit | 5 lib components render Phosphor SVG in host / correct aria-label | RED assert SVG/label → GREEN swap. Extend 4 existing test files; CREATE `AhorremosTiempoButton.test.ts`. Convention: `@testing-library/svelte` + `$lib` alias import (matches `AnomalyList.test.ts`). |
| Playwright e2e | 3 route pages render no pictographic emoji | RED `tests/e2e/emoji-audit.spec.ts` (login → visit routes → assert body text matches no emoji range) → GREEN swap. `auth.spec.ts` must keep passing. |

## Migration / Rollout

No migration required. Pure presentation tokens, no state/API. Each file is an independent revertible unit.

## Open Questions

- [ ] Trend arrows adjacent to `%` use `aria-hidden` (per instruction), which drops direction for AT users. Acceptable, or add sr-only "subió/bajó" text in a follow-up? (Flagged — not blocking.)
- [ ] `predictive/+page.svelte` uses Tailwind-style utility classes despite "no Tailwind" — unrelated to this change; noted for a future consistency change.

## Risks

| Severity | Description |
|----------|-------------|
| Low | `--color-warning` is orange `#f97316`, not yellow — 🟡 renders orange. Semantically correct for "warning"; visual change vs. current yellow dot. |
| Low | Chip icons shrink from ~17.6px (1.1rem emoji) to 14px Phosphor. Minor visual delta; intentional per size spec. |
| Medium | Trend `aria-hidden` next to `%` removes direction for screen-reader users. Mitigation: documented; candidate for sr-only follow-up. |
| Low | `analytics/+page.svelte` severity span had no `aria-label` — swap adds one (a11y improvement, but a behavior change for AT output). |
| Low | New e2e `emoji-audit.spec.ts` requires auth + running BFF; if CI lacks fixtures it may be skipped. Mitigation: reuse `auth.spec.ts` login flow. |
