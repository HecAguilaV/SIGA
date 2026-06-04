# SIGA: Intelligent Asset Management System

*Read this in other languages: [Español](README.md)*

Welcome to **SIGA**, an intelligent ecosystem designed for Asset and Inventory Management in multi-branch SMEs. Unlike traditional ERPs, SIGA integrates Operational AI Agents that act as co-pilots in business administration.

This repository is a Microservices Monorepo featuring Zero-Trust architecture and schema-level data isolation (Database-per-service).

---

## Documentation Vault (Bilingual Symmetry)

A Semantic Mirroring standard has been established to ensure that documentation is always available and updated in both languages.

| Section | Spanish (Native) | English (Mirror) |
| :--- | :--- | :--- |
| **Architecture** | [C4 Model L1/L2](docs/es/arquitectura/MODELO_C4.md) | [C4 Model L1/L2](docs/en/architecture/C4_MODEL.md) |
| **Security** | [Security Manifesto](docs/es/security/MANIFIESTO_SEGURIDAD.md) | [Security Manifesto](docs/en/security/SECURITY_MANIFESTO.md) |
| **Testing and Quality** | [Testing Strategy](docs/tests/es/README.md) | [Testing Strategy](docs/tests/en/README.md) |
| **Business** | [Core Business Rules](docs/es/arquitectura/REGLAS_NEGOCIO_CORE.md) | [Core Business Rules](docs/en/architecture/CORE_BUSINESS.md) |
| **Testing/APIs** | [Postman Collection](docs/es/api/siga-apis.postman_collection.json) | [Postman Collection](docs/en/api/siga-apis.postman_collection.json) |
| **Front-end** | [Frontend Pact](docs/es/arquitectura/PACTO_FRONTEND.md) | [Frontend Pact](docs/en/architecture/FRONTEND_PACT.md) |

---

## Technology Stack (V2)

SIGA utilizes cutting-edge technologies to ensure performance and legal compliance:

- **Backend**: Kotlin + Spring Boot 4.0.6. Strict implementation of Gold Standard Hexagonal Architecture and TDD Discipline.
- **Messaging**: Apache Kafka (SAGA Choreography) for distributed transactions between microservices.
- **AI**: Kotlin + Spring Boot + Google Gemini SDK (A2UI v0.9 Protocol + 3-Tier Fallback).
- **Frontend**: SvelteKit 5 (Unified dashboard under `apps/dashboard`).
- **Persistence**: PostgreSQL with schema isolation and UUID v4 as the unique standard for identity and pseudonymization.
- **Security**: JWT (Stateless) and rigorous compliance with Chilean Law 21.719.
- **Ops**: [ContainerFlow](https://github.com/RGJorge/ContainerFlow) — real-time Docker architecture visualizer with interactive topology, metrics, logs, best-practice recommendations, and Discord notifications (state changes, CPU/RAM alerts, action errors).

---

## Quick Start (Backend)

SIGA's infrastructure is fully dockerized to ensure a consistent development environment.

```bash
# Option 1: Spin up everything at once (requires ~16 GB RAM)
docker compose up -d

# Option 2: Staggered startup (recommended for resource-constrained machines)
# Starts services one by one with 30-60s delays between each phase.
bash scripts/start-staggered.sh
```

**Service Status**: Once up, the API Gateway will orchestrate requests to the Auth, Billing, and Inventory microservices under the UUID standard.

**Ops Panel**: ContainerFlow is available at `http://localhost:9470` — visualize container topology, check CPU/RAM metrics, browse logs, execute commands (`docker exec`) directly from the browser, and receive Discord alerts when a container crashes, restarts, or exceeds resource thresholds.

---

### 🚚 Inventory Core — New Capabilities (May 2026)

`siga-inventory` now features full business logic:

| Capability | Endpoint | Stories |
|------------|----------|---------|
| **Consolidated stock view** | `GET /api/v1/inventory/stock/consolidated?productId=X` | US-2.1 |
| **Auto-SKU + duplicate detection** | `POST /api/v1/inventory/products`, `GET /duplicate-check?name=X` | US-2.2 |
| **Smart search** | `GET /api/v1/inventory/products/search?q=X` | US-2.3 |
| **Stock reconciliation** | `POST /api/v1/inventory/stock/reconciliations` | US-2.4 |
| **Warehouse ↔ store transfer** | `POST /api/v1/inventory/stock/transfers` | US-2.5 |
| **Movement history** | `GET /api/v1/inventory/stock/movements` | US-2.5 |

Each capability implemented with **Hexagonal Architecture** (ports/adapters), **Strict TDD** (50+ tests), and SDD specs at `openspec/changes/inventory-core-features/`.

---

## Frontend Ecosystem — SvelteKit Unified

SIGA converges into a unified SvelteKit 5 frontend under `apps/dashboard`, acting as a native BFF (Backend For Frontend) with server-side data composition:

- **Dashboard**: Located in `apps/dashboard`. Unifies the administration, landing, customer portal, and admin portal interfaces into a single SvelteKit 5 application.

*Note: Legacy frontends in `apps/` (webapp, landing, customer-portal, admin-portal, mobile) were declared legacy deprecated as of May 2026. The dashboard is the new unified frontend. All frontends consume the microservices located in `services/` through the API Gateway.*

---

## Governance, Privacy, and Compliance (Law 21.719)

SIGA is not just code; it is a platform designed to be legally unassailable under Chilean data protection regulations:

- **Privacy by Design (Art. 14 quáter)**: The architecture is shielded so that the service provider (SIGA Admin) **DOES NOT** have access to sensitive financial data, billing amounts, or granular details of SME customers.
- **Zero-Knowledge Architecture**: The platform applies the "Blind to Data, Attuned to Flow" principle. It manages infrastructure and availability while respecting the absolute data sovereignty of each business.
- **Pseudonymization (Art. 2, letter l)**: Mandatory use of **UUID v4** across all layers to prevent unauthorized traceability of natural persons.
- **SDD (Spec-Driven Development)**: Every technical change stems from a specification, ensuring that security and privacy are functional requirements, not afterthoughts.
- **Technical Bilingualism**: API documentation and contracts in mirror format (ES/EN) to ensure transparency and scalability.

## License

This project is the private property of **Héctor Aguila**. All rights reserved. The code is provided exclusively for technical review and audit compliance purposes under Law 21.719. See the [LICENSE](LICENSE) file for more details.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
