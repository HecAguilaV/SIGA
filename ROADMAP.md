# 🗺️ Roadmap SIGA

> *Pasado, presente y futuro del Sistema Inteligente de Gestión de Activos.*
>
> Este roadmap es la **guía única de desarrollo**. Si algo no está acá, no se hace hasta que se discuta y se agregue.

---

## Leyenda

| Símbolo | Significado |
|---------|-------------|
| ✅ | Completado |
| 🚧 | En desarrollo |
| 📋 | Planificado |
| 🔮 | Futuro (post-MVP) |
| ❌ | No haremos |

---

## 📅 Línea de Tiempo

### 🟢 2025 — Prototipo y Monolito

| Hito | Detalle | Estado |
|------|---------|--------|
| **Sep 2025** | Primer prototipo funcional. Landing page con HTML estático | ✅ |
| **Oct 2025** | Branding SIGA: logo, paleta corporativa, favicon | ✅ |
| **Nov 2025** | Asistente IA con Gemini 2.5 Flash + RAG en landing page | ✅ |
| **Nov 2025** | Backend monolito Spring Boot con endpoints REST | ✅ |
| **Nov 2025** | Multi-repo: backend, webapp, webcomercial separados | ✅ |
| **Dic 2025** | **Monorepo**: unificación de todos los repos en uno solo | ✅ |
| **Dic 2025** | Sistema de permisos granular completo | ✅ |
| **Dic 2025** | Deploy a Railway con Docker + Spring Boot | ✅ |
| **Dic 2025** | Primeros tests unitarios (backend) | ✅ |
| **Dic 2025** | App móvil nativa (APK) | ✅ |

### 🟡 2026 — Microservicios y Hexagonal

| Hito | Detalle | Estado |
|------|---------|--------|
| **Abr 2026** | **Migración a Microservicios**: monolito → services/auth, billing, inventory, sales, agent, gateway, registry | ✅ |
| **Abr 2026** | **Arquitectura Hexagonal (Gold Standard)** en todos los servicios core | ✅ |
| **Abr 2026** | **SvelteKit 5** como frontend unificado (`apps/dashboard`) con server-side data composition (BFF nativo) | ✅ |
| **Abr 2026** | **Declaración de legacy deprecado**: webapp, customer-portal, admin-portal, landing, mobile, POS congelados | ✅ |
| **May 2026** | **Inventory Core Features**: stock consolidado, auto-SKU, búsqueda, conciliación, transferencias (US-2.1 a 2.5) | ✅ |
| **May 2026** | **Auth+Permissions**: tenant-scoped controllers, integration tests, cobertura 80.9% auth | ✅ |
| **May 2026** | **A2UI v0.9**: Agente IA en dashboard con streaming, narrativas, auto-scroll | ✅ |
| **May 2026** | **SAGA con Kafka**: Venta → Stock (coreografía) + BillingInvoiceConsumer | ✅ |
| **May 2026** | **Consolidación**: single frontend, billing solo SaaS, directorios legacy eliminados | ✅ |
| **May 2026** | **Este roadmap** creado como guía única de desarrollo | ✅ |

### 🟠 HOY — Estado Actual (Mayo 2026)

```
Backend:    ✅ Hexagonal completo · 300+ tests · Kafka SAGA · API Gateway
Frontend:   ✅ SvelteKit 5 (apps/dashboard/) · BFF nativo · A2UI v0.9
Auth:       ✅ JWT · BCrypt · dual login (Customer/User) · tenant-scoped CRUD
Billing:    ✅ Planes · Suscripciones · Pagos (SOLO SaaS de SIGA)
Inventory:  ✅ Stock consolidado · Auto-SKU · Búsqueda · Transferencias
Sales:      ✅ POS backend · SAGA con Inventory · SaleInvoice event
Agent:      ✅ Gemini 2.5 Flash · A2UI v0.9 · pgvector
Roles:      ⚠️ Solo ADMINISTRATOR, OPERATOR, CASHIER, EMPLOYEE — falta GODADMIN y SUPER_ADMIN
Dashboard:  ⚠️ Falta /(platform)/ para admin de SIGA
POS UI:     ❌ No existe como interfaz de usuario
Docs:       ✅ ROADMAP · ARCHITECTURE_STATE · LEARNING · README
```

---

## 🔵 FUTURO — Priorizado

### 🥇 Prioridad 1 — POS Simple (UI para cajeras)

