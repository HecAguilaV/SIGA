# Arquitectura SIGA — Documento de Arquitectura

> **Propósito**: Documentar las decisiones arquitectónicas clave del frontend SIGA.
> **Idioma**: Español (Rioplatense)
> **Última actualización**: 2026-05-14

---

## A2UI Protocol Integration

### Visión General

SIGA opera en **dos modos** que comparten el mismo catálogo de componentes Svelte 5:

1. **Modo Clásico**: Navegación fija por rutas (`/products`, `/analytics`, `/dashboard`). Los datos se componen server-side via load functions con polling cada 60s. Es el dashboard tradicional.
2. **Modo Agentivo (A2UI)**: El agente orquestador compone la UI dinámicamente enviando payloads JSON A2UI `{type, props, children}` via SSE. El frontend tiene un `A2UIRenderer.svelte` que mapea tipos a componentes del catálogo nativo.

La misma aplicación Dashboard alberga ambos modos. No son excluyentes — el usuario elige cuándo saltar de uno a otro mediante el botón "Ahorremos tiempo: SIGA".

### Arquitectura Dual-Mode

```
┌──────────────────────────────────────────────┐
│              SIGA Dashboard                    │
│                                                 │
│  ┌──────────────┐   ┌──────────────────────┐  │
│  │ MODO CLÁSICO │   │  MODO AGENTIVO A2UI  │  │
│  │              │   │                      │  │
│  │ load fns     │   │ A2UIRenderer.svelte  │  │
│  │ rutas fijas  │   │ mapea type→component │  │
│  │ polling 60s  │   │ árbol reactivo       │  │
│  └──────┬───────┘   └─────────┬────────────┘  │
│         │                     │                │
│         └──────┬──────────────┘                │
│                ▼                               │
│  ┌──────────────────────────────┐             │
│  │  Catálogo de Componentes     │             │
│  │  (Card, Button, CrudTable,   │             │
│  │   ChartWrapper, Insight, etc)│             │
│  └──────────────────────────────┘             │
│                                                 │
│  ┌──────────────────────────────┐             │
│  │  SSE (extendido)             │             │
│  │  {type:"text"} → burbuja     │             │
│  │  {type:"a2ui"} → renderer    │             │
│  │  {type:"update"} → parche    │             │
│  └──────────────────────────────┘             │
└──────────────────────────────────────────────┘
```

### Component Catalog

| Tipo A2UI | Componente Svelte 5 | Props clave |
|-----------|---------------------|-------------|
| card | Card.svelte | variant, padding, header, children, footer |
| button | Button.svelte | variant, size, loading, disabled |
| input | Input.svelte | type, label, error, placeholder |
| crud-table | CrudTable.svelte | columns, data, total, page, actions |
| crud-form | CrudForm.svelte | fields, onSubmit, initialValues, mode |
| chart | ChartWrapper.svelte | type, data, options, loading, height |
| insight-panel | InsightPanel.svelte | insights, variant |
| anomaly-list | AnomalyList.svelte | anomalies, emptyMessage |
| search-bar | SearchBar.svelte | value, placeholder, onSearch |
| badge | Badge.svelte | variant, children |
| modal | Modal.svelte | open, title, onClose |
| spinner | Spinner.svelte | size, variant |
| skeleton | Skeleton.svelte | variant |
| container | — (div wrapper) | layout, gap, direction |

> **Importante**: No se duplican componentes. El catálogo ES el mismo que usa el dashboard clásico. El renderizador A2UI solo mapea tipos a componentes existentes.

### SSE Protocol

El SSE implementado en F3 (ContextualAssistant) se extiende para transportar payloads A2UI:

| Evento | Payload | Descripción |
|--------|---------|-------------|
| chunk | `{type:"chunk", content, done:false}` | Fragmento de texto del agente |
| done | `{type:"done", content, done:true}` | Respuesta de texto completa |
| error | `{type:"error", code, message}` | Error del agente |
| tool | `{type:"tool", name, status}` | Ejecución de herramienta |
| **a2ui** | `{type:"a2ui", tree: A2UINode, action: "replace"\|"append"}` | Payload de UI generativa |
| **update** | `{type:"update", nodeId, props}` | Actualización incremental de props en un nodo |
| **patch** | `{type:"patch", nodeId, children}` | Reemplazo de children en un nodo |

### Mode Transition

El botón **"Ahorremos tiempo: SIGA"** es el mecanismo de transición de modo clásico a agentivo:

1. Usuario está en dashboard clásico
2. Click en "Ahorremos tiempo: SIGA" (visible en Header o como FAB global)
3. El store `a2ui.svelte.ts` cambia `mode = 'a2ui'`
4. El `ContextualAssistant` se expande (o aparece si estaba colapsado)
5. El contenido principal pasa a ser controlado por `A2UIRenderer.svelte`
6. Se envía un mensaje al agente con el contexto de la ruta actual
7. El agente responde con un payload A2UI y el renderizador construye la UI
8. El usuario conversa y la UI se reshapea dinámicamente según las respuestas del agente

La transición inversa (agentivo → clásico) se hace cerrando el modo agentivo, lo que restaura la navegación fija.

### Archivos Clave

| Archivo | Rol |
|---------|-----|
| `src/lib/components/a2ui/A2UIRenderer.svelte` | Renderizador A2UI: mapea type→component, maneja replace/update/patch |
| `src/lib/stores/a2ui.svelte.ts` | Store reactivo: mode (classic\|a2ui), tree, enter/exit/update/patch |
| `src/lib/stores/chat.svelte.ts` | Store de chat extendido: maneja eventos a2ui/update/patch del SSE |
| `src/lib/components/a2ui/` | Componentes A2UI: ContextualAssistant, ChatBubble, ChatInput, etc. |
| `src/lib/components/layout/Header.svelte` | Botón "Ahorremos tiempo: SIGA" en el Header |
| `src/routes/(dashboard)/+layout.svelte` | Layout dual-mode: detecta modo y renderiza contenido clásico o A2UI |

### Decisiones Arquitectónicas

| Decisión | Opción | Rationale |
|----------|--------|-----------|
| Dual-mode vs reemplazo total | **Dual-mode** | El modo clásico es la experiencia familiar. El agentivo es el valor diferencial. No son excluyentes, son complementarios. |
| Catálogo compartido vs duplicado | **Compartido** | El mismo componente (ChartWrapper) se renderiza igual en ambos modos, solo cambia quién lo compone (load function vs agente). |
| Formato A2UI vs protocolo completo Google | **Formato adaptado** | A2UI de Google está en v0.8. Adoptamos el formato JSON {type, props, children}. Más adelante compatibilidad total si hace falta. |
| Botón de transición visible vs automático | **Visible, no intrusivo** | El usuario debe elegir cuándo entrar en modo agentivo. El botón está visible pero no disruptivo. |
