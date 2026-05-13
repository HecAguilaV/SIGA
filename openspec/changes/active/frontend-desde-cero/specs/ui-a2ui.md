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

### Protocolo SSE

| Evento | Formato | Descripción |
|--------|---------|-------------|
| chunk | `data: {"type":"chunk","content":"texto parcial","done":false}\n\n` | Fragmento de respuesta del agente |
| done | `data: {"type":"done","content":"texto completo","done":true}\n\n` | Fin de la respuesta |
| error | `data: {"type":"error","code":"AGENT_TIMEOUT","message":"El agente no respondió a tiempo"}\n\n` | Error del agente |
| tool | `data: {"type":"tool","name":"ajustar_stock","status":"running"}\n\n` | Notificación de ejecución de herramienta |

### Non-functional

- REQ-A2UI-08: El primer chunk DEBE aparecer en < 2s desde el envío del mensaje (TTFB).
- REQ-A2UI-09: Timeout DEBE ser 60s para respuesta completa del agente.

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