El POS backend ya existe (Sales + SAGA). Falta la interfaz de usuario.

| Tarea | Detalle | Estado |
|-------|---------|--------|
| UI de venta rápida | Buscador de productos, carrito, métodos de pago | 📋 |
| Integración con inventory | Descuento de stock automático (SAGA ya funciona) | 📋 |
| Comprobante interno | Ticket no fiscal para el cliente | 📋 |
| KPIs para el cliente | Productos más vendidos, ventas por período, ticket promedio | 📋 |
| Cierre de caja | Cuadre de caja diario | 📋 |

**Dependencias**: Ninguna. Backend listo.
**Esfuerzo estimado**: UI + integración.

---

### 🥇 Prioridad 1 — GODADMIN + SUPER_ADMIN (Roles de plataforma)

El modelo de jerarquía actual solo tiene roles de negocio. Falta el dueño de SIGA.

| Tarea | Detalle | Estado |
|-------|---------|--------|
| Agregar `GODADMIN` a UserRole enum | Dueño de SIGA, control total de plataforma | 📋 |
| Agregar `SUPER_ADMIN` a UserRole enum | Dueño de empresa PYME, control inherente | 📋 |
| Restringir `ADMINISTRATOR` | Que NO pueda hacer cosas de GODADMIN ni SUPER_ADMIN | 📋 |
| Separación de rutas en dashboard | `/(platform)/` solo para GODADMIN, `/(dashboard)/` para clientes | 📋 |
| UI de administración de plataforma | CRUD de planes, clientes SaaS, suscripciones, monitoreo | 📋 |

**Dependencias**: Backend auth (roles), Frontend (rutas).
**Esfuerzo estimado**: Medio.

---

### 🥈 Prioridad 2 — Dashboard para Cliente PYME

Completar la UI que el cliente PYME ve.

| Tarea | Detalle | Estado |
|-------|---------|--------|
| Dashboard analytics | KPIs visuales (top productos, ventas, stock bajo) | 🚧 Parcial |
| Gestión de usuarios | CRUD de empleados con asignación de roles | ✅ |
| Gestión de productos | CRUD con auto-SKU, búsqueda, categorías | ✅ |
| Gestión de tiendas | CRUD de locales/sucursales | ✅ |
| Perfil y configuración | Datos de la empresa, plan actual | 📋 |

**Dependencias**: Ninguna. Mayoría del backend listo.
**Esfuerzo estimado**: Bajo-Medio.

---

### 🥈 Prioridad 2 — Integración Continua y Calidad

| Tarea | Detalle | Estado |
|-------|---------|--------|
| JaCoCo coverage unificado | Reporte agregado de todos los servicios | ✅ |
| Subir cobertura billing + sales | Billing 33 tests, Sales 103 tests — cubrir más | 📋 |
| Tests de SAGA con Embedded Kafka | Tests de integración de eventos | 📋 |
| GitHub Actions CI | Build + test + coverage en cada push | 📋 |
| Contenerización completa | docker-compose funcional para todo el stack | ✅ |

**Dependencias**: Varía por tarea.
**Esfuerzo estimado**: Continuo.

---

### 🥉 Prioridad 3 — Facturación Electrónica SII (DTE)

| Tarea | Detalle | Estado |
|-------|---------|--------|
| Investigar servicios DTE | Nexxus, E-Sii.cl, FacturaTotal, Docket | 📋 |
| Integrar API de facturación | Módulo en billing o sales que hable con el servicio externo | 🔮 |
| Emitir boletas electrónicas | Para ventas a consumidores finales | 🔮 |
| Emitir facturas electrónicas | Para ventas entre empresas | 🔮 |
| UI de facturación en dashboard | El cliente PYME ve y descarga sus DTE | 🔮 |

**Decisión**: **No construir desde cero.** Integrar con servicio externo. El SII chileno requiere certificados digitales, timbraje, XML, firma electrónica avanzada — es un producto en sí mismo.

**Dependencias**: POS simple funcionando, clientes pidiéndolo.
**Esfuerzo estimado**: Alto (pero lo absorbe el servicio externo).

---

### 🥉 Prioridad 3 — AWS Deployment

