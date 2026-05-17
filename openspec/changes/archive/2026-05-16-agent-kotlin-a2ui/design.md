# Design: agent-kotlin-a2ui — Arquitectura Kotlin + A2UI v0.9

## Technical Approach

Reemplazar `services/agent/` (Python/FastAPI) por un microservicio Kotlin/Spring Boot 3.x con capas Controller → Service → Engine. El protocolo A2UI pasa del formato custom `{tree, action}` a los mensajes oficiales v0.9 (`createSurface`, `updateComponents`, `updateDataModel`) usando el SDK A2UI de Google. Sin DB, sin JPA, sin hexagonal — solo lógica de orquestación con fallback 3-tier.

La app se registra en Eureka como `siga-agent`, puerto 8000. Se agrega al `settings.gradle.kts` como `services:agent`. El frontend se actualiza para entender el nuevo envelope v0.9.

## Architecture Decisions

| Opción | Alternativas | Decisión | Razón |
|--------|-------------|----------|-------|
| **Layered** (Controller→Service→Engine) | Hexagonal con ports/adapters | Layered | Sin DB ni I/O externo que justifique puertos. Capas claras para orquestación. |
| **Spring Boot WebFlux** (SSE) vs WebMVC | WebMVC | WebFlux | SSE streaming requiere non-blocking I/O. `spring-boot-starter-webflux` con `Flow<T>` para SSE. |
| **A2UI SDK de Google** v0.9 | SDK manual sin wrapper | SDK oficial + adapter | SDK provee tipos v0.9 (`A2uiSurface`, etc.). Adapter en `GeminiEngine` aísla cambios de API. |
| **SSE Legacy en GET** | Migrar a POST+WS | GET igual que antes | El BFF espera `GET /api/agent/chat/stream`. Cambiarlo rompe frontend sin beneficio. |
| **FallbackEngine sin LLM** | Mock, responses hardcodeadas | Keyword matcher + SQL Mapping | Cada respuesta debe ser real. El matcher clasifica intentos y ejecuta queries contra la DB compartida. |
| **Kotest + MockK** | JUnit 5 (usado en auth) | JUnit 5 | El resto del proyecto usa JUnit. Consistencia sobre ideal. Kotlin-specific features via MockK. |
| **settings.gradle.kts** include | Docker build standalone | `include("services:agent")` | Misma estructura que auth/inventory. Reutiliza `services:common`. |

## Data Flow

```
POST /api/agent/a2ui { prompt, context? }
       │
       ▼
  ┌──────────────┐
  │ A2UIController│
  └──────┬───────┘
         │ llama
         ▼
  ┌──────────────┐    1. intenta
  │  A2UIService  │ ──────────► ┌──────────────┐
  │  (orquesta)   │             │ GeminiEngine  │ ← SDK Google A2UI
  │  Tier 1 │ 2   │             │ generateSurface│ → createSurface + updateComponents
  │  Tier 2 │ 3   │             └──────────────┘
  │  Tier 3      │                    │ fallback
  └──────┬───────┘                    ▼
         │                     ┌──────────────┐
         │                     │FallbackEngine│ → classifies intent → SQL → A2UI components
         │                     └──────────────┘
         │                           │ fallback
         │                           ▼
         │                     ┌──────────────┐
         │                     │  Catalog     │ → suggestions as stat-card/trend-badge
         │                     └──────────────┘
         ▼
  Response: A2UIv0Message[]
```

Para SSE legacy (`GET /api/agent/chat/stream`):

```
BFF ──GET──► AgentController ──► GeminiEngine.chat() ──► SSE (chunk | done | error)
```

## File Changes

### Backend (services/agent/)

| File | Action | Description |
|------|--------|-------------|
| `services/agent/build.gradle.kts` | Create | Spring Boot 3.x + WebFlux + Eureka + A2UI SDK |
| `services/agent/Dockerfile` | Create | Multi-stage: Gradle 8.x → eclipse-temurin:21-jre |
| `services/agent/.gitignore` | Create | Gradle/Kotlin ignores |
| `services/agent/src/main/kotlin/com/siga/agent/SigaAgentApplication.kt` | Create | `@SpringBootApplication` + `@EnableDiscoveryClient` |
| `services/agent/src/main/kotlin/com/siga/agent/controller/HealthController.kt` | Create | `GET /health` |
| `services/agent/src/main/kotlin/com/siga/agent/controller/A2UIController.kt` | Create | `POST /api/agent/a2ui` |
| `services/agent/src/main/kotlin/com/siga/agent/controller/ChatController.kt` | Create | `GET /api/agent/chat/stream` (SSE) |
| `services/agent/src/main/kotlin/com/siga/agent/service/A2UIService.kt` | Create | Orquestador 3-tier |
| `services/agent/src/main/kotlin/com/siga/agent/engine/GeminiEngine.kt` | Create | Google A2UI SDK adapter |
| `services/agent/src/main/kotlin/com/siga/agent/engine/FallbackEngine.kt` | Create | Keyword matcher + SQL → A2UI components |
| `services/agent/src/main/kotlin/com/siga/agent/model/A2UIMessage.kt` | Create | Data classes para A2UI v0.9 |
| `services/agent/src/main/kotlin/com/siga/agent/config/AgentConfig.kt` | Create | Properties, beans, Eureka config |
| `services/agent/src/main/resources/application.yml` | Create | `server.port=8000`, Eureka, Gemini |

