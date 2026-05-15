# Spec: ui-a2ui

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: ui-bff, ui-auth-flow, ui-theme

## Requirements

### Functional

- REQ-A2UI-01: El sistema DEBE exponer un endpoint `+server.ts` `GET /api/chat/stream` que se conecta a `siga-agent` SSE y retorna un `ReadableStream`.
- REQ-A2UI-02: El sistema DEBE proporcionar un componente `ContextualAssistant` flotante (FAB) que se despliega como chat widget en cualquier ruta.
- REQ-A2UI-03: El `ContextualAssistant` DEBE tener dos modos: **Analyst** (consulta datos del tenant) y **Operator** (ejecuta acciones como ajustar stock).
- REQ-A2UI-04: El sistema DEBE enviar el historial de la conversación y contexto de ruta actual como parte del payload al agente.
- REQ-A2UI-05: El sistema DEBE mostrar chunks del streaming en tiempo real mientras el agente genera la respuesta.
- REQ-A2UI-06: El sistema DEBE manejar reconexión automática si la conexión SSE se cierra inesperadamente (hasta 3 reintentos con backoff exponencial).
- REQ-A2UI-07: El sistema DEBE mostrar indicador de "escribiendo..." mientras el agente genera la respuesta.

### A2UI Protocol

- REQ-A2UI-14: El sistema DEBE implementar un renderizador `A2UIRenderer.svelte` que mapee payloads JSON A2UI {type, props, children} a componentes nativos del catálogo.
- REQ-A2UI-15: El sistema DEBE mantener un catálogo de componentes registrados (Card, Button, Input, CrudTable, ChartWrapper, InsightPanel, AnomalyList, Badge, Modal, Spinner, Skeleton, SearchBar, CrudForm, Container).
- REQ-A2UI-16: El SSE DEBE extenderse para transportar eventos `a2ui` además de `chunk`/`done`/`error`/`tool`.
- REQ-A2UI-17: El sistema DEBE soportar dos modos de operación: Classic (navegación fija) y Agentive (UI compuesta por agente vía A2UI).
- REQ-A2UI-18: El sistema DEBE proveer un mecanismo de transición (botón "Ahorremos tiempo: SIGA") que transforme la vista actual de modo clásico a agentivo.
- REQ-A2UI-19: El A2UIRenderer DEBE soportar actualizaciones incrementales (update nodo por ID), parches de children (patch) y reemplazo completo de árbol (replace).

### Protocolo SSE

| Evento | Formato | Descripción |
|--------|---------|-------------|
| chunk | `data: {"type":"chunk","content":"texto parcial","done":false}\n\n` | Fragmento de respuesta del agente |
| done | `data: {"type":"done","content":"texto completo","done":true}\n\n` | Fin de la respuesta |
| error | `data: {"type":"error","code":"AGENT_TIMEOUT","message":"El agente no respondió a tiempo"}\n\n` | Error del agente |
| tool | `data: {"type":"tool","name":"ajustar_stock","status":"running"}\n\n` | Notificación de ejecución de herramienta |
| a2ui | `data: {"type":"a2ui","tree":{"type":"container","children":[...]},"action":"replace"}\n\n` | Payload A2UI para renderizado de UI generativa (action: replace | append) |
| update | `data: {"type":"update","nodeId":"chart-1","props":{...}}\n\n` | Actualización incremental de props en un nodo A2UI existente |
| patch | `data: {"type":"patch","nodeId":"container-main","children":[...]}\n\n` | Reemplazo de children en un nodo A2UI existente |

### Non-functional

- REQ-A2UI-08: El primer chunk DEBE aparecer en < 2s desde el envío del mensaje (TTFB).
- REQ-A2UI-09: Timeout DEBE ser 60s para respuesta completa del agente.
- REQ-A2UI-20: Toda la UI A2UI DEBE ser responsive-first: desktop, tablet (iPad) y smartphone. El renderizador DEBE adaptar layouts (grid → stack), el ContextualAssistant DEBE cambiar a bottom sheet en mobile, y todos los touch targets DEBEN cumplir mínimo 44px (WCAG).
- REQ-A2UI-21: El A2UIRenderer DEBE aceptar hints de layout responsive en el payload del agente: `{type: "container", props: {layout: "grid" | "stack" | "sidebar", columns: {desktop: 3, tablet: 2, mobile: 1}}}`.
- REQ-A2UI-22: En viewports < 768px, el modo agentivo DEBE priorizar una columna (stack vertical) con el chat como bottom sheet. En tablets (768-1024px), DEBE soportar 2 columnas con sidebar colapsable. En desktop (>1024px), DEBE soportar hasta 3-4 columnas con chat flotante.

## Scenarios (GWT)

### Scenario: Envío de mensaje con streaming
Given el usuario escribe "¿cuántos productos tienen stock bajo?" en el chat
When presiona Enter
Then el mensaje aparece en el historial como "enviando"
Y el widget muestra indicador "Analizando..."
Y los chunks llegan vía SSE y se renderizan en tiempo real
Y al recibir `done`, el indicador desaparece y el mensaje se marca como completo

