# SIGA: Sistema Inteligente de Gestión de Activos

*Read this in other languages: [English](README.en.md)*

Bienvenido a **SIGA**, un ecosistema inteligente diseñado para la Gestión de Activos e Inventarios en Pymes multi-sucursal. A diferencia de un ERP tradicional, SIGA integra Agentes de IA Operativos que actúan como co-pilotos en la administración del negocio.

Este repositorio es un Monorepo de Microservicios con arquitectura Zero-Trust y aislamiento de datos por esquema (Database-per-service).

---

## Bóveda Documental (Simetría Bilingüe)

Se ha establecido un estándar de Espejo Semántico para garantizar que la documentación esté siempre disponible y actualizada en ambos idiomas.

| Sección | Castellano (Nativo) | English (Mirror) |
| :--- | :--- | :--- |
| **Arquitectura** | [Modelo C4 L1/L2](docs/es/arquitectura/MODELO_C4.md) | [C4 Model L1/L2](docs/en/architecture/C4_MODEL.md) |
| **Seguridad** | [Manifiesto de Seguridad](docs/es/security/MANIFIESTO_SEGURIDAD.md) | [Security Manifesto](docs/en/security/SECURITY_MANIFESTO.md) |
| **Pruebas y Calidad** | [Estrategia de Tests](docs/tests/es/README.md) | [Testing Strategy](docs/tests/en/README.md) |
| **Negocio** | [Reglas de Negocio Core](docs/es/arquitectura/REGLAS_NEGOCIO_CORE.md) | [Core Business Rules](docs/en/architecture/CORE_BUSINESS.md) |
| **Testing/APIs** | [Colección Postman](docs/es/api/siga-apis.postman_collection.json) | [Postman Collection](docs/en/api/siga-apis.postman_collection.json) |
| **Roadmap** | [ROADMAP.md](ROADMAP.md) | [ROADMAP.en.md](ROADMAP.en.md) |

---

## Stack Tecnológico

SIGA utiliza tecnologías de vanguardia para asegurar el rendimiento y el cumplimiento legal:

- **Backend**: Kotlin + Spring Boot 4.0.6. Implementación estricta de Arquitectura Hexagonal (Gold Standard) y Disciplina TDD.
- **Mensajería**: Apache Kafka (SAGA Coreografía) para transacciones distribuidas entre microservicios.
- **IA**: Kotlin + Spring Boot + Google Gemini SDK (A2UI v0.9 Protocol + 3-Tier Fallback).
- **Frontend**: SvelteKit 5 (`apps/dashboard`) — ÚNICA app frontend activa. Funciona como BFF nativo con server-side data composition.
- **Persistencia**: PostgreSQL con aislamiento de esquemas y UUID v4 como estándar único de identidad y seudonimización.
- **Seguridad**: JWT (Stateless) y cumplimiento riguroso con la Ley Chilena 21.719.
- **Ops**: [ContainerFlow](https://github.com/RGJorge/ContainerFlow) — visualizador de arquitectura Docker en tiempo real con notificaciones vía Discord (alertas de estado, umbrales de CPU/RAM, errores de acciones).

---

## Despegue Rápido (Backend)

La infraestructura de SIGA está completamente dockerizada para garantizar un entorno de desarrollo consistente.

```bash
# Opción 1: Levantar todo de golpe (requiere ~16 GB RAM)
docker compose up -d

# Opción 2: Arranque escalonado (recomendado para máquinas con recursos limitados)
# Levanta servicios de a uno con delays de 30-60s entre cada fase.
bash scripts/start-staggered.sh
```

**Estado de Servicios**: Una vez arriba, la API Gateway orquestará las peticiones hacia los microservicios de Auth, Billing e Inventory bajo el estándar UUID.

**Panel de Operaciones**: ContainerFlow queda disponible en `http://localhost:9470` — permite visualizar la topología de contenedores, revisar métricas CPU/RAM, consultar logs, ejecutar comandos (`docker exec`) directamente desde el navegador, y recibir alertas por Discord cuando un contenedor se cae, reinicia, o supera umbrales de recursos.

---

## Frontend Unificado — `apps/dashboard`

**SIGA tiene UNA SOLA app frontend activa: `apps/dashboard/` (SvelteKit 5).**

El frontend se divide en grupos de rutas dentro del mismo proyecto:
- `/(auth)/` — login, logout
- `/(dashboard)/` — interfaz del cliente PYME (categorías, productos, tiendas, usuarios, analytics)
- `/(platform)/` — **futuro**: administración de la plataforma SIGA (planes, suscripciones, clientes SaaS, monitoreo)
- `/assistant` — agente IA conversacional

*Nota: Los antiguos directorios `apps/admin-portal`, `apps/customer-portal`, `apps/mobile`, `apps/pos` y `apps/landing` fueron eliminados en mayo 2026. Eran carpetas vacías o con solo READMEs que confundían. Sus funcionalidades se unificaron en `apps/dashboard`.*

---

## Microservicios

| Servicio | Puerto | BD | Rol |
|----------|--------|----|-----|
| `siga-auth` | 8081 | `siga_auth` | Identidad, registro, login, JWT, permisos, roles |
| `siga-billing` | 8084 | `siga_billing` | **SaaS de SIGA**: planes de suscripción, gestión de clientes del SaaS, pagos |
| `siga-inventory` | 8082 | `siga_inventory` | Stock multi-tenant, búsqueda, transferencias, conciliación |
| `siga-sales` | 8083 | `siga_sales` | POS y registro de ventas de la PYME (SAGA con inventory) |
| `siga-agent` | 8000 | `siga_agent` | Agente IA con búsqueda vectorial (A2UI v0.9) |
| `siga-gateway` | 8080 | — | API Gateway (Spring Cloud Gateway) |
| `siga-registry` | 8761 | — | Service Discovery (Eureka) |

### Billing — Solo SaaS de SIGA

`siga-billing` gestiona exclusivamente la facturación de la **plataforma SIGA**:
- Planes de suscripción (planes que las PYMEs contratan)
- Suscripciones activas por cliente
- Pagos de las PYMEs a SIGA

**No incluye** la facturación de ventas de las PYMEs (boletas/facturas SII). Esa funcionalidad está planificada para una fase futura mediante integración con servicios externos (Nexxus/E-Sii).

---

## POS — Fase 1 (Actual)

El POS actual registra ventas con descuento automático de stock vía SAGA (Kafka). Es un POS simple pensado para que cajeras registren ventas rápidamente.

**Incluye:**
- Búsqueda de productos en tiempo real
- Carrito de ventas
- Múltiples métodos de pago
- Descuento de stock automático (SAGA)
- Comprobante interno (no fiscal)

**No incluye (futuro):**
- Facturación electrónica SII (boletas/facturas)
- Integración con servicios DTE externos

---

## Inventory Core — Capacidades

| Capacidad | Endpoint |
|-----------|----------|
| Stock consolidado multi-punto | `GET /api/v1/inventory/stock/consolidated?productId=X` |
| Auto-SKU + detección duplicados | `POST /api/v1/inventory/products` |
| Búsqueda inteligente (ILIKE+unaccent) | `GET /api/v1/inventory/products/search?q=X` |
| Reconciliación de stock con alertas | `POST /api/v1/inventory/stock/reconciliations` |
| Transferencia bodega ↔ punto | `POST /api/v1/inventory/stock/transfers` |
| Historial de movimientos | `GET /api/v1/inventory/stock/movements` |

---

## Gobernanza, Privacidad y Cumplimiento (Ley 21.719)

SIGA no es solo código; es una plataforma diseñada para ser legalmente inexpugnable bajo la normativa chilena de protección de datos:

- **Privacidad por Diseño (Art. 14 quáter)**: La arquitectura está blindada para que el proveedor del servicio (SIGA Admin) **NO** tenga acceso a datos financieros sensibles, montos de facturación ni detalles granulares de los clientes de las Pymes.
- **Zero-Knowledge Architecture**: Aplicamos el principio de "Ceguera al Dato, Atención al Flujo". Gestionamos la infraestructura y la disponibilidad, pero respetamos la soberanía absoluta de los datos de cada negocio.
- **Seudonimización (Art. 2, letra l)**: Uso mandatorio de **UUID v4** en todas las capas para evitar la trazabilidad no autorizada de personas naturales.
- **SDD (Spec-Driven Development)**: Cada cambio técnico nace de una especificación, asegurando que la seguridad y la privacidad sean requisitos funcionales, no añadidos posteriores.
- **Bilingüismo Técnico**: Documentación y contratos de API en espejo (ES/EN) para asegurar transparencia y escalabilidad.

## Gestión de Secretos

A partir de junio 2026, todas las contraseñas de bases de datos y secretos deben definirse **exclusivamente vía variables de entorno** (o GitHub Secrets en CI). No hay valores hardcodeados ni defaults en `application.yml`.

Para desarrollo local, creá un archivo `.env` en la raíz del proyecto:

```bash
# PostgreSQL
POSTGRES_PASSWORD=tu_password_segura

# Servicios
AUTH_DB_PASSWORD=tu_password_segura
INVENTORY_DB_PASSWORD=tu_password_segura
SALES_DB_PASSWORD=tu_password_segura
BILLING_DB_PASSWORD=tu_password_segura
NOTIFICATION_DB_PASSWORD=tu_password_segura
AGENT_DB_PASSWORD=tu_password_segura

# JWT
JWT_SECRET=clave_secreta_para_firmar_tokens
```

Ver `.env.example` para la lista completa de variables requeridas.

## Roadmap

Ver [ROADMAP.md](ROADMAP.md) para el plan de desarrollo completo (pasado, presente y futuro).

## Licencia

Este proyecto es propiedad privada de **Héctor Aguila**. Todos los derechos están reservados. El código se proporciona exclusivamente para fines de revisión técnica y cumplimiento de auditoría bajo la Ley 21.719. Consulte el archivo [LICENSE](LICENSE) para más detalles.

---

Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
