# SIGA — Arquitectura del Sistema

> ** Última actualización:** 2026-04-23
> ** Propósito:** Plano director para desarrollo y presentación

---

## 1. ESTRUCTURA DE DIRECTORIOS

```
SIGA/
├── services/                    # TODOS LOS SERVICIOS (backend + frontend)
│   │
│   │── backend/                 # ⛔ HERENCIA (monolito legacy, no usar)
│   │
│   │── COMMON                   # Librería compartida
│   │   ├── dto/                  # DTOs comunes entre servicios
│   │   ├── utils/                # Utilidades compartidas
│   │   └── exceptions/           # Excepciones compartidas
│   │
│   ├── ━━━━ BACKEND SERVICES (Kotlin) ━━━━
│   │
│   │── auth/                     # Autenticación y usuarios
│   │   ├── src/main/kotlin/com/siga/auth/
│   │   │   ├── entity/            # Usuario, Rol, Permiso
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── Dockerfile
│   │
│   │── inventory/                # Gestión de productos y stock
│   │   ├── src/main/kotlin/com/siga/inventory/
│   │   │   ├── entity/            # Producto, Local, Stock, Category
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── Dockerfile
│   │
│   │── sales/                     # Transacciones y métricas
│   │   ├── src/main/kotlin/com/siga/sales/
│   │   │   ├── entity/            # Venta, DetalleVenta, Factura
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── Dockerfile
│   │
│   │── billing/                   # Suscripciones y pagos (MOCK)
│   │   ├── src/main/kotlin/com/siga/billing/
│   │   │   ├── entity/            # Plan, Suscripcion, Pago
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── controller/
│   │   └── Dockerfile
│   │
│   │── agent/                     # Motor A2UI (Python)
│   │   ├── app/
│   │   │   ├── parser/            # Chat parser
│   │   │   ├── intents/          # Intent detection
│   │   │   ├── actions/          # Action executor
│   │   │   └── responses/        # Response formatter
│   │   ├── Dockerfile
│   │   └── requirements.txt
│   │
│   │── gateway/                   # API Gateway (enrutador)
│   │   ├── src/main/kotlin/com/siga/gateway/
│   │   │   ├── routes/            # Rutas por servicio
│   │   │   └── filter/           # Filtros (auth, logging)
│   │   └── Dockerfile
│   │
│   │── registry/                   # Service Discovery (futuro)
│   │
│   ├── ━━━━ FRONTEND SERVICES ━━━━
│   │
│   │── landing/                   # Página de aterrizaje (futuro)
│   │
│   │── comercial/                 # Portal comercial (registro, login)
│   │   ├── src/
│   │   │   ├── pages/             # Registro, Login, Dashboard
│   │   │   └── components/
│   │   └── package.json
│   │
│   │── webapp/                    # Aplicación principal
│   │   ├── src/
│   │   │   ├── lib/
│   │   │   │   ├── components/    # UI components
│   │   │   │   ├── stores/       # Estado (Svelte stores)
│   │   │   │   └── api/          # Llamadas a gateway
│   │   │   ├── routes/           # Páginas (SvelteKit)
│   │   │   └── agent/            # Integración A2UI
│   │   │       ├── chat/         # Chatbox componente
│   │   │       └── mode/         # Toggle dashboard/agentic
│   │   └── package.json
│   │
│   └── mobile/                    # App nativa Android (futuro)
│       ├── app/
│       └── build.gradle
│
├── scripts/
│   └── database/
│       └── DB_SIGA_NEW.sql        # Script de base de datos
│
├── docs/
│   ├── architecture/
│   ├── diagrams/                  # Diagramas UML
│   └── CORE_BUSINESS.md           # Definición del negocio
│
├── openspec/                      # SDD artifacts
│   ├── core/
│   ├── specs/
│   └── changes/
│
├── docker-compose.yml             # Orquestación local
├── settings.gradle.kts           # Configuración Gradle
└── README.md
```

---

