# Delta for ui-a2ui

**Change**: agent-kotlin-a2ui — custom `{tree, action}` → A2UI v0.9 protocol

## ADDED Requirements

### REQ-A2UI-23: Surface + Components State

The a2ui store MUST hold `surfaceId`, `components[]`, and `dataBindings{}` instead of a single `tree`. Each component SHALL have `ref` (stable identifier) for targeted updates.

#### Scenario: Agent renders dashboard via createSurface
- GIVEN the agent responds with `{"type":"createSurface","surfaceId":"s1","components":[{"type":"chart","ref":"c1","props":{...}},{"type":"stat-card","ref":"sc1","props":{...}}],"dataBindings":{"c1":"/api/sales"}}`
- WHEN the SSE event arrives
- THEN the store SHALL set `surfaceId="s1"`, `components=[...]`, `dataBindings={...}`
- AND the renderer SHALL display chart + stat-card

### REQ-A2UI-24: Component Reference (child) Pattern

Components SHALL use `ref` (not `nodeId`) for identity. Children SHALL be expressed as `children[]` with `ref` per child, not as nested `A2UINode` trees.

#### Scenario: Container with referenced children
- GIVEN a container component with `ref:"main"` and `children:[{ref:"grid1",type:"container",...}]`
- WHEN the renderer processes it
- THEN each child SHALL be addressable by `ref` for future `updateComponents` targeting

### REQ-A2UI-25: Data Binding

Components MAY declare data bindings: `{ref:"chart-1", bind:"/api/sales"}`. The store SHALL resolve bindings from `dataBindings{}` and pass resolved data as props.

#### Scenario: Data binding resolution
- GIVEN `dataBindings:{ "chart-1":"/api/sales" }` and component `{ref:"chart-1", bind:"chart-1"}`
- WHEN the renderer mounts the component
- THEN it SHALL fetch `/api/sales` and pass result as `data` prop

### REQ-A2UI-26: New Catalog Components

The catalog MUST add three components: `stat-card`, `trend-badge`, `data-table`.

| Component | Svelte file | Purpose |
|-----------|-------------|---------|
| stat-card | `$lib/components/ui/StatCard.svelte` | KPI with value, label, delta |
| trend-badge | `$lib/components/ui/TrendBadge.svelte` | Trend indicator (up/down/flat) |
| data-table | `$lib/components/ui/DataTable.svelte` | Tabular data with sort |

#### Scenario: Render stat-card from agent response
- GIVEN the agent returns a component with `type:"stat-card", props:{label:"Ventas",value:"$12.4K",delta:8.2}`
- WHEN the renderer resolves the component
- THEN it SHALL render StatCard.svelte with those props

## MODIFIED Requirements

### REQ-A2UI-14: A2UIRenderer

El sistema DEBE implementar un renderizador `A2UIRenderer.svelte` que acepte un envelope A2UI v0.9 con `surfaceId` + `components[]` y mapee cada componente (`type`, `props`, `ref`) a componentes nativos del catálogo.
(Previously: Mapeaba payloads JSON {type, props, children} como árbol único)

#### Scenario: Render v0.9 components array
- GIVEN the store has `components:[{type:"chart",ref:"c1"},{type:"stat-card",ref:"sc1"}]`
- WHEN A2UIRenderer processes the array
- THEN it SHALL render each component in order as a flat list or wrapped in a surface container
- AND each component SHALL receive its `ref` as a data attribute

#### Scenario: Empty components array
- GIVEN `components:[]`
- WHEN A2UIRenderer renders
- THEN it SHALL show the empty state ("No hay contenido disponible")

### REQ-A2UI-15: Catalog

El sistema DEBE mantener un catálogo de componentes registrados (Card, Button, Input, CrudTable, ChartWrapper, InsightPanel, AnomalyList, Badge, Modal, Spinner, Skeleton, SearchBar, CrudForm, Container, StatCard, TrendBadge, DataTable).
(Previously: 14 componentes sin stat-card, trend-badge, data-table)

*Scenarios unchanged from original spec, plus REQ-A2UI-26 above.*

### REQ-A2UI-16: SSE Events

El SSE DEBE transportar eventos de protocolo A2UI v0.9 (`createSurface`, `updateComponents`, `updateDataModel`) dentro del tipo `a2ui`, además de `chunk`/`done`/`error`/`tool`.
(Previously: Transportaba evento a2ui con {tree, action} custom)

#### Scenario: Surface arrives via SSE during stream
- GIVEN a chat stream in progress
- WHEN the agent emits `{"type":"a2ui","surfaceId":"s1","surface":{"type":"createSurface","components":[...]}}`
- THEN the SSE parser SHALL route it to the a2ui store
- AND the store SHALL update `components` and trigger re-render

#### Scenario: Backward compat with update/patch
- GIVEN an older agent emits `{"type":"update","nodeId":"n1","props":{}}`
- WHEN the frontend receives it
- THEN it SHALL still process it as a targeted prop update via `patchNode` (backward compat)

### SSE Event Table (Replaces main spec table)

| Event | Formato | Descripción |
|-------|---------|-------------|
| chunk | `data: {"type":"chunk","content":"texto","done":false}\n\n` | Fragmento de respuesta |
| done | `data: {"type":"done","content":"texto completo","done":true}\n\n` | Fin de la respuesta |
| error | `data: {"type":"error","code":"...","message":"..."}\n\n` | Error del agente |
| tool | `data: {"type":"tool","name":"...","status":"running\|done\|error"}\n\n` | Notificación de tool |
| a2ui | `data: {"type":"a2ui","surfaceId":"...","surface":{"type":"createSurface\|updateComponents\|updateDataModel","components":[...]}}\n\n` | Payload A2UI v0.9 |

### REQ-A2UI-19: Incremental Updates

El A2UIRenderer DEBE soportar actualizaciones incrementales vía `updateComponents` (target por `ref` dentro de `components[]`) y reemplazo completo vía `createSurface`.
(Previously: Soportaba update/patch por nodeId y replace de árbol)

#### Scenario: Targeted component update via ref
- GIVEN components:[{ref:"c1",type:"chart",...},{ref:"sc1",type:"stat-card",...}]
- WHEN the agent emits `{"type":"a2ui","surfaceId":"s1","surface":{"type":"updateComponents","components":[{"ref":"sc1","props":{"value":"$15K"}}]}}`
- THEN the store SHALL merge new props into the stat-card component by `ref`
- AND the chart component SHALL remain unchanged

## Edge Cases

- REQ-A2UI-27: If `updateComponents` references a `ref` not in current `components[]`, the system SHOULD ignore it silently and log a warning.
- REQ-A2UI-28: Backward compat: SSE events with `update`/`patch` types (legacy format) MUST still work via the existing `patchNode`/`patchChildren` paths.