### Frontend (apps/dashboard/)

| File | Action | Description |
|------|--------|-------------|
| `src/lib/types/a2ui.ts` | Modify | Add `A2UISurface`, `A2UIComponent`, `A2UIv0Message` types |
| `src/lib/components/a2ui/catalog.ts` | Modify | Register `stat-card`, `trend-badge`, `data-table` |
| `src/lib/components/a2ui/A2UIRenderer.svelte` | Modify | Accept `{ surfaceId, components[] }` instead of flat tree |
| `src/lib/stores/a2ui.svelte.ts` | Modify | `updateTree` → `handleSurface(msg)`, add `components` + `surfaceId` state |
| `src/routes/assistant/+server.ts` | Modify | Add `POST /api/agent/a2ui` proxy |

### Infrastructure

| File | Action | Description |
|------|--------|-------------|
| `settings.gradle.kts` | Modify | Add `include("services:agent")` |
| `docker-compose.yml` | Modify | Build context to `./services/agent` with new Dockerfile |

## Interfaces / Contracts

### A2UI v0.9 Messages (Kotlin data classes)

```kotlin
// Mensajes entrantes al SDK según protocolo v0.9
data class A2UIv0Request(
    val prompt: String,
    val context: Map<String, Any>? = null
)

// Mensajes salientes v0.9
data class CreateSurface(
    val surfaceId: String,
    val components: List<A2UIComponent>,
    val layout: A2UILayout? = null
)

data class UpdateComponents(
    val surfaceId: String,
    val components: List<A2UIComponent>,
    val mode: UpdateMode = UpdateMode.REPLACE  // REPLACE | APPEND | PATCH
)

data class UpdateDataModel(
    val surfaceId: String,
    val data: Map<String, Any>
)

enum class UpdateMode { REPLACE, APPEND, PATCH }

// Componentes del catálogo extendido
data class A2UIComponent(
    val type: String,           // "stat-card" | "trend-badge" | "data-table" | ...
    val props: Map<String, Any>? = null,
    val children: List<A2UIComponent>? = null,
    val nodeId: String? = null
)
```

### Frontend types (TypeScript additions)

```typescript
// a2ui.ts additions for v0.9
export interface A2UISurface {
    surfaceId: string;
    components: A2UIComponent[];
    layout?: A2UILayout;
}

export interface A2UIComponent {
    type: string;
    props?: Record<string, unknown>;
    children?: A2UIComponent[];
    nodeId?: string;
}

// Union of v0.9 messages
export type A2UIv0Message =
    | { type: 'createSurface'; surfaceId: string; components: A2UIComponent[]; layout?: A2UILayout }
    | { type: 'updateComponents'; surfaceId: string; components: A2UIComponent[]; mode: 'replace' | 'append' | 'patch' }
    | { type: 'updateDataModel'; surfaceId: string; data: Record<string, unknown> };
```

### SSE Legacy Contract (unchanged)

```
data: {"type":"chunk","content":"texto","done":false}
data: {"type":"done","content":"","done":true}
data: {"type":"error","code":"GEMINI_ERROR","message":"..."}
```

## Human-in-the-Loop (HiTL) para Mutaciones

### Principio
Ninguna operación de escritura (INSERT/UPDATE/DELETE) se ejecuta sin confirmación explícita del usuario. Tanto Gemini (Tier 1) como FallbackEngine (Tier 2) siguen el mismo patrón de confirmación.

### Flujo HiTL

```
Usuario: "añade 10 laptops al local Centro"
  ↓
Agente clasifica: INTENT = "inventario:add_stock"
  ↓
[Fase 1] Tree A2UI de CONFIRMACIÓN:
  ┌──────────────────────────────────┐
  │  ¿Confirmar operación?           │
  │                                  │
  │  Producto:  Laptop Gamer XZ      │
  │  Cantidad:  +10                  │
  │  Local:     Centro               │
  │                                  │
  │  [✅ Sí, ejecutar] [❌ Cancelar]  │
  └──────────────────────────────────┘
  ↓
Usuario hace clic en "Sí, ejecutar"
  ↓
[Fase 2] Agente ejecuta la mutación
  ↓
Tree A2UI de RESULTADO:
  ┌──────────────────────────────────┐
  │  ✅ Operación exitosa            │
  │                                  │
  │  Stock actual: 47 unidades       │
  │  Local: Centro                   │
  │  Timestamp: 16/05 18:30          │
  │  Operador: admin@tenant          │
  │  ID: op-abc123                   │
  └──────────────────────────────────┘
```

