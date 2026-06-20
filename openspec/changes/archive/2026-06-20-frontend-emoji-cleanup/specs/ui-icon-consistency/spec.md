# Delta for ui-icon-consistency

## ADDED Requirements

### Requirement: No pictographic emojis in UI

Frontend UI MUST render Phosphor Svelte icons for pictographic indicators; `apps/dashboard/src/routes/**` MUST NOT render pictographic emoji characters (U+1F000–U+1FAFF, U+1F300–U+1F5FF, U+1F600–U+1F64F, U+1F680–U+1F6FF, U+1F900–U+1F9FF) in the DOM.

#### Scenario: Eight components render Phosphor

- GIVEN 8 components render: ContextualAssistant, dashboard/+page, analytics/+page, predictive/+page, AnomalyList, InsightPanel, A2UIRenderer (Clipboard 40px), AhorremosTiempoButton (CaretLeft/Sparkle 16px)
- WHEN the DOM is inspected
- THEN each indicator renders a Phosphor SVG inside its existing host
- AND no pictographic emoji character appears in `document.body.textContent`.

#### Scenario: Strict-TDD sequence per component

- GIVEN a test asserts Phosphor SVG presence or aria-label
- WHEN run before the swap
- THEN it fails (RED); after, passes (GREEN).

#### Scenario: Rollback restores prior state

- GIVEN the change is applied
- WHEN the commit is reverted
- THEN emojis reappear in 8 components and 3 docs
- AND pre-change tests are restored.

### Requirement: Severity rendering

Critical/high indicators MUST use `WarningCircle` with host `color: var(--color-error)`; warning/medium MUST use `WarningCircle` with `color: var(--color-warning)`. Host MUST carry `aria-label="{severity} severity"`; SVG MUST be `aria-hidden="true"`.

#### Scenario: Severity uses colored WarningCircle

- GIVEN AnomalyList and analytics/+page render critical/high and warning/medium anomalies
- WHEN severity indicators are inspected
- THEN each renders `WarningCircle` (bold, 16px) with host `color: var(--color-error)` or `var(--color-warning)`, host `aria-label`, SVG `aria-hidden="true"`.

### Requirement: Trend rendering

Up/down/neutral trends MUST use `TrendUp`/`TrendDown`/`ArrowRight`. When an adjacent numeric value conveys direction (e.g. "8%"), the icon MUST be `aria-hidden="true"`; otherwise it MUST carry an `aria-label`.

#### Scenario: Trends render direction icons with conditional a11y

- GIVEN dashboard/+page, analytics/+page, predictive/+page render trend values
- WHEN a trend icon sits adjacent to a `%` value
- THEN it renders TrendUp/TrendDown/ArrowRight (bold, 12–14px) with `aria-hidden="true"`; else it carries an `aria-label`.

### Requirement: Chip-icon host pattern

The existing `<span class="chip-icon">` MUST remain the styled host; the Phosphor component MUST be its child, preserving CSS without restyling.

#### Scenario: ContextualAssistant chips preserve chip-icon host

- GIVEN ContextualAssistant renders 6 suggestion chips
- WHEN each chip icon is inspected
- THEN `<span class="chip-icon">` hosts a Phosphor SVG: ChartLineUp/Warning/ChartBar/Package/NotePencil/CurrencyDollar (bold, 14px), unchanged CSS.

### Requirement: Icon weights and decorativeness

Feature-card icons MUST use `weight="duotone"`; inline chip/badge indicators MUST use `weight="bold"`. Decorative icons (adjacent `<h3>`, `<p class="kpi-title">`, or text labels the feature) MUST be `aria-hidden="true"`.

#### Scenario: Feature cards duotone, chips/badges bold

- GIVEN dashboard/+page KPI cards and InsightPanel render icons
- WHEN the icons are inspected
- THEN KPI card icons use `weight="duotone"` (28px) with `aria-hidden="true"` (kpi-title labels)
- AND InsightPanel icons use `weight="bold"` (14px) with `aria-label="{type} insight"`.

### Requirement: Frontend docs emoji-free

`apps/README.md`, `apps/dashboard/README.md`, and `apps/dashboard/STATUS.md` MUST contain zero pictographic emoji characters; markdown structure MUST be preserved; `STATUS.md` markers MUST use `- [x]` or `DONE` instead of ✅.

#### Scenario: Three docs scanned, structure preserved

- GIVEN the 3 frontend docs are updated
- WHEN each file is scanned for pictographic emoji characters
- THEN zero matches are found, headings/tree diagrams/code blocks stay unchanged, and STATUS.md markers use ` — DONE` or `- [x]`.

### Requirement: Frontend.md claim verifiable

After the change, ACADEMIC/Frontend.md's claim "se erradicó por completo el uso de emojis" MUST be verifiable by an audit scanning `apps/dashboard/src/routes/**` for pictographic emoji characters.

#### Scenario: Route audit confirms zero pictographic emojis

- GIVEN the change is applied
- WHEN an audit scans `apps/dashboard/src/routes/**` for pictographic emoji ranges
- THEN zero matches are found and the Frontend.md claim is verifiable as true.

### Requirement: Accepted out-of-scope

`(landing)/+page.svelte` CSS `content: '✓'` (U+2713, pseudo-element) MUST remain unchanged — pseudo-elements cannot host components and ✓ is not pictographic. Code-comment arrows (→ ← ↔ ↑ ↓) and `ACADEMIC/` beyond `Frontend.md` and `stitch_ui/` MUST remain unchanged.

#### Scenario: CSS checkmark and code-comment arrows unchanged

- GIVEN the change is applied
- WHEN `(landing)/+page.svelte` and `*.ts` comments are inspected
- THEN `content: '✓'` and code-comment arrows remain unchanged, and ACADEMIC/ beyond Frontend.md and stitch_ui/ are untouched.