### Scenario: Reconexión SSE por caída de red
Given una conexión SSE activa
When la conexión se cierra inesperadamente
Then el cliente espera 1s, reintenta (hasta 3 veces con backoff: 1s, 2s, 4s)
Y si reconecta, retoma donde quedó
Y si agota reintentos, muestra "Conexión perdida. Intenta de nuevo."

### Scenario: Timeout del agente
Given el agente no responde en 60s
When el timeout se dispara
Then se muestra "El agente no respondió a tiempo. Intenta de nuevo."
Y la conexión SSE se cierra limpiamente

### Scenario: Error del agente durante streaming
Given el agente encuentra un error mientras genera respuesta (ej: no puede conectar a DB)
When emite evento `{"type":"error","code":"DB_ERROR"}`
Then el último chunk válido se muestra
Y se muestra mensaje de error: "Ocurrió un error al procesar tu consulta"

### Scenario: ContextualAssistant en ruta /products
Given el usuario está en `/products` y abre el chat
When envía "agrega 10 unidades a Harina 000"
Then el payload incluye `{ currentRoute: "/products", history: [] }`
Y el agente reconoce el contexto y ejecuta la acción en el tenant actual

### Scenario: Agente renderiza dashboard vía A2UI
Given el usuario está en modo A2UI (agentivo)
When el agente responde con `{"type":"a2ui","tree":{"type":"container","children":[{"type":"chart","props":{...}},{"type":"insight-panel","props":{...}}]},"action":"replace"}`
Then el A2UIRenderer mapea "chart" → ChartWrapper.svelte
Y mapea "insight-panel" → InsightPanel.svelte
Y renderiza el árbol completo en el slot principal del dashboard

### Scenario: Transición de modo clásico a agentivo
Given el usuario está viendo el dashboard en modo clásico
When hace click en "Ahorremos tiempo: SIGA"
Then el store `a2ui.svelte.ts` cambia mode a "a2ui"
Y el ContextualAssistant se expande
Y el contenido principal pasa a ser controlado por A2UIRenderer
Y se envía un mensaje al agente con el contexto de la ruta actual

### Scenario: Actualización incremental de nodo A2UI
Given un dashboard agentivo renderizado con un ChartWrapper mostrando datos de ayer
When el agente emite `{"type":"update","nodeId":"chart-ventas","props":{"data":{...nuevos datos}}}`
Then el A2UIRenderer actualiza SOLO el nodo "chart-ventas" con los nuevos props
Y el resto del árbol permanece intacto

### Scenario: Dashboard agentivo responsive en mobile
Given un usuario en un smartphone con viewport < 768px
When entra en modo A2UI
Then el A2UIRenderer renderiza en una columna (stack vertical)
Y las cards ocupan el 100% del ancho
Y el ContextualAssistant se convierte en bottom sheet anclado abajo
Y los touch targets miden al menos 44px
Y el usuario puede swipear entre cards

### Scenario: Dashboard agentivo en tablet (iPad)
Given un usuario en iPad en orientación horizontal (1024px)
When entra en modo A2UI
Then el A2UIRenderer renderiza en grilla de 2 columnas
Y el chat se muestra como sidebar colapsable a la derecha
Y las cards se redistribuyen sin pérdida de contenido
Y al rotar a vertical (< 768px) la grilla pasa a 1 columna automáticamente

## Edge Cases
- REQ-A2UI-12: Si el usuario envía mensajes muy rápido (spam), el sistema DEBE encolar y procesar en orden, ignorando mensajes duplicados idénticos en los últimos 2s.
- REQ-A2UI-13: Si el agente responde con un tool call que falla, DEBE mostrar el error en el chat y permitir al usuario corregir.

## Acceptance Criteria
- [ ] Endpoint SSE funcional con `ReadableStream`
- [ ] `ContextualAssistant` flotante en todas las rutas
- [ ] Streaming de chunks renderizado en tiempo real
- [ ] Reconexión automática con backoff
- [ ] Modos Analyst y Operator funcionales
- [ ] Timeout de 60s implementado
- [ ] A2UIRenderer.svelte mapea type→component del catálogo
- [ ] SSE extendido con eventos a2ui/update/patch
- [ ] Dual-mode: Classic ↔ Agentive vía store reactivo
- [ ] Botón "Ahorremos tiempo: SIGA" en Header/FAB
- [ ] Actualización incremental de nodos A2UI (update/patch)
- [ ] Catálogo de 14 tipos A2UI registrados
- [ ] Responsive-first: 3 breakpoints funcionales (mobile < 768px, tablet 768-1024px, desktop > 1024px)
- [ ] ContextualAssistant como bottom sheet en mobile
- [ ] Touch targets mínimos 44px en todos los componentes interactivos
- [ ] Layout hints A2UI (grid/stack/sidebar) respetados y adaptados por viewport