### Implementación en FallbackEngine

El `FallbackEngine` mantiene un **mapa de intenciones** que incluye tanto reads como writes:

```kotlin
enum class IntentType { READ, WRITE }

data class IntentMapping(
    val pattern: Regex,
    val intentType: IntentType,
    val sqlTemplate: String,      // con placeholders tipo ?
    val params: List<String>,     // nombres de parámetros extraídos
    val confirmTitle: String,     // título del tree de confirmación
    val resultTitle: String       // título del tree de resultado
)

val intentMap = listOf(
    // Reads
    IntentMapping("stock de (.+)", READ, "SELECT ...", listOf("producto"), "", ""),
    IntentMapping("ventas del (.+)", READ, "SELECT ...", listOf("periodo"), "", ""),
    // Writes
    IntentMapping("añade? (\\d+) (.+) al local (.+)", WRITE,
        "UPDATE inventory SET quantity = quantity + ? WHERE ...",
        listOf("cantidad", "producto", "local"),
        "¿Agregar stock?",
        "Stock actualizado"
    ),
)
```

### Logging Obligatorio

Toda operación ejecutada (confirmada por HiTL) DEBE registrarse:

| Campo | Descripción |
|-------|-------------|
| `operation_id` | UUID generado |
| `tenant_id` | Del header X-Tenant-Id |
| `user_id` | Del contexto de autenticación |
| `intent` | Intención clasificada (ej: "inventario:add_stock") |
| `params` | Parámetros de la operación en JSON |
| `status` | SUCCESS / FAILED |
| `tier` | gemini / fallback-engine |
| `timestamp` | Timestamp de la operación |
| `ip_address` | Dirección del cliente |

**Almacenamiento:** tabla `agent.operation_log` en PostgreSQL (misma DB compartida).

### Consideraciones de Seguridad

1. **Prepared statements siempre** — nunca concatenar strings SQL
2. **Whitelist de operaciones** — solo las intenciones mapeadas explícitamente son ejecutables
3. **Timeout por confirmación** — el tree de confirmación expira a los 120s; después se cancela automáticamente
4. **Rate limiting** — máximo 10 operaciones por minuto por tenant
5. **Rollback implícito** — cada operación se ejecuta en una transacción; si algo falla, se revierte

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | `A2UIService` orchestration logic | Mock GeminiEngine + FallbackEngine, verify 3-tier fallback. JUnit 5 + MockK. |
| Unit | `FallbackEngine.intentClassifier` | Test keyword/regex matching: "stock", "ventas", "kpi", unknown. Verify correct SQL mapping. |
| Unit | `GeminiEngine.generateSurface` | Mock A2UI SDK, verify adapter transforms Google types to our data classes. |
| Controller | `A2UIController` HTTP contract | WebTestClient. Verify 200 with valid A2UI v0.9 response, 400 on empty prompt, 502 on Gemini failure. |
| Controller | `ChatController` SSE | WebTestClient + `exchange().expectBody()`. Verify SSE event format matches legacy contract. |
| Integration | E2E A2UI flow | Full stack test: POST to controller → verify correct message types in response. |

## Migration / Rollout

1. **Frontend first**: Commit catalog additions + type changes + store updates. Frontend sigue funcionando con backend Python (additive). ✅
2. **Backend build**: Commit Kotlin service, Dockerfile, settings.gradle.kts update. ✅
3. **Docker compose**: Update build context for `siga-agent` to point to new Dockerfile. ✅
4. **Validate**: Smoke test health + A2UI + SSE endpoints. Rename old Python files to `.bak`. ✅
5. **Rollback**: Revert Docker compose context → rebuild Python image. Frontend changes are additive.

## Open Questions

- [x] ¿El SDK A2UI de Google es accesible vía Maven Central o hay que agregar un repo custom? → **Pendiente de verificar durante T-AGENT-07 (build.gradle.kts)**
- [x] ¿El `FallbackEngine` necesita conexión directa a PostgreSQL o recibe datos vía otro servicio? → **PostgreSQL JDBC directo, read+write con HiTL**
- [x] Confirmar si `stat-card`, `trend-badge` y `data-table` se implementan como componentes Svelte nuevos o ya existen en el design system con otro nombre. → **No existen, se crean desde cero en T-AGENT-01**