## 2. ARQUITECTURA DE MICROSERVICIOS

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              CLIENTES                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐                │
│  │ LANDING  │  │COMERCIAL │  │ WEBAPP   │  │ MOBILE   │                │
│  │ (futuro) │  │ (React)  │  │(Svelte)  │  │(futuro)  │                │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────────┘                │
│       │              │              │                                   │
│       │              │              │    ┌─────────────┐              │
│       │              │              ├────┤  A2UI CHAT  │              │
│       │              │              │    │  (webapp)   │              │
│       │              │              │    └─────────────┘              │
│       └──────────────┼──────────────┘                                 │
│                      │                                                 │
│                      ▼                                                 │
│              ┌──────────────┐                                          │
│              │   GATEWAY    │                                          │
│              │  (Kotlin)    │                                          │
│              └──────┬───────┘                                          │
│                     │                                                   │
│       ┌─────────────┼─────────────┬─────────────┐                      │
│       │             │             │             │                      │
│       ▼             ▼             ▼             ▼                      │
│  ┌─────────┐  ┌───────────┐  ┌─────────┐  ┌───────────┐              │
│  │  AUTH   │  │ INVENTORY │  │  SALES  │  │ BILLING   │              │
│  │(Kotlin) │  │ (Kotlin)  │  │(Kotlin) │  │ (Kotlin)  │              │
│  └────┬────┘  └─────┬─────┘  └────┬────┘  └─────┬─────┘              │
│       │             │             │             │                      │
│       ▼             ▼             ▼             ▼                      │
│  ┌────────┐    ┌────────┐    ┌────────┐    ┌────────┐                │
│  │siga_   │    │siga_   │    │siga_   │    │siga_   │                │
│  │auth    │    │inventory│   │sales   │    │billing │                │
│  │(schema)│    │(schema) │   │(schema)│    │(schema)│                │
│  └────────┘    └────────┘    └────────┘    └────────┘                │
│                                                                         │
│  ┌─────────┐                                                          │
│  │  AGENT  │◄───── A2UI ENGINE (Python)                               │
│  │(Python) │      ┌──────────────────────────────────────┐             │
│  └────┬────┘      │ • Parser de lenguaje natural          │             │
│       │           │ • Detección de intent                │             │
│       │           │ • Ejecución de acciones              │             │
│       │           │ • Formateador de respuestas           │             │
│       │           └──────────────────────────────────────┘             │
│       │                                                                  │
│       │           ┌────────┐  ┌────────┐  ┌────────┐                   │
│       └──────────►│INVEN-  │  │ SALES  │  │  AUTH  │                    │
│                   │TORY    │  │        │  │        │                    │
│                   └────────┘  └────────┘  └────────┘                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 3. ESQUEMAS DE BASE DE DATOS

| Schema | Servicio | Tablas Principales |
|--------|----------|-------------------|
| `siga_auth` | auth | usuarios, roles, permisos, roles_permisos, sesiones |
| `siga_inventory` | inventory | productos, categorias, locales, stock, movimientos |
| `siga_sales` | sales | ventas, detalle_ventas, facturas |
| `siga_billing` | billing | planes, suscripciones, pagos |
| `siga_comercial` | comercial | empresas, contactos, onboarding |

---

## 4. MODELO DE PERMISOS (Planes afectan permisos)

