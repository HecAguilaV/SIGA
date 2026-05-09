# SIGA WebApp (Operating System)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

**The operational heart of the SIGA ecosystem.**
Inventory management, sales, and AI-powered decision making.

[Live Demo](https://siga-webapp.vercel.app)

---

## Description
SIGA WebApp is not a simple CRUD. It is a **simplified ERP** designed for the daily operation of commercial establishments.
It integrates natively with **SIGA WebComercial** (for subscription management) and **SIGA Mobile App** (for field operations), sharing the same Backend and Database.

## Key Features
* **Real-Time Inventory Management**: Immediate sync with mobile app and backend.
* **Multimodal AI Assistant**:
    * **Voice and Text**: Interact naturally ("How much stock of X is left?", "Add 5 units").
    * **Business Context**: The AI knows your inventory and business rules.
* **Roles and Permissions**:
    * **Administrator**: Full view, management of operational users and locations.
    * **Operator**: Limited access to inventory and daily movements.
* **Single Sign-On (SSO)**: Seamless access from the commercial portal via JWT tokens.
* **Analytics Dashboard**: Key performance metrics and low-stock alerts.

## Tech Stack
* **Frontend**: SvelteKit 5 (Vite)
* **Styles**: Bulma CSS + Phosphor Icons
* **Backend**: Spring Boot (REST API)
* **Database**: PostgreSQL (AlwaysData)
* **AI**: Google Gemini 1.5 Pro (Integrated via Backend)

## Installation and Deployment

### Requirements
* Node.js 20+
* Internet access (to connect with Railway Backend)

### Steps
1.  **Clone repository**:
    ```bash
    git clone https://github.com/HecAguilaV/SIGA_WEBAPP.git
    cd SIGA_WEBAPP
    ```

2.  **Install dependencies**:
    ```bash
    npm install
    ```

3.  **Run in development**:
    ```bash
    npm run dev
    ```
    > The system will start at **http://localhost:5174** (Port configured to avoid conflict with WebComercial on 5173).

### Proxy Configuration
The project includes a proxy in `vite.config.js` to redirect `/api` requests automatically to the production backend on Railway, enabling local development without CORS issues.

## Test Credentials
The system uses the same login as the rest of the ecosystem:

| Role | Email | Password |
|------|-------|----------|
| **Administrator** | `admin@test.cl` | `test123` |
| **Operator** | `oper@test.cl` | `test123` |

## Project Structure
```
src/
├── lib/
│   ├── components/    # Reusable UI (Tables, Modals, Assistant)
│   ├── services/      # Business logic and API calls
│   └── stores/        # State management (Svelte Stores)
├── routes/
│   ├── +layout.svelte # Main layout (Sidebar, Navbar)
│   ├── dashboard/     # Main metrics view
│   ├── inventario/    # Product and stock management
│   ├── locales/       # Branch CRUD
│   └── sso/           # Landing page for WebComercial authentication
└── app.html           # HTML entry point
```

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
