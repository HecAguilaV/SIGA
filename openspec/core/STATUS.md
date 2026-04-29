# Estado del Ecosistema SIGA (Brain Sync)

Este documento refleja la realidad técnica y el progreso de la migración al **29 de abril de 2026**.

## 1. Mapa de Servicios y Puertos

| Servicio | Puerto | Schema DB | Estado | Nota de Realidad |
| :--- | :--- | :--- | :--- | :--- |
| `siga-eureka` | 8761 | — | ✅ Estable | Service Registry operando. |
| `siga-gateway` | 8080 | — | ✅ Estable | Ruteo, JWT y CORS funcional. |
| `siga-auth` | 8081 | `siga_auth` | ✅ Estable | Usuarios, roles y permisos. |
| `siga-inventory` | 8082 | `siga_inventario` | ✅ Estable | Corazón del stock multi-tenant. |
| `siga-sales` | 8083 | `siga_ventas` | ✅ Estable | POS y transacciones. |
| `siga-billing` | 8084 | `siga_comercial` | 🔄 En desarrollo | Gestión comercial y pagos. |
| `siga-agent` | 8000 | `siga_agente` | ✅ Estable | **IA Agéntica (Strands) estabilizada**. |
| `webapp-v2` | 5173 | — | 🔄 En proceso | Interfaz Premium (SvelteKit). |

## 2. Decisiones Técnicas Consolidadas

| Decisión | Justificación |
| :--- | :--- |
| **Schema-per-service** | Aislamiento lógico sin el costo de múltiples servidores de DB. |
| **Kotlin (No Data Classes)** | Estabilidad con Hibernate proxies y lazy loading. |
| **Python FastAPI** | Mejor ecosistema para integración con LLMs (LangChain). |
| **Void/Glassmorphism** | Estética premium para diferenciación competitiva. |

## 3. Próximos Pasos Estratégicos

*   **Integración A2UI**: Conectar la interfaz de chat con las herramientas CRUD del backend.
*   **Permisos Dinámicos**: Implementar la lógica para que los dueños de Pyme autogestionen permisos.
*   **Documentación MDX**: Activar el Technical Room interactivo.

---
*Actualizado: 29 de abril de 2026*
> **Un Soñador con poca RAM 🧑‍💻**
