# Estado Actual del Ecosistema SIGA

Este documento resume el estado de los componentes del Proyecto SIGA al **24 de abril de 2026**.

## Componentes

| Servicio | Estado | Tecnología | Propósito |
| :--- | :--- | :--- | :--- |
| **auth** | ✅ Compilando | Kotlin / Spring | Autenticación y usuarios |
| **inventory** | ✅ Compilando | Kotlin / Spring | Productos, stock, locales |
| **sales** | ✅ Compilando | Kotlin / Spring | Ventas, métricas |
| **billing** | ✅ Compilando (MOCK) | Kotlin / Spring | Planes, suscripciones |
| **agent** | ⏳ Por implementar | Python / FastAPI | A2UI Engine |
| **gateway** | ⏳ Configurado | Kotlin | Enrutador |
| **comercial** | 🟡 Legacy | React | Portal registro |
| **webapp** | 🟡 Legacy | SvelteKit | Panel principal |
| **landing** | ⏳ Por crear | TBD | Landing page |

## Schema agent (A2UI)

Tablas creadas en `scripts/database/DB_SIGA_ENGLISH.sql`:

| Tabla | Propósito |
| :--- | :--- |
| `agent.documents` | RAG con tenant_id |
| `agent.conversations` | Contexto + tenant_id |
| `agent.intent_logs` | Auditoría de intents |
| `agent.pending_actions` | Confirmación 60s |
| `agent.intent_permissions` | Permisos por plan |

## Contratos

- `docs/A2UI_PROTOCOL.md` — Contrato agent ↔ webapp

## Próximos Pasos

1. Agent Service (Python): parser → validación DB → ejecución
2. Webapp + A2UI: chat + visual hints

---

*Actualizado: 2026-04-24*
*Último commit: 59d8c32 feat: añadir schema agent para A2UI*