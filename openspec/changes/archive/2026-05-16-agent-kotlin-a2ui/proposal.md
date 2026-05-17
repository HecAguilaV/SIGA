# Proposal: agent-kotlin-a2ui

## Intent

Refactor `services/agent/` from Python/FastAPI to Kotlin/Spring Boot using Google A2UI Kotlin SDK and A2UI v0.9 protocol. Updates both agent and frontend for the official protocol format.

## Scope

### In Scope
- Replace `services/agent/` Python → Kotlin/Spring Boot
- A2UI v0.9: `createSurface`, `updateComponents`, `updateDataModel`
- 3-tier fallback: Gemini → FallbackEngine → Catalog
- Add `stat-card`, `trend-badge`, `data-table` to `catalog.ts`
- Update `A2UIRenderer` + `a2ui.svelte.ts` for v0.9
- Update BFF `+server.ts` (POST proxy, SSE compat)
- Update `docker-compose.yml` build context
- Keep port **8000** + Eureka (`siga-agent`)

### Out of Scope
- DB/Flyway/hexagonal, mobile, auth, real DB conn

## Capabilities

### New
- `agent-service`: Agent API contract — health, A2UI v0.9 generation, SSE streaming

### Modified
- `ui-a2ui`: Protocol changes from custom `{tree, action}` to v0.9. New component types in catalog.

## Approach

Layered (Controller → Service → Client):
1. **Controller**: `POST /api/agent/a2ui`, `GET /chat/stream`, `GET /health`
2. **Service**: Routes intent: Tier 1 (Gemini) → Tier 2 (FallbackEngine) → Tier 3 (Catalog)
3. **Client**: Google A2UI Kotlin SDK → v0.9 `A2uiSurface`
4. **Frontend**: `{ surface, components }` state. Renderer unwraps v0.9 messages.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/agent/` | Replaced | Python → Kotlin/Spring Boot |
| `apps/dashboard/src/lib/types/a2ui.ts` | Modified | Add v0.9 envelope types |
| `apps/dashboard/src/lib/components/a2ui/catalog.ts` | Modified | Add 3 component types |
| `apps/dashboard/src/lib/components/a2ui/A2UIRenderer.svelte` | Modified | Handle v0.9 messages |
| `apps/dashboard/src/lib/stores/a2ui.svelte.ts` | Modified | Surface + components state |
| `apps/dashboard/src/routes/assistant/+server.ts` | Modified | Add POST proxy |
| `docker-compose.yml` | Modified | Kotlin Dockerfile build |
| `openspec/specs/ui-a2ui/spec.md` | Modified | Protocol delta for v0.9 |

## Risks

| Risk | Mitigation |
|------|------------|
| Frontend missing components at deploy | Ship catalog additions first (separate commit) |
| SSE backward compat break | Keep same path + event schema |
| A2UI SDK API mismatch | Pin version, wrap in adapter |
| Port conflict | `server.port=8000` in Spring Boot |

## Rollback Plan

1. Rename old Python files (`.bak`) until verified
2. Frontend additions are additive — revert easily
3. Git revert if smoke tests fail

## Dependencies

- Google A2UI Kotlin SDK (Maven Central)
- Spring Boot 3.x + WebFlux (SSE)
- Spring Cloud Netflix Eureka client

## Success Criteria

- [ ] `GET /health` returns UP with model info
- [ ] `POST /api/agent/a2ui` returns valid A2UI v0.9 `createSurface`
- [ ] `GET /api/agent/chat/stream` returns SSE chunk/done events
- [ ] FallbackEngine returns A2UI tree when Gemini unavailable
- [ ] Frontend renders stat-card, trend-badge, data-table
- [ ] Dual-mode (classic ↔ agentive) works with v0.9 protocol
