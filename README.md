# SIGA WebApp (Sistema Operativo)

**El corazón operativo del ecosistema SIGA.**  
Gestión de inventario, ventas y toma de decisiones potenciada por IA.

[Live Demo](https://siga-webapp.vercel.app)

---

## Descripción
SIGA WebApp no es un simple CRUD. Es un **Sistema de Planificación de Recursos (ERP) simplificado** diseñado para la operación diaria de locales comerciales.
Se integra nativamente con **SIGA WebComercial** (para gestión de suscripciones) y **SIGA App Móvil** (para operaciones en terreno), compartiendo el mismo Backend y Base de Datos.

## Características Clave
*   **Gestión de Inventario Real-Time**: Sincronización inmediata con app móvil y backend.
*   **Asistente IA Multimodal**:
    *   **Voz y Texto**: Interactúa naturalmente ("¿Cuánto stock queda de X?", "Agrega 5 unidades").
    *   **Contexto de Negocio**: La IA conoce tu inventario y reglas de negocio.
*   **Roles y Permisos**:
    *   **Administrador**: Visión total, gestión de usuarios operativos y locales.
    *   **Operador**: Acceso limitado a inventario y movimientos diarios.
*   **Single Sign-On (SSO)**: Acceso fluido desde el portal comercial mediante Tokens JWT.
*   **Dashboard Analítico**: Métricas clave de rendimiento y alertas de stock bajo.

## Stack Tecnológico
*   **Frontend**: SvelteKit 5 (Vite)
*   **Estilos**: Bulma CSS + Phosphor Icons
*   **Backend**: Spring Boot (API REST)
*   **Base de Datos**: PostgreSQL (AlwaysData)
*   **IA**: Google Gemini 1.5 Pro (Integrado vía Backend)

## Instalación y Despliegue

### Requisitos
*   Node.js 20+
*   Acceso a Internet (para conectar con Backend Railway)

### Pasos
1.  **Clonar repositorio**:
    ```bash
    git clone https://github.com/HecAguilaV/SIGA_WEBAPP.git
    cd SIGA_WEBAPP
    ```

2.  **Instalar dependencias**:
    ```bash
    npm install
    ```

3.  **Ejecutar en desarrollo**:
    ```bash
    npm run dev
    ```
    > El sistema se iniciará en **http://localhost:5174** (Puerto configurado para no chocar con WebComercial en 5173).

### Configuración de Proxy
El proyecto incluye un proxy en `vite.config.js` para redirigir las peticiones `/api` automáticamente al backend de producción en Railway, facilitando el desarrollo local sin CORS.

## Credenciales de Prueba
El sistema utiliza el mismo login que el resto del ecosistema:

| Rol | Email | Password |
|-----|-------|----------|
| **Administrador** | `admin@test.cl` | `test123` |
| **Operador** | `oper@test.cl` | `test123` |

## Estructura del Proyecto
```
src/
├── lib/
│   ├── components/    # UI Reutilizable (Tablas, Modales, Asistente)
│   ├── services/      # Lógica de negocio y llamadas API
│   └── stores/        # Gestión de estado (Svelte Stores)
├── routes/
│   ├── +layout.svelte # Layout principal (Sidebar, Navbar)
│   ├── dashboard/     # Vista principal de métricas
│   ├── inventario/    # Gestión de productos y stock
│   ├── locales/       # CRUD de sucursales
│   └── sso/           # Landing page para autenticación desde WebComercial
└── app.html           # Punto de entrada HTML
```

---

## Autor

> **Héctor Aguila**  
>> Un Soñador con Poca RAM