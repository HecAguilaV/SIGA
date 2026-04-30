<p align="center">
  <img src="docs/brand/Logo_SIGA.png" alt="Logo SIGA" width="220" />
</p>

# SIGA: Sistema Inteligente de Gestión de Activos

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](README.en.md)*

Bienvenido a **SIGA**, un ecosistema inteligente diseñado para la **Gestión de Activos e Inventarios** en Pymes multi-sucursal. A diferencia de un ERP tradicional, SIGA integra **Agentes de IA Operativos** que actúan como co-pilotos en la administración del negocio.

Este repositorio es un **Monorepo de Microservicios** con arquitectura **Zero-Trust** y aislamiento de datos por esquema (**Database-per-service**).

---

## 📚 Bóveda Documental (Simetría Bilingüe)

Hemos establecido un estándar de **Espejo Semántico** para garantizar que la documentación esté siempre disponible y actualizada en ambos idiomas.

| Sección | 🇪🇸 Castellano (Nativo) | 🇺🇸 English (Mirror) |
| :--- | :--- | :--- |
| **Arquitectura** | [Modelo C4 L1/L2](docs/es/arquitectura/MODELO_C4.md) | [C4 Model L1/L2](docs/en/architecture/C4_MODEL.md) |
| **Seguridad** | [Manifiesto de Seguridad](docs/es/security/MANIFIESTO_SEGURIDAD.md) | [Security Manifesto](docs/en/security/SECURITY_MANIFESTO.md) |
| **Negocio** | [Reglas de Negocio Core](docs/es/arquitectura/REGLAS_NEGOCIO_CORE.md) | [Core Business Rules](docs/en/architecture/CORE_BUSINESS.md) |
| **Testing/APIs** | [Colección Postman](docs/es/api/siga-apis.postman_collection.json) | [Postman Collection](docs/en/api/siga-apis.postman_collection.json) |
| **Front-end** | [Pacto de Frontend](docs/es/arquitectura/PACTO_FRONTEND.md) | [Frontend Pact](docs/en/architecture/FRONTEND_PACT.md) |


---

## 🛠️ Stack Tecnológico (V2)

SIGA utiliza tecnologías de vanguardia para asegurar el rendimiento y la legalidad:

- **Backend**: Java/Kotlin + Spring Boot 3.2.x (Arquitectura Hexagonal).
- **IA**: Python + LangChain + PGVector (Memoria Semántica por Tenant).
- **Frontend**: Svelte 5 (Webapp) & Jetpack Compose (Mobile).
- **Persistencia**: PostgreSQL con aislamiento de esquemas y **UUID v4** como estándar de seudonimización.
- **Seguridad**: JWT (Stateless) y cumplimiento estricto con la **Ley Chilena 21.719**.

---

## 🚀 Despegue Rápido

El entorno está 100% dockerizado para un inicio inmediato.

```bash
# 1. Levantar infraestructura completa (DB + Microservicios)
docker-compose up -d

# 2. Iniciar Webapp de Administración
cd services/webapp
pnpm install && pnpm dev
```

**Credenciales de Demo**: `admin@siga.cl` / `admin` (Tenant Alpha).

---

## ⚖️ Gobernanza y Compliance

Este proyecto no solo es código, es una implementación legalmente responsable:
- **Privacy by Design**: Cumplimiento del Art. 14 quáter de la Ley 21.719.
- **SDD (Spec-Driven Development)**: Cada cambio es trazable y nace de una especificación técnica.
- **Commits Bilingües**: Estándar internacional para equipos distribuidos.

---
> Un Soñador con poca RAM 🧑‍💻
