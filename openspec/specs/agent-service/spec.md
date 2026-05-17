# agent-service Specification

**Status**: Active (new)
**Depends on**: None

## Purpose

Agent backend contract — Kotlin/Spring Boot service exposing A2UI v0.9 protocol endpoints, 3-tier fallback generation, and SSE streaming for BFF legacy compatibility.

## Requirements

### REQ-AGENT-01: Health Check

`GET /health` MUST return `{"status":"UP","model":"{model_id}","version":"{app_version}"}`.

| Scenario | GIVEN | WHEN | THEN |
|----------|-------|------|------|
| Healthy | Service running | GET /health | 200 + `status:"UP"` |
| Degraded | Gemini unavailable | GET /health | 200 + `status:"DEGRADED"` + `"tier1":"down"` |

### REQ-AGENT-02: A2UI v0.9 Generation

`POST /api/agent/a2ui` MUST accept `{message, context?, history[], mode?}` and return a v0.9 `createSurface` envelope.

**Response:** `{surfaceId, surface:{type:"createSurface", components[], dataBindings{}}, provenance:"gemini"|"fallback-engine"|"catalog"}`

| Scenario | GIVEN | WHEN | THEN |
|----------|-------|------|------|
| Analyst query | Valid message + mode:analyst | POST /a2ui | 200 + `createSurface` with components |
| Operator action | "ajusta stock leche 10" | POST /a2ui | 200 + surface with action confirmation |
| Empty message | message:"" | POST /a2ui | 400 + `{"code":"INVALID_MESSAGE"}` |
| Malformed body | Bad JSON | POST /a2ui | 400 + `{"code":"BAD_REQUEST"}` |

### REQ-AGENT-03: 3-Tier Fallback

The service MUST route through Gemini (Tier 1) → FallbackEngine (Tier 2) → Catalog (Tier 3).

| Tier | Mechanism | Timeout | Fallback trigger |
|------|-----------|---------|------------------|
| 1 | Google Gemini + A2UI SDK | 15s HTTP | Error or timeout |
| 2 | Template matcher (rule-based) | <2s | Tier 1 failure |
| 3 | Static catalog defaults | <500ms | Tier 2 failure |

| Scenario | GIVEN | WHEN | THEN |
|----------|-------|------|------|
| Gemini healthy | Tier 1 responds | POST /a2ui | provenance:"gemini" |
| Gemini timeout | Gemini >15s | POST /a2ui | provenance:"fallback-engine", response within 60s |
| All tiers down | 3 tiers fail | POST /a2ui | 503 + `{"code":"ALL_TIERS_DOWN"}` |

### REQ-AGENT-04: SSE Streaming

`GET /api/agent/chat/stream?message=&context=&history=` MUST return SSE for BFF.

| Event | Format | When |
|-------|--------|------|
| chunk | `{"type":"chunk","content":"...","done":false}` | Partial text |
| done | `{"type":"done","content":"...","done":true}` | Complete |
| error | `{"type":"error","code":"...","message":"..."}` | Error |
| tool | `{"type":"tool","name":"...","status":"running\|done\|error"}` | Tool execution |
| a2ui | `{"type":"a2ui","surfaceId":"...","surface":{...}}` | V0.9 surface |

| Scenario | GIVEN | WHEN | THEN |
|----------|-------|------|------|
| Full stream | Valid params | SSE connect | TTFB <2s, chunk events, done event, close |
| Agent error | Internal error | SSE streaming | error event emitted, stream closes |
| No message | GET with missing message | /chat/stream | 400 response |

### REQ-AGENT-05: Timeouts & Retry

| Parameter | Value |
|-----------|-------|
| Gemini HTTP timeout | 15s |
| Total request timeout | 60s |
| SSE stream idle timeout | 60s |
| Gemini retry | 1 attempt, 2s backoff |
| FallbackEngine retry | 0 |

### REQ-AGENT-06: Environment

| Variable | Req | Default | Purpose |
|----------|-----|---------|---------|
| `GEMINI_API_KEY` | YES | — | Gemini auth |
| `GEMINI_MODEL_ID` | YES | — | Model name |
| `GEMINI_TIMEOUT_SECS` | NO | 15 | Per-call timeout |
| `SERVER_PORT` | NO | 8000 | Spring Boot port |
| `EUREKA_SERVICE_URL` | NO | `http://localhost:8761/eureka` | Registry |
| `LOG_LEVEL` | NO | INFO | Logging |

| Scenario | GIVEN | WHEN | THEN |
|----------|-------|------|------|
| Missing API key | `GEMINI_API_KEY` unset | Startup | Fail fast with descriptive error |

### REQ-AGENT-07: A2UI v0.9 Message Types

The service MUST emit three v0.9 envelope types:

| Type | Trigger | Payload |
|------|---------|---------|
| `createSurface` | Initial request or mode switch | `{surfaceId, components[], dataBindings{}}` |
| `updateComponents` | Incremental UI change | `{surfaceId, components[], targetRef?}` |
| `updateDataModel` | Data refresh | `{surfaceId, dataBindings{}}` |

## Edge Cases

- REQ-AGENT-08: Duplicate messages (same message <2s apart) MUST be deduplicated server-side.
- REQ-AGENT-09: SSE connection limit MUST be capped at 50 concurrent connections.
- REQ-AGENT-10: All A2UI responses MUST include `surfaceId` for traceability.

## Acceptance Criteria
- [ ] GET /health returns UP + model info
- [ ] POST /api/agent/a2ui returns valid createSurface (all 3 provenances)
- [ ] GET /api/agent/chat/stream emits chunk/done/error/tool/a2ui events
- [ ] 3-tier fallback verified: Gemini up / Gemini down / all down
- [ ] Timeouts: Gemini 15s, total 60s
- [ ] Missing GEMINI_API_KEY fails startup
- [ ] Server port = 8000, Eureka app name = `siga-agent`