```
┌────────────────────────────────────────────────────────────────┐
│                    SISTEMA DE PERMISOS                          │
├──��─────────────────────────────────────────────────────────────┤
│                                                                │
│  ROLES BASE                                                    │
│  ──────────                                                    │
│  ┌─────────────┐                                               │
│  │   OWNER     │  Propietario de la empresa                   │
│  └──────┬──────┘  Permisos: TODO                              │
│         │                                                      │
│    ┌────┴────┐                                                 │
│    │         │                                                 │
│    ▼         ▼                                                 │
│ ┌──────┐ ┌──────┐                                             │
│ │ADMIN │ │STAFF │                                             │
│ └──────┘ └──────┘                                             │
│                                                                │
│  PERMISOS POR ACCIÓN                                           │
│  ─────────────────                                             │
│  ┌─────────────────────────────────────────────────┐          │
│  │ Permiso              │ STARTER │ PRO │ Detalle   │          │
│  ├──────────────────────┼─────────┼─────┼───────────┤          │
│  │ inventory:read      │    ✓    │  ✓  │ Ver stock │          │
│  │ inventory:write     │    ✓    │  ✓  │ Agregar   │          │
│  │ inventory:delete    │    ✗    │  ✓  │ Eliminar  │          │
│  │ sales:read           │    ✓    │  ✓  │ Ver ventas│          │
│  │ sales:insights      │    ✗    │  ✓  │ KPIs      │          │
│  │ agent:basic         │    ✓    │  ✓  │ Chat      │          │
│  │ agent:advanced      │    ✗    │  ✓  │ A2UI full │          │
│  │ users:manage        │    ✗    │  ✓  │ Agregar   │          │
│  │ locales:multiple    │    ✗    │  ✓  │ >1 local  │          │
│  └──────────────────────┴─────────┴─────┴───────────┘          │
│                                                                │
│  📌 RELACIÓN PLAN → PERMISOS                                    │
│  ─────────────────────────                                     │
│  STARTER = 1 local, 1 usuario, agente básico                   │
│  PRO     = ilimitado locales, 5 usuarios, A2UI, insights      │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 5. PLANES Y PRICING

```
┌────────────────────────────────────────────────────────────────┐
│                         PLANES SIGA                            │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌─────────────────────┐    ┌─────────────────────┐          │
│  │   🥉 STARTER        │    │   🏆 PRO             │          │
│  │                     │    │                     │          │
│  │   $29.900 CLP/mes   │    │   $59.900 CLP/mes    │          │
│  │   (~USD 30)        │    │   (~USD 60)         │          │
│  ├─────────────────────┤    ├─────────────────────┤          │
│  │                     │    │                     │          │
│  │ ✓ 1 Local           │    │ ✓ Locales ilimitados│          │
│  │ ✓ 1 Usuario         │    │ ✓ 5 Usuarios         │          │
│  │ ✓ Agente IA (chat)  │    │ ✓ Agente IA (A2UI)    │          │
│  │ ✓ 200 ops/mes       │    │ ✓ Ops ilimitadas     │          │
│  │ ✓ Dashboard básico  │    │ ✓ Dashboard completo  │          │
│  │ ✓ Soporte email     │    │ ✓ Insights + KPIs     │          │
│  │                     │    │ ✓ Soporte prioritario  │          │
│  │                     │    │                       │          │
│  │  [Empezar]         │    │  [Empezar]            │          │
│  └─────────────────────┘    └─────────────────────┘          │
│                                                                │
│  ──────────────────────────────────────────────────────────── │
│                                                                │
│  ⏰ PERÍODO DE PRUEBA                                          │
│  ├── 14 días gratis, sin tarjeta                               │
│  └── Al finalizar: elige plan o cierra cuenta                 │
│                                                                │
│  💳 FACTURACIÓN (MOCK)                                         │
│  ├── Plan mensual (para MVP)                                   │
│  ├── Datos reales requieren empresa constituida               │
│  └── Stripe/MercadoPago reservado para v2                      │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 6. FLUJO DE USUARIO

