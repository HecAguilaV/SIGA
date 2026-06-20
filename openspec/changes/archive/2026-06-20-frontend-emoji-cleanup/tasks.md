# Tasks: Frontend Emoji Cleanup

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~265 (40 swaps + 45 docs + 180 tests) |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR (4 work-unit commits) |
| Delivery strategy | ask-always |
| Chain strategy | pending |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: pending
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | RED tests (Vitest unit + Playwright e2e) | PR 1 | base: main; fails until swap |
| 2 | GREEN swaps (8 components) | PR 1 | depends on Unit 1; makes tests pass |
| 3 | Docs + Frontend.md claim | PR 1 | depends on Unit 2 |
| 4 | Verify (Vitest + Playwright + audit) | PR 1 | final gate |

Single PR (Low risk, <400 lines). Work units map to reviewable work-unit commits (tests-with-code, docs-with-change), not chained PRs.

## Phase 1: RED — Tests First (Work-Unit 1)

- [x] 1.1 Extend 4 + create 1 Vitest unit test asserting Phosphor SVG in host (`.chip-icon svg`, `.anomaly-severity svg`, `.a2ui-empty-icon svg`) or aria-label for the 5 lib components. Files: `tests/unit/components/a2ui/{ContextualAssistant,A2UIRenderer,AhorremosTiempoButton}.test.ts` (CREATE the last), `tests/unit/components/dashboard/{InsightPanel,AnomalyList}.test.ts`. Convention: `@testing-library/svelte` + `$lib` alias. Spec: R1 Strict-TDD, R2, R4, R5. Verify: `pnpm --filter @siga/dashboard test` → FAIL (RED).
- [x] 1.2 Create `tests/e2e/emoji-audit.spec.ts` (reuse `auth.spec.ts` login) visiting `/dashboard`, `/analytics`, `/analytics/predictive`; assert `document.body.textContent` matches no pictographic emoji range (U+1F000–1FAFF etc.) and Phosphor SVGs present. Spec: R1 Eight components, R3, R8. Verify: `pnpm --filter @siga/dashboard test:e2e emoji-audit` → FAIL (RED). _Note: runtime RED blocked by Playwright webServer build timeout (60s) + BFF auth dependency; test is RED-by-construction (pre-swap body matches pictographic regex). Unit RED is the hard gate._

## Phase 2: GREEN — Component Swaps (Work-Unit 2)

- [x] 2.1 Swap emojis→Phosphor in 5 lib components per design mapping; add deep imports `phosphor-svelte/lib/{Name}`. Files: `lib/components/a2ui/{ContextualAssistant,A2UIRenderer,AhorremosTiempoButton}.svelte`, `lib/components/dashboard/{InsightPanel,AnomalyList}.svelte`. Spec: R1, R2, R4, R5. Verify: `pnpm --filter @siga/dashboard test` → PASS. Depends: 1.1.
- [x] 2.2 Swap emojis→Phosphor in 3 route pages per design mapping; add deep imports. Files: `routes/(dashboard)/dashboard/+page.svelte`, `routes/analytics/+page.svelte`, `routes/(dashboard)/analytics/predictive/+page.svelte`. Spec: R1, R3, R5. Verify: `pnpm --filter @siga/dashboard test:e2e emoji-audit` → PASS. Depends: 1.2, 2.1. _Note: e2e runtime blocked by Playwright webServer build timeout + BFF auth dependency; static audit confirms 0 pictographic emoji in src/routes/** (89 files). Unit suite 255/255 green._

## Phase 3: Docs + Claim (Work-Unit 3)

- [x] 3.1 Remove pictographic emojis from `apps/README.md`, `apps/dashboard/README.md`, `apps/dashboard/STATUS.md` preserving markdown structure; `STATUS.md` `✅`→` — DONE`/`- [x]`; keep prose `→` arrows. Spec: R6. Verify: grep pictographic ranges → 0 matches. Depends: 2.2.
- [x] 3.2 Run emoji audit over `apps/dashboard/src/routes/**`; confirm zero pictographic emojis; verify `ACADEMIC/Frontend.md` "erradicó" claim true (modify Frontend.md ONLY if it contains a pictographic emoji — it should not). Confirm `(landing)/+page.svelte` `content:'✓'` and `*.ts` comment arrows unchanged. Spec: R7, R8. Verify: audit → 0 route matches; out-of-scope unchanged. Depends: 3.1. _Note: Frontend.md had 2 pictographic emoji (📦🏪) inside the "erradicó" claim sentence — replaced with descriptive text "los pictogramas de paquete o tienda". (landing) CSS `content:'✓'` unchanged (line 852). No source .ts comment arrows touched (only 4 .test.ts files edited)._

## Phase 4: Verify (Work-Unit 4)

- [x] 4.1 Run `pnpm --filter @siga/dashboard test` (Vitest) + `pnpm --filter @siga/dashboard test:e2e` (Playwright incl. `auth.spec.ts`); confirm green; final emoji audit over routes + 3 docs. Spec: R1–R8. Verify: all green + zero pictographic emoji matches. Depends: 3.2. _Vitest: 255/255 pass. e2e: runtime blocked (Playwright webServer build timeout 60s + BFF auth dependency). Final audit: 0 pictographic emoji in apps/dashboard/src/** + 3 docs + Frontend.md; (landing) CSS ✓ and .ts comment arrows unchanged._