| Tarea | Detalle | Estado |
|-------|---------|--------|
| VPC + subnets | Red virtual | 🔮 |
| ECR + ECS | Contenedores en AWS | 🔮 |
| RDS PostgreSQL | Base de datos administrada | 🔮 |
| MSK (Kafka) | Event broker administrado | 🔮 |
| CI/CD con GitHub Actions | Build → push → deploy automático | 🔮 |
| SSL/TLS + dominio | HTTPS con certificado | 🔮 |

**Dependencias**: Créditos AWS disponibles.
**Esfuerzo estimado**: Alto.

---

### 🔮 Futuro Lejano (Post-MVP)

| Tarea | Detalle | Prioridad |
|-------|---------|-----------|
| App móvil nativa | React Native o Kotlin Multiplatform | Baja |
| Agentes IA avanzados | RAG con documentos del negocio, predicciones | Baja |
| Big Data / Analytics | Streaming a BigQuery, dashboards avanzados | Baja |
| Landing page independiente | Para SEO y marketing (si es necesario) | Baja |
| Multi-idioma en frontend | i18n completo | Baja |

---

## ⛔ Lo que NO vamos a hacer

| Esto | Razón |
|------|-------|
| Facturación electrónica SII desde cero | Infierno regulatorio. Integrar con Nexxus/E-Sii |
| SUPER_ADMIN como enum value normal | Debe ser un tipo de principal distinto (como Customer), no un UserRole más |
| GODADMIN en la misma tabla que Users | Debe ser una entidad separada con su propio modelo |
| Frontend separado para admin | Va dentro del dashboard como `/(platform)/` |
| Microservicios separados en repos | Monorepo hasta que haya equipo humano |
| Base de datos por servicio con servidores separados | Una instancia PostgreSQL con BDs separadas hasta que haya presupuesto |

---

## 📐 Decisiones Arquitectónicas Clave

| Decisión | Valor |
|----------|-------|
| **Monorepo** | Un solo repo para todos los microservicios. Cambios atómicos, CI/CD unificado |
| **Arquitectura Hexagonal** | Puerto-Adaptador en todos los servicios core. El dominio no sabe de Spring/JPA/HTTP |
| **BFF Nativo (SvelteKit 5)** | Server-side data composition en `+page.server.ts`. El cliente nunca hace fetch directo |
| **Database per Service** | Cada servicio es dueño absoluto de su BD. Comunicación solo por API o eventos |
| **SAGA por Coreografía (Kafka)** | Sin orquestador central. Cada servicio publica y reacciona a eventos |
| **Zero-Knowledge Architecture** | SIGA no accede a datos financieros de los clientes de sus clientes (Ley 21.719) |
| **SDD (Spec-Driven Development)** | Todo cambio grande pasa por: Explore → Propose → Spec → Design → Tasks → Apply → Verify → Archive |
| **Strict TDD** | RED → GREEN → REFACTOR. No se commitea con tests fallando |
| **Billing solo SaaS** | Billing gestiona planes, suscripciones y pagos de SIGA. La facturación PYME es otro dominio |
| **POS simple primero** | Valor inmediato para el cliente. Facturación electrónica después |

---

## 📊 Métricas de Progreso

| Métrica | Actual | Objetivo MVP |
|---------|--------|--------------|
| Tests totales | 300+ | 500+ |
| Cobertura auth | 80.9% | 85% |
| Cobertura billing | ~40% | 75% |
| Cobertura inventory | ~70% | 80% |
| Cobertura sales | ~60% | 80% |
| Frontend rutas implementadas | ~60% | 100% |
| Roles implementados | 4/6 | 6/6 |
| Servicios cloud | 0 | Todos en AWS |

---

## 🧭 Principios de Desarrollo

1. **Primero la PYME, después la plataforma** — El valor de SIGA está en resolver el día a día del negocio. La administración de SIGA (planes, monitoreo) es secundaria.
2. **Sin boleta fiscal hasta que el cliente la pida** — El POS simple da valor inmediato. La facturación electrónica es una mejora, no un requisito de entrada.
3. **No reinventar ruedas regulatorias** — SII, certificados digitales, timbraje → tercerizar.
4. **Un cambio a la vez** — No mezclar features. Cada SDD cycle toca un tema y termina.
5. **Documentar inmediatamente** — La documentación desactualizada duele más que la falta de documentación.
6. **Si no está en el roadmap, no se hace** — Cada nuevo feature se discute y se prioriza antes de codificar.

---

> *"No gestiones tu inventario, gestiona Tu Tiempo."*
>
> — SIGA