```
┌────────────────────────────────────────────────────────────────┐
│              RECORRIDO DEL USUARIO                             │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌─────────┐                                                   │
│  │ VISITO  │                                                   │
│  │ LANDING │                                                   │
│  └────┬────┘                                                   │
│       │                                                        │
│       ▼                                                        │
│  ┌─────────┐     ┌─────────┐                                  │
│  │ QUIERO  │────►│ REGIS-  │                                  │
│  │ PROBAR  │     │ TRARSE  │                                  │
│  └─────────┘     └────┬────┘                                  │
│                      │                                         │
│                      ▼                                         │
│              ┌───────────────┐                                 │
│              │  COMERCIAL    │                                 │
│              │ (registro,    │                                 │
│              │  selecciona   │                                 │
│              │  plan)        │                                 │
│              └───────┬───────┘                                 │
│                      │                                         │
│                      ▼                                         │
│              ┌───────────────┐                                 │
│              │ WEBAPP        │                                 │
│              │               │                                 │
│              │ ┌───────────┐ │                                 │
│              │ │A2UI CHAT │ │◄── Modo principal              │
│              │ └───────────┘ │                                 │
│              │ ┌───────────┐ │                                 │
│              │ │ DASHBOARD │ │◄── Modo control total          │
│              │ └───────────┘ │                                 │
│              └───────────────┘                                 │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 7. A2UI — INTERFAZ AGENTE

```
┌────────────────────────────────────────────────────────────────┐
│                      A2UI EN WEBAPP                            │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │  SIGA — Gestión Inteligente                    [≡] [?] │   │
│  ├────────────────────────────────────────────────────────┤   │
│  │                                                         │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │  💬 Buenos días, ¿en qué puedo ayudarte?       │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  │                                                         │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │ Añade 50 unidades de arroz al local Centro    │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  │                                                         │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │ ✅ Agregado: 50 arroz → Local Centro           │   │   │
│  │  │    Stock actual: 150 unidades                  │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  │                                                         │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │ [Chat input...                          ] [→]  │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  │                                                         │   │
│  │  [📊 Dashboard]  [🤖 Modo Agente]  [📈 Insights]       │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                │
│  MODO AGENTE ←→ DASHBOARD                                       │
│  (se toggelea, no son páginas separadas)                       │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 8. ROADMAP MVP

```
┌────────────────────────────────────────────────────────────────┐
│                         ROADMAP MVP                            │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  FASE 1: CORE FUNCIONAL                                        │
│  ──────────────────────                                        │
│  ☑ auth         — usuarios, login, logout                     │
│  ☑ inventory    — productos, locales, stock                   │
│  ☑ sales        — registro ventas                              │
│  ☐ billing      — planes, suscripciones (MOCK)               │
│  ☐ comercial    — registro, onboarding                         │
│  ☐ webapp       — dashboard funcional                          │
│                                                                │
│  FASE 2: AGENTE IA                                            │
│  ──────────────────                                            │
│  ☐ agent        — parser, intents, actions                   │
│  ☐ A2UI en webapp — chat integrado                            │
│                                                                │
│  FASE 3: INSIGHTS                                             │
│  ────────────────                                              │
│  ☐ sales:metrics — KPIs, gráficos                            │
│  ☐ webapp:insights — dashboard de métricas                    │
│                                                                │
│  FASE 4: GROWTH                                               │
│  ──────────────                                               │
│  ☐ landing      — página pública                             │
│  ☐ mobile       — app Android                                │
│  ☐ pagos reales — Stripe/MercadoPago                         │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## 9. RESPUESTAS A TUS PREGUNTAS

### 1. ¿Planes afectan permisos?

**SÍ.** El plan determina qué acciones puede ejecutar el usuario:
- STARTER = 1 local, 1 usuario, agente básico
- PRO = ilimitado, múltiples usuarios, A2UI completo, insights

La tabla de permisos está en la sección 4.

### 2. ¿Estructura de directorios ordenada?

**SÍ.** Ver sección 1. El plano está claro:
- `services/` = todo el código
- `backend/` = herencia (no usar)
- Servicios Kotlin = auth, inventory, sales, billing, gateway
- Agent = Python
- Frontends = comercial, webapp, (landing, mobile futuro)

### 3. ¿Arquitectura clara?

**SÍ.** Ver sección 2. El Gateway rutea, cada servicio tiene su schema, Agent se comunica con todos.

### 4. ¿MVP funcional?

**SÍ con esto:**
- Auth + Inventory + Sales funcionando
- Webapp con dashboard
- Billing mockeado
- Agent como Proof of Concept

---

*¿Necesitas que profundice en algún módulo?*