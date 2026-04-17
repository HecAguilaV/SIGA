<p align="center">
  <img src="docs/brand/Logo_SIGA.png" alt="Logo SIGA" width="220" />
</p>

# SIGA: Sistema Inteligente de Gestión de Activos

Bienvenido a **SIGA**, una plataforma empresarial de grado productivo para la gestión de inventarios y activos físicos. 

Recientemente el proyecto ha evolucionado de un monolito a una **Arquitectura de Microservicios Distribuidos**, soportada por inteligencia artificial.

## Arquitectura del Sistema (V2)

SIGA está construido sobre un ecosistema de microservicios resiliente y escalable:

### Microservicios Core (Backend)
- **Service Registry (`siga-eureka`)**: El corazón del descubrimiento de servicios.
- **API Gateway (`siga-gateway`)**: El único punto de entrada público, encargado de enrutamiento y balanceo.
- **Auth Service (`siga-auth`)**: Emisión y validación de tokens corporativos.
- **Microservicio Inventario (`siga-inventario`)**: Dominio transaccional de productos, categorías y stock.
- **Microservicio Ventas (`siga-ventas`)**: Motor de transacciones de salida y facturación.
- **Agente IA (`siga-agente`)**: Microservicio Polyglot (Python + FastAPI) que sirve como motor de análisis y GenAI.

### Interfaces de Usuario
- **Webapp V2 (`/services/webapp`)**: Consola de administración construida en **Svelte 5 / SvelteKit**. Recientemente rediseñada con un sistema de diseño *Void/Glassmorphism* premium de alta fidelidad.
- **Mobile (`/services/mobile`)**: Aplicación para operarios en terreno (Android / Jetpack Compose).
- **Portal Comercial (`/services/comercial`)**: B2B Storefront y Landing web.

## Despliegue y Ejecución Rápida

Todo el stack de backend está orquestado mediante Docker. Para levantar el entorno de desarrollo localizado con emulación de base de datos aislada (Database-per-Service concept en esquema local):

```bash
# 1. Levantar infraestuctura backend
docker-compose up -d

# 2. Levantar el Frontend V2 Premium
cd services/webapp
pnpm install
pnpm dev
```

> **UI Acceso Rápido**: El frontend se expondrá en `http://localhost:5174`. Para demos académicas rápidas, utiliza el acceso configurado: `admin@siga.cl` / `admin`.

## Reglas de Gobernanza

Este proyecto aplica normativas estrictas (Spec-Driven Development) para asegurar la entrega continua de valor:
- **Trazabilidad SDD**: Todos los cambios arquitectónicos deben partir de una `proposal` aprobada y registrada en el sistema *Engram*.
- **Commits**: Convencionales, SIEMPRE en español, descriptivos y justificados (ej: `feat(webapp): rediseño premium v2 y mejoras a11y`).
- **Seguridad**: Adopción progresiva de los estándares de privaticidad requeridos por la **Ley Chilena 21.719**.

---
> *Un Soñador con Poca RAM & Misael*
