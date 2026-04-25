<p align="center">
  <img src="docs/brand/Logo_SIGA.png" alt="Logo SIGA" width="220" />
</p>

# SIGA: Sistema Inteligente de Gestión de Activos

*Read this in other languages: [English](README.en.md).*

Bienvenido a **SIGA**, una solución tecnológica inteligente diseñada específicamente para **Pymes** (desde 1 a N sucursales). El núcleo operativo de SIGA es la **Gestión de Activos (Inventario)** impulsado por **Agentes de Inteligencia Artificial**.

Este proyecto es una **Arquitectura de Microservicios Multi-tenant alojada en un Monorepo**. Cada servicio opera dentro de su propio esquema de base de datos (`database-per-service`), asegurando aislamiento, resiliencia y escalabilidad bajo un modelo SaaS puro.

## Documentación Educativa

Dado el rápido avance técnico del proyecto con la ayuda de agentes de IA, hemos compilado un documento académico que detalla la estructura, flujo y diseño de arquitectura.
> **[Leer el Manual Docente de Arquitectura](docs/MANUAL_DOCENTE.md)**

## Arquitectura del Sistema (V2)

SIGA está construido sobre un ecosistema de microservicios robusto (Java/Kotlin + Spring Boot):

### Microservicios Core (Backend)
- **Service Registry (`siga-eureka`)**: El corazón del descubrimiento de servicios.
- **API Gateway (`siga-gateway`)**: El único punto de entrada público, responsable del enrutamiento.
- **Auth Service (`siga-auth`)**: Emisión y validación de tokens corporativos JWT.
- **Inventory Service (`siga-inventory`)**: El núcleo del sistema. Gestión de productos, categorías y stock por tienda.
- **Sales Service (`siga-sales`)**: Módulo POS (Punto de Venta) diseñado para la deducción precisa de stock por sucursal.
- **AI Agent (`siga-agent`)**: Motor de inteligencia operativa. Asiste a los usuarios con analíticas y automatizaciones.
- **Billing Service (`siga-billing`)**: Gestión de transacciones comerciales y facturas.

### Interfaces de Usuario
- **Webapp V2 (`/services/webapp`)**: Consola de administración construida con **Svelte 5 / SvelteKit**. Cuenta con un sistema de diseño premium (*Void/Glassmorphism*).
- **Mobile (`/services/mobile`)**: Aplicación para operadores en terreno (Android / Jetpack Compose).
- **Commercial Portal (`/services/comercial`)**: Escaparate B2B y página de aterrizaje (Landing Page).

## Despliegue y Arranque Rápido

Todo el backend está orquestado con Docker, y el ciclo de vida (CI/CD) está delegado en GitHub Actions para mantener el entorno de desarrollo ágil y libre de carga computacional excesiva.

```bash
# 1. Levantar la infraestructura del backend (Base de datos y Microservicios desde Docker Hub)
docker-compose up -d

# 2. Levantar el Frontend V2
cd services/webapp
npm install
npm run dev
```

> **Acceso a la UI**: El frontend se expondrá en `http://localhost:5173`. Para demostraciones académicas, utilice: `admin@siga.cl` / `admin`.

## Gobernanza y Reglas de Desarrollo

Este proyecto aplica estrictas normas de ingeniería (Spec-Driven Development):

- **Trazabilidad SDD**: Todos los cambios arquitectónicos deben originarse a partir de una `propuesta` aprobada y registrada en el sistema de memoria *Engram*.
- **Commits en Español**: Todos los mensajes de commit en el repositorio de Git deben estar **ESTRICTAMENTE en Español** (siguiendo el formato de Conventional Commits, ej: `feat(webapp): rediseño premium v2 y mejoras de accesibilidad`).
- **Seguridad y Privacidad**: Adopción de normas de privacidad requeridas por la **Ley Chilena 21.719** a través del módulo transversal de auditoría (`siga-common`).

---
> *Un soñador con poca RAM & Misael*
