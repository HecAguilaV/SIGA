<p align="center">
  <img src="docs/brand/Logo_SIGA.png" alt="SIGA Logo" width="220" />
</p>

# SIGA: Intelligent Asset Management System

*Leer en otros idiomas: [Español](README.md).*

Welcome to **SIGA**, an intelligent technological solution specifically designed for **SMEs** (from 1 to N locations). SIGA focuses on **Asset Management (Inventory)** as the business core, powered by operational **AI Agents**.

The project is structured as a **Multi-tenant Microservices Architecture within a Monorepo**. Each service operates inside its own database schema (`database-per-service`), ensuring isolation, resilience, and scalability under a SaaS model.

## Educational Documentation

Given the rapid technical advancement of the project with the help of AI agents, we have compiled an academic document detailing the structure, flow, and architecture design.
> **[Read the Learning Journal (Spanish)](docs/es/learning/LEARNING.md)**
> **[View C4 Architecture Model (Level 1 & 2)](docs/en/architecture/C4_MODEL.md)**

## System Architecture (V2)

SIGA is built on a robust microservices ecosystem (Java/Kotlin + Spring Boot):

### Core Microservices (Backend)
- **Service Registry (`siga-eureka`)**: The heart of service discovery.
- **API Gateway (`siga-gateway`)**: The single public entry point, responsible for routing and load balancing.
- **Auth Service (`siga-auth`)**: Issue and validation of corporate JWT tokens.
- **Inventory Service (`siga-inventory`)**: The system core. Management of products, categories, and stock per store.
- **Sales Service (`siga-sales`)**: POS module designed to ensure precise and autonomous stock deduction per store.
- **AI Agent (`siga-agent`)**: Operational intelligence engine. Assists users with analytics and CRUD executions.
- **Billing Service (`siga-billing`)**: Management of commercial transactions and invoices.

### User Interfaces
- **Webapp V2 (`/services/webapp`)**: Administration console built with **Svelte 5 / SvelteKit**. Features a high-fidelity *Void/Glassmorphism* premium design system.
- **Mobile (`/services/mobile`)**: Application for field operators (Android / Jetpack Compose).
- **Commercial Portal (`/services/comercial`)**: B2B Storefront and Landing page.

## Deployment & Quick Start

The entire backend stack is orchestrated via Docker, and the lifecycle (CI/CD) is delegated to GitHub Actions to keep the development environment agile and free of excessive compute load.

```bash
# 1. Start backend infrastructure (Database and Microservices from Docker Hub)
docker-compose up -d

# 2. Start the Premium V2 Frontend
cd services/webapp
npm install
npm run dev
```

> **Quick Access UI**: The frontend will be exposed at `http://localhost:5173`. For quick academic demos, use: `admin@siga.cl` / `admin`.

## Governance Rules

This project applies strict engineering regulations (Spec-Driven Development):

- **SDD Traceability**: All architectural changes must originate from an approved `proposal` registered in the *Engram* memory system.
- **Commits in Spanish**: All commit messages in the Git repository must be **STRICTLY in Spanish** (following the Conventional Commits format).
- **Security & Privacy**: Adoption of privacy standards required by **Chilean Law 21.719** through the cross-cutting auditing module (`siga-common`).

---
> A Dreamer with little RAM 🧑‍💻
