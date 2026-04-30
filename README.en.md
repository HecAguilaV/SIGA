# SIGA: Intelligent Asset Management System

*Read this in other languages: [Español](README.md)*

Welcome to **SIGA**, an intelligent ecosystem designed for Asset and Inventory Management in multi-branch SMEs. Unlike traditional ERPs, SIGA integrates Operational AI Agents that act as co-pilots in business administration.

This repository is a Microservices Monorepo featuring Zero-Trust architecture and schema-level data isolation (Database-per-service).

---

## Documentation Vault (Bilingual Symmetry)

We have established a Semantic Mirroring standard to ensure that documentation is always available and updated in both languages.

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

- **Backend**: Java/Kotlin + Spring Boot 3.2.x. Strict implementation of Hexagonal Architecture and TDD Discipline.
- **AI**: Python + LangChain + PGVector (Per-Tenant Semantic Memory).
- **Frontend**: Svelte 5 (Webapp) and Jetpack Compose (Mobile).
- **Persistence**: PostgreSQL with schema isolation and UUID v4 as the unique standard for identity and pseudonymization.
- **Security**: JWT (Stateless) and rigorous compliance with Chilean Law 21.719.

---

## Quick Start (Backend)

SIGA's infrastructure is fully dockerized to ensure a consistent development environment.

```bash
# Spin up full infrastructure (Database + Microservices)
docker-compose up -d
```

**Service Status**: Once up, the API Gateway will orchestrate requests to the Auth, Billing, and Inventory microservices under the UUID standard.

---

## Frontend Ecosystem (Under Development)

SIGA contemplates multiple interfaces that are currently in the initialization phase:

- **Webapp (Administration)**: Located in `services/webapp`. Based on Svelte 5.
- **Landing Page**: Located in `services/landing`.
- **Mobile App**: Located in `services/mobile`. Based on Jetpack Compose.

*Note: These services will be integrated with the bilingual microservices core in the next project phases.*

---

## Governance and Compliance

This project is not just code; it is a legally responsible implementation:
- **Privacy by Design**: Compliance with Art. 14 quáter of Law 21.719.
- **SDD (Spec-Driven Development)**: Every change is traceable and originates from a technical spec.
- **Bilingual Standard**: API documentation and contracts in mirror format (ES/EN) for international scalability.

---
*SIGA - Intelligent Asset Management System*
