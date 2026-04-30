<p align="center">
  <img src="docs/brand/Logo_SIGA.png" alt="SIGA Logo" width="220" />
</p>

# SIGA: Intelligent Asset Management System

*Read this in other languages: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](README.md)*

Welcome to **SIGA**, an intelligent ecosystem designed for **Asset and Inventory Management** in multi-branch SMEs. Unlike traditional ERPs, SIGA integrates **Operational AI Agents** that act as co-pilots in business administration.

This repository is a **Microservices Monorepo** featuring **Zero-Trust** architecture and schema-level data isolation (**Database-per-service**).

---

## 📚 Documentation Vault (Bilingual Symmetry)

We have established a **Semantic Mirroring** standard to ensure that documentation is always available and updated in both languages.

| Section | 🇪🇸 Spanish (Native) | 🇺🇸 English (Mirror) |
| :--- | :--- | :--- |
| **Architecture** | [C4 Model L1/L2](docs/es/arquitectura/MODELO_C4.md) | [C4 Model L1/L2](docs/en/architecture/C4_MODEL.md) |
| **Security** | [Security Manifesto](docs/es/security/MANIFIESTO_SEGURIDAD.md) | [Security Manifesto](docs/en/security/SECURITY_MANIFESTO.md) |
| **Business** | [Core Business Rules](docs/es/arquitectura/REGLAS_NEGOCIO_CORE.md) | [Core Business Rules](docs/en/architecture/CORE_BUSINESS.md) |
| **Testing/APIs** | [Postman Collection](docs/es/api/siga-apis.postman_collection.json) | [Postman Collection](docs/en/api/siga-apis.postman_collection.json) |
| **Front-end** | [Frontend Pact](docs/es/arquitectura/PACTO_FRONTEND.md) | [Frontend Pact](docs/en/architecture/FRONTEND_PACT.md) |


---

## 🛠️ Technology Stack (V2)

SIGA utilizes cutting-edge technologies to ensure performance and legal compliance:

- **Backend**: Java/Kotlin + Spring Boot 3.2.x (Hexagonal Architecture).
- **AI**: Python + LangChain + PGVector (Per-Tenant Semantic Memory).
- **Frontend**: Svelte 5 (Webapp) & Jetpack Compose (Mobile).
- **Persistence**: PostgreSQL with schema isolation and **UUID v4** as the pseudonymization standard.
- **Security**: JWT (Stateless) and strict compliance with **Chilean Law 21.719**.

---

## 🚀 Quick Start

The environment is 100% dockerized for an immediate start.

```bash
# 1. Spin up full infrastructure (DB + Microservices)
docker-compose up -d

# 2. Start Admin Webapp
cd services/webapp
pnpm install && pnpm dev
```

**Demo Credentials**: `admin@siga.cl` / `admin` (Tenant Alpha).

---

## ⚖️ Governance and Compliance

This project is not just code; it is a legally responsible implementation:
- **Privacy by Design**: Compliance with Art. 14 quáter of Law 21.719.
- **SDD (Spec-Driven Development)**: Every change is traceable and originates from a technical spec.
- **Bilingual Commits**: International standard for distributed teams.

---
> A Dreamer with little RAM 🧑‍💻
