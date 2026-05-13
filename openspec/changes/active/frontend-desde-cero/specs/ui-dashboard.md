# Spec: ui-dashboard

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: ui-bff, ui-theme

## Requirements

### Functional

- REQ-DASH-01: El dashboard DEBE cargarse mediante `+page.server.ts` que compone datos desde el gateway vía `GET /api/v1/dashboard/insights`.
- REQ-DASH-02: El dashboard DEBE mostrar Insights (hallazgos analíticos, no métricas planas): total productos, total locales, valor de inventario, productos con stock bajo, tendencias de venta, anomalías detectadas.
- REQ-DASH-03: El sistema DEBE distinguir entre KPIs (numéricos, puntuales) e Insights (analíticos, con contexto/trend).
- REQ-DASH-04: El dashboard DEBE mostrar estado de carga mientras la load function compone datos (skeleton screens).
- REQ-DASH-05: El dashboard DEBE mostrar error si el gateway retorna error en alguna sección, pero DEBE mostrar las secciones que sí respondieron (fallback parcial).
- REQ-DASH-06: El dashboard DEBE actualizarse periódicamente (polling cada 60s) para datos de stock bajo y anomalías.

### Estructura de Insights

| Insight | Tipo | Fuente Gateway | Visualización |
|---------|------|---------------|---------------|
| Total productos | KPI | `GET /api/v1/dashboard/insights` | Card numérico |
| Total locales | KPI | ídem | Card numérico |
| Valor inventario | KPI | ídem | Card numérico con badge de tendencia |
| Productos stock bajo | Insight | ídem | Lista con alerta roja, link a /products?filter=low-stock |
| Anomalías detectadas | Insight | ídem | Lista de eventos recientes (ej: "ajuste de stock sin justificación") |
| Tendencia de inventario | Insight | ídem | Chart de barras (últimos 7 días, via Chart.js rescatado) |

### Non-functional

- REQ-DASH-07: La carga inicial DEBE resolverse en < 1s (P95). Si el gateway demora, mostrar skeleton inmediato.
- REQ-DASH-08: El polling NO DEBE hacer fetch si la pestaña no está visible (Page Visibility API).

## Scenarios (GWT)

### Scenario: Dashboard carga exitosamente
Given un usuario autenticado
When navega a `/dashboard`
Then la load function compone todos los insights desde el gateway
Y muestra KPIs numéricos, lista de stock bajo, tendencias y anomalías
Y skeleton screen se reemplaza suavemente

### Scenario: Falla parcial del gateway
Given el gateway retorna 200 con `{ insights: {...}, anomalies: { error: "timeout" } }`
When la load function procesa la respuesta parcial
Then muestra KPIs y stock bajo normalmente
Y la sección de anomalías muestra "No disponible" con botón de reintentar

### Scenario: Dashboard sin datos (tenant nuevo)
Given un tenant sin productos, sin locales, sin movimientos
When carga el dashboard
Then los KPIs muestran 0
Y los Insights muestran "No hay datos suficientes para generar insights"
Y no hay anomalías ni tendencias

### Scenario: Stock bajo detectado
Given un producto con cantidad < stock mínimo configurado
When el dashboard carga
Then el Insight de stock bajo incluye ese producto
Y el link redirige a `/products?filter=low-stock`
Y el card de stock bajo tiene indicador visual de alerta

## Edge Cases
- REQ-DASH-09: Si el gateway retorna 500 completo, la load function DEBE llamar `error(503)` y mostrar pantalla de error con reintentar.
- REQ-DASH-10: Si el usuario no tiene permisos para ver insights financieros, DEBE omitir valor de inventario sin error.

## Acceptance Criteria
- [ ] Dashboard renderiza todos los KPIs e Insights server-composed
- [ ] Skeleton loading state presente en todos los cards
- [ ] Fallback parcial funcional (sección con error no bloquea al resto)
- [ ] Polling automático cada 60s
- [ ] Estado vacío manejado para tenant nuevo
