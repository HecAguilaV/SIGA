# Estado del Ecosistema SIGA (Brain Sync)

Este documento refleja la realidad técnica y el progreso de la migración al **16 de mayo de 2026**.

## 1. Mapa de Servicios y Puertos

| Servicio | Puerto | Schema DB | Estado | Nota de Realidad |
| :--- | :--- | :--- | :--- | :--- |
| `siga-eureka` | 8761 | — | ✅ Estable | Service Registry operando. |
| `siga-gateway` | 8080 | — | ✅ Estable | Ruteo vía RewritePath, Eureka locator deshabilitado, CORS funcional. |
| `siga-auth` | 8081 | `siga_auth` | ✅ Estable | Autenticación completa: register, verify, login dual (Customer+User), JWT, CRUD multi-tenant — 126 tests. |
| `siga-inventory` | 8082 | `siga_inventario` | ✅ Estable | Corazón del stock multi-tenant. |
| `siga-sales` | 8083 | `siga_ventas` | ✅ Estable | POS y transacciones. |
| `siga-billing` | 8084 | `siga_comercial` | 🔄 En desarrollo | Gestión comercial y pagos. |
| `siga-agent` | 8000 | `siga_agente` | ✅ Estable | **IA Agéntica (Kotlin Spring Boot + A2UI v0.9)**. |
| `webapp-v2` | 5173 | — | 🔄 En proceso | Interfaz Premium (SvelteKit). |

## 2. Decisiones Técnicas Consolidadas

| Decisión | Justificación |
| :--- | :--- |
| **Schema-per-service** | Aislamiento lógico sin el costo de múltiples servidores de DB. |
| **Kotlin (No Data Classes)** | Estabilidad con Hibernate proxies y lazy loading. |
| **Kotlin Spring Boot (Agent)** | Agente migrado de Python/FastAPI a Kotlin/Spring Boot con A2UI v0.9. El SDK GenAI de Google reemplaza LangChain. |
| **Void/Glassmorphism** | Estética premium para diferenciación competitiva. |

## 3. Próximos Pasos Estratégicos

*   ✅ **Autenticación Multi-Tenant Completa**: Register, verify, login dual (Customer+User), JWT, y CRUD de usuarios con alcance por tenant — 126 tests, 0 fallos.
*   ✅ **Integración A2UI**: Agente Kotlin con protocolo A2UI v0.9, 3-tier fallback, SSE streaming. Frontend con renderizador A2UI, componentes Svelte 5 (StatCard, TrendBadge, DataTable).
*   **Permisos Dinámicos**: Implementar la lógica para que los dueños de Pyme autogestionen permisos.
*   ✅ **Infra-hardening**: Gateway routes corregidas (RewritePath), Flyway unificado (DDL propio en V1), JWT secret hardening (sin defaults, validación al startup).
*   **Documentación MDX**: Activar el Technical Room interactivo.

---
*Actualizado: 16 de mayo de 2026*
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
