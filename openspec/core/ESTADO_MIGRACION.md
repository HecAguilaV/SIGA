# Estado de la Migración — SIGA v2.1

> Este documento consolida el estado actual del proyecto. Actualizado: Abril 2026.

---

## 1. Arquitectura Objetivo

| Servicio | Puerto | Schema DB | Responsabilidad | Estado |
|----------|--------|-----------|----------------|--------|
| `siga-eureka` | 8761 | — | Service Registry | ✅ |
| `siga-gateway` | 8080 | — | Ruteo, JWT, CORS | ✅ Estructura |
| `siga-auth` | 8081 | `siga_auth` | Usuarios, roles, permisos | ✅ Entidades + Repos + Controllers |
| `siga-inventario` | 8082 | `siga_inventario` | Productos, categorías, stock | ✅ Entidades + Repos + Controllers |
| `siga-ventas` | 8083 | `siga_ventas` | POS, transacciones, caja | ✅ Entidades + Repos + Controllers |
| `siga-billing` | 8084 | `siga_comercial` | Planes, suscripciones, pagos | ✅ Entidades + Repos + Controllers |
| `siga-agente` | 8000 | `siga_agente` | IA Asistente (Python) | ✅ Estructura |
| `siga-backend` | 8084 | `siga_comercial` | Legacy monolito | 🗑️ Eliminar |

---

## 2. Base de Datos

### Schemas Activos (PostgreSQL)
| Schema | Tablas | Estado |
|--------|--------|--------|
| `siga_auth` | usuarios, permisos, roles_permisos, usuarios_permisos, usuarios_locales | ✅ |
| `siga_inventario` | productos, categorias, locales, stock, movimientos, alertas | ✅ |
| `siga_ventas` | ventas, detalles_venta, carrito_pos, turnos_caja, transacciones_pos, metodos_pago | ✅ |
| `siga_comercial` | usuarios, planes, suscripciones, pagos, carritos, facturas | ✅ |
| `siga_agente` | conversaciones, documentos, respuestas | ✅ |

### Convenciones de Entidades (SPEC: jpa-entity-convention)
- ✅ Usar `class` (NO `data class`)
- ✅ `equals`/`hashCode` basados en `id`
- ✅ `toString` sin relaciones lazy

---

## 3. Flujo de Autenticación (OBJETIVO)

```
[Empresa/Dueño]
       │
       ▼
[Web Comercial] ──Google OAuth2──▶ [siga-auth]
       │                                    │
       │ (registra, compra plan)            │
       ▼                                    ▼
[Suscripción ACTIVA] ◀──────────────────────┘
       │
       ▼
[SSO via JWT] ──▶ [Webapp POS]
       │
       ▼
[Usuario Empleado] ──creado por Dueño──▶ usuario/pass estándar
```

---

## 4. Pendientes (SDD: migracion-microservicios)

### Fase 2: Servicios Core
- [ ] **Permisos Granulares Dinámicos** — Dueño puede asignar/quitar permisos
- [ ] **Herencia de Privilegios** para el Agente IA
- [ ] Reconciliación de Caja (monto contado vs sistema)
- [ ] Webhooks de stock entre Ventas ↔ Inventario

### Fase 3: IA y Resiliencia
- [ ] JWT Pass-through en servicio Python
- [ ] Herramientas CRUD con chequeo de permisos
- [ ] Diseño de `siga-fallback` (módulo o servicio dedicado)
- [ ] Queries SQL/PL-SQL predefinidas para fallback

### Fase 4: Verificación
- [ ] Smoke Test: todos los servicios registrados en Eureka
- [ ] Auditoría de seguridad multi-tenant

---

## 5. Decisions Conocidas

| Decisión | Justificación |
|----------|---------------|
| Schema-per-service | Costo-efectivo, aislamiento lógico sin infraestructura separada |
| Kotlin + Spring Boot | Stack oficial, soporte Google/JetBrains |
| Python para Agente | Ecosistema LLM más maduro |
| JWT para Auth | Stateless, compatible con móvil |
| No data class en JPA | Compatibilidad con Hibernate proxies |

---

## 6. Referencias

- `openspec/changes/migracion-microservicios/` — SDD activo
- `openspec/specs/database/spec.md` — Requisitos de entidades
- `services/` — Código fuente de servicios
- `scripts/database/DB_SIGA_NEW.sql` — Script de esquemas
