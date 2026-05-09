# SIGA Core Manifesto

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../../es/arquitectura/REGLAS_NEGOCIO_CORE.md)*

## 1. Vision and Purpose
SIGA (Intelligence and Asset Management System) is a technological solution designed specifically for SMEs operating with one or multiple branches. Unlike a traditional administrative system, SIGA is an **Intelligent Ecosystem** where AI is an active operator.

## 2. Fundamental Pillars
1.  **Asset Management as the Center:** Inventory is the heart of SIGA. The entire system revolves around precise stock control per location.
2.  **Operational AI Agents:** Agents don't just answer questions; they execute actions (CRUD) on the inventory and perform data analysis, always inheriting and respecting the human user's privileges.
3.  **Purpose-driven Sales Module (POS):** The sales module is not an end in itself, but the necessary tool to guarantee real-time stock deduction without depending on external integrations.
4.  **Modern Multi-Tenant Architecture:** Real SaaS implementation with a single database and isolation through per-service schemas (`siga_auth`, `siga_inventory`, `siga_sales`, etc.).

## 3. Roles and Flexibility (Permissions Governance)
In SMEs, role boundaries are fuzzy and dynamic. SIGA implements a **Granular and Inheritable Permissions** model:
*   **AI Inheritance:** AI Agents operate under the user's security umbrella. They can never execute an action (CRUD or Analysis) for which the human user does not have explicit permission.
*   **Dynamic Granularity:** The system allows specific privileges to be added or removed as an employee's trust evolves, enabling organic rather than static access management.

## 4. Resilience and Fallback System
SIGA is designed to never fail from the user's perspective. If the AI Agent service experiences downtime or excessive latency, the **Fallback Service** comes into play:
*   **Database Logic:** Through SQL/PL-SQL procedures or backup services, the system will deliver real results (traditional queries) wrapped in a friendly message.
*   **Business Continuity:** The chat or intelligent interface will always return value, ensuring the user never sees a raw technical error.

## 5. Business Model (SaaS)
The system is structured in plans differentiated by AI capability:
*   **Base Plan:** Analysis AI (Read-only and suggestions).
*   **Advanced Plan:** Operational AI (CRUD capability and inventory action execution).

---
*This document constitutes the source of truth for the development of the architecture and business logic.*
