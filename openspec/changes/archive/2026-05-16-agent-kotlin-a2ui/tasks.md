# Tasks: agent-kotlin-a2ui

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 1050–1200 |
| 400-line budget risk | High |
| Chained PRs recommended | No |
| Suggested split | Single batch (direct commits per user) |
| Delivery strategy | exception-ok |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: High

### Suggested Work Units

| Unit | Tasks | Files | Est. Lines |
|------|-------|-------|------------|
| 1 — Frontend v0.9 | T-01..T-05 | 6 files | ~400 |
| 2 — Backend setup | T-06..T-09 | 5 files | ~105 |
| 3 — Core engines | T-10..T-13 | 4 files | ~280 |
| 4 — Controllers | T-14..T-15 | 2 files | ~110 |
| 5 — Infra + cleanup | T-16..T-20 | 7 files | ~55 |
| 6 — Docs | T-21..T-22 | 4 files | ~80 |

## Phase 1: Frontend Additive (backward-compatible)

- [ ] **T-AGENT-01**: Create 3 Svelte 5 components in `apps/dashboard/src/lib/components/ui/`: `StatCard.svelte` (label/value/delta props), `TrendBadge.svelte` (up/down/flat trend), `DataTable.svelte` (columns+rows, sortable). AC: render from A2UI component props with no regressions.
- [ ] **T-AGENT-02**: Register `stat-card`, `trend-badge`, `data-table` in `apps/dashboard/src/lib/components/a2ui/catalog.ts`. AC: `getComponent('stat-card')` returns StatCard. Dep: T-01.
- [ ] **T-AGENT-03**: Add `A2UISurface`, `A2UIComponent` (type/props/children/ref), `A2UIv0Message` (createSurface|updateComponents|updateDataModel) types to `apps/dashboard/src/lib/types/a2ui.ts`. AC: TypeScript compiles; backward compat with legacy `A2UINode`/`A2UIEvent`.
- [ ] **T-AGENT-04**: Refactor `apps/dashboard/src/lib/stores/a2ui.svelte.ts` — add `components[]`, `surfaceId`, `dataBindings{}` state; add `handleSurface(msg: A2UIv0Message)` for v0.9; keep `patchNode`/`patchChildren` for legacy. AC: v0.9 messages update components by `ref`; legacy SSE still patches tree. Dep: T-03.
- [ ] **T-AGENT-05**: Update `apps/dashboard/src/lib/components/a2ui/A2UIRenderer.svelte` to accept `{ surfaceId: string, components: A2UIComponent[] }` envelope; render flat list via `A2UINodeRenderer`; show "No hay contenido" on empty `[]`. AC: renders stat-card, trend-badge, data-table from v0.9 response. Dep: T-02, T-04.
- [ ] **T-AGENT-06**: Add `POST /api/agent/a2ui` proxy to `apps/dashboard/src/routes/assistant/+server.ts` — forward `{message, context?, history[], mode?}` to agent, return A2UI response. AC: `POST /api/agent/a2ui` returns 200 + valid A2UI envelope. Dep: none.

## Phase 2: Backend Foundation

- [x] **T-AGENT-07**: Create `services/agent/build.gradle.kts` — Spring Boot 4.0.6, WebFlux, Eureka client, A2UI SDK, PostgreSQL JDBC, JUnit 5 + MockK. AC: `./gradlew :services:agent:build` compiles. Dep: none.
- [x] **T-AGENT-08**: Create `services/agent/src/main/resources/application.yml` (port 8000, Eureka `siga-agent`, Gemini props) + `config/AgentConfig.kt` (properties class, `@Configuration` beans for RestClient, ObjectMapper). AC: service starts on 8000, registers in Eureka. Dep: T-07.
- [x] **T-AGENT-09**: Create `SigaAgentApplication.kt` in `com.siga.agent` — `@SpringBootApplication` + `@EnableDiscoveryClient`. AC: `GET /health` not found (T-11 adds it), but app boots without error. Dep: T-07, T-08.

## Phase 3: Backend Core Logic

