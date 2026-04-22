<p align="center">
  <img src="docs/brand/Logo_SIGA.png" alt="SIGA Logo" width="220" />
</p>

# SIGA: Intelligent Asset Management System

Welcome to **SIGA**, an intelligent technological solution specifically designed for **SMEs** (from 1 to N locations). SIGA focuses on **Asset Management (Inventory)** as the business core, powered by operational **AI Agents**.

The project has evolved into a **Multi-tenant Microservices Architecture**, where each service operates within its own database schema, ensuring isolation and scalability under a SaaS model.

## System Architecture (V2)

SIGA is built on a resilient and scalable microservices ecosystem:

### Core Microservices (Backend)
- **Service Registry (`siga-eureka`)**: The heart of service discovery.
- **API Gateway (`siga-gateway`)**: The single public entry point, responsible for routing and load balancing.
- **Auth Service (`siga-auth`)**: Issue and validation of corporate JWT tokens.
- **Inventory Service (`siga-inventory`)**: The system core. Management of products, categories, and stock per store.
- **Sales Service (`siga-sales`)**: POS module designed to ensure precise and autonomous stock deduction per store.
- **AI Agent (`siga-agent`)**: Operational intelligence engine. Assists users with analytics and CRUD executions based on their privileges.
- **Billing Service (`siga-billing`)**: Management of commercial transactions and invoices.

### User Interfaces
- **Webapp V2 (`/services/webapp`)**: Administration console built with **Svelte 5 / SvelteKit**. Features a high-fidelity *Void/Glassmorphism* premium design system.
- **Mobile (`/services/mobile`)**: Application for field operators (Android / Jetpack Compose).
- **Commercial Portal (`/services/comercial`)**: B2B Storefront and Landing page.

## Deployment & Quick Start

The entire backend stack is orchestrated via Docker. To start the development environment with isolated database emulation (Database-per-Service concept in local schema):

```bash
# 1. Start backend infrastructure
docker-compose up -d

# 2. Start the Premium V2 Frontend
cd services/webapp
npm install
npm run dev
```

> **Quick Access UI**: The frontend will be exposed at `http://localhost:5173`. For quick academic demos, use the configured access: `admin@siga.cl` / `admin`.

## Governance Rules

This project applies strict regulations (Spec-Driven Development) to ensure continuous value delivery:
- **SDD Traceability**: All architectural changes must originate from an approved `proposal` registered in the *Engram* system.
- **Commits**: Conventional, ALWAYS in English, descriptive, and justified (e.g., `feat(webapp): premium v2 redesign and a11y improvements`).
- **Security**: Progressive adoption of privacy standards required by **Chilean Law 21.719**.

---
> *A Dreamer with little RAM & Misael*