- [x] **T-AGENT-10**: Create `model/A2UIMessage.kt` in `com.siga.agent` — data classes: `A2UIv0Request`, `CreateSurface`, `UpdateComponents`, `UpdateDataModel`, `A2UIComponent`, `UpdateMode` enum (REPLACE/APPEND/PATCH). AC: serialized to/from JSON matching v0.9 spec. Dep: none.
- [x] **T-AGENT-11**: Create `HealthController.kt` — `GET /health` returns `{"status":"UP","model":"...","version":"1.0.0"}` on healthy, `DEGRADED` when Gemini unreachable. AC: 200 + valid JSON. Dep: T-09.
- [x] **T-AGENT-12**: Create `engine/GeminiEngine.kt` — wraps Google A2UI Kotlin SDK; `generateSurface(prompt, context) -> CreateSurface`; 15s HTTP timeout, 1 retry 2s backoff; adapter isolates SDK types. AC: returns valid `CreateSurface` with components array. Dep: T-10.
- [x] **T-AGENT-13**: Create `engine/FallbackEngine.kt` — keyword/regex intent classifier con soporte READ + WRITE + HiTL:
  - **Reads**: "stock de X", "ventas del periodo", "kpi" → PostgreSQL JDBC queries → `A2UIComponent[]` (stat-card, data-table)
  - **Writes con HiTL**: "añade N X al local Y", "registra venta de X" → Fase 1: tree A2UI de **confirmación** con botones Confirmar/Cancelar. Fase 2: si usuario confirma → ejecuta INSERT/UPDATE + tree de **resultado**
  - **Logging obligatorio**: toda operación WRITE ejecutada se persiste en `agent.operation_log` (operation_id, tenant_id, user_id, intent, params, status, tier, timestamp)
  - **Seguridad**: prepared statements, whitelist de operaciones, timeout de confirmación 120s, rate limiting 10ops/min/tenant
  - **Catalog fallback**: intenciones no reconocidas devuelven tree con opciones disponibles
  - AC: "stock" → stat-card con data. "añade 10 laptops al local Centro" → confirmación → resultado. AC: intención desconocida → catálogo de ayuda. Dep: T-10.
- [x] **T-AGENT-14**: Create `service/A2UIService.kt` — orchestrator: try Gemini → catch → FallbackEngine → catch → Catalog defaults; 60s total timeout; deduplicates identical messages <2s apart. AC: provenance field indicates which tier served. Dep: T-12, T-13.
- [x] **T-AGENT-15**: Create `controller/A2UIController.kt` — `POST /api/agent/a2ui` accepts `{message, context?, history[], mode?}`, returns `{surfaceId, surface: CreateSurface, provenance}`; 400 on empty message, 502 on Gemini error, 503 on all tiers down. AC: all 3 provenances verified. Dep: T-14.
- [x] **T-AGENT-16**: Create `controller/ChatController.kt` — `GET /api/agent/chat/stream?message=&context=&history=` returns SSE; events: chunk/done/error/tool/a2ui; max 50 concurrent connections; 60s idle timeout. AC: SSE matches legacy contract. Dep: T-12.

## Phase 4: Infrastructure & Cleanup

- [x] **T-AGENT-17**: Create `services/agent/Dockerfile` — multi-stage: Gradle 8.x + JDK 21 build → eclipse-temurin:21-jre runtime; `ENTRYPOINT ["java","-jar","/app/agent.jar"]`. Create `.gitignore` — `build/`, `.gradle/`, `*.jar`, `!gradle/wrapper/gradle-wrapper.jar`. AC: `docker build -t siga-agent ./services/agent` succeeds. Dep: T-07. *(Existing .gitignore already present)*
- [x] **T-AGENT-18**: Update `settings.gradle.kts` — add `include("services:agent")`. AC: `./gradlew projects` lists agent. Dep: T-07. *(Done early as compilation dependency for this batch)*
- [x] **T-AGENT-19**: Update `docker-compose.yml` — build context `./services/agent`, env: `GEMINI_API_KEY`, `GEMINI_MODEL_ID`, `SERVER_PORT=8000`, `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`. AC: `docker compose up siga-agent` starts on 8000. Dep: T-17.
- [x] **T-AGENT-20**: Delete Python files: `app/main.py`, `app/core/gemini.py`, `app/core/database.py`, `requirements.txt`, `app/__init__.py`, `app/core/__init__.py`, `app/api/__init__.py`. Rename old `Dockerfile` → `Dockerfile.python.bak`. AC: no `.py` files remain in `services/agent/`. Dep: T-19.

## Phase 5: Documentation

- [x] **T-AGENT-21**: Update `services/agent/README.md` and `README.en.md` — Kotlin/Spring Boot stack, `./gradlew bootRun` instructions, new endpoint table (health, A2UI v0.9, SSE), env vars table. AC: README reflects Kotlin not Python. Dep: T-16.
- [x] **T-AGENT-22**: Update `openspec/changes/active/migracion-microservicios/design.md` — change `siga-agente` from Python/FastAPI to Kotlin/Spring Boot. Update `openspec/core/STATUS.md` — change agent stack from Python to Kotlin. AC: both docs reference Kotlin for agent. Dep: none.
