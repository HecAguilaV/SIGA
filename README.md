# SIGA: Sistema Inteligente de Gestión de Activos

*Read this in other languages: [English](README.en.md)*

Bienvenido a **SIGA**, un ecosistema inteligente diseñado para la Gestión de Activos e Inventarios en Pymes multi-sucursal. A diferencia de un ERP tradicional, SIGA integra Agentes de IA Operativos que actúan como co-pilotos en la administración del negocio.

Este repositorio es un Monorepo de Microservicios con arquitectura Zero-Trust y aislamiento de datos por esquema (Database-per-service).

---

## Bóveda Documental (Simetría Bilingüe)

Hemos establecido un estándar de Espejo Semántico para garantizar que la documentación esté siempre disponible y actualizada en ambos idiomas.

| Sección | Castellano (Nativo) | English (Mirror) |
| :--- | :--- | :--- |
| **Arquitectura** | [Modelo C4 L1/L2](docs/es/arquitectura/MODELO_C4.md) | [C4 Model L1/L2](docs/en/architecture/C4_MODEL.md) |
| **Seguridad** | [Manifiesto de Seguridad](docs/es/security/MANIFIESTO_SEGURIDAD.md) | [Security Manifesto](docs/en/security/SECURITY_MANIFESTO.md) |
| **Pruebas y Calidad** | [Estrategia de Tests](docs/tests/es/README.md) | [Testing Strategy](docs/tests/en/README.md) |
| **Negocio** | [Reglas de Negocio Core](docs/es/arquitectura/REGLAS_NEGOCIO_CORE.md) | [Core Business Rules](docs/en/architecture/CORE_BUSINESS.md) |
| **Testing/APIs** | [Colección Postman](docs/es/api/siga-apis.postman_collection.json) | [Postman Collection](docs/en/api/siga-apis.postman_collection.json) |
| **Front-end** | [Pacto de Frontend](docs/es/arquitectura/PACTO_FRONTEND.md) | [Frontend Pact](docs/en/architecture/FRONTEND_PACT.md) |

---

## Stack Tecnológico (V2)

SIGA utiliza tecnologías de vanguardia para asegurar el rendimiento y el cumplimiento legal:

- **Backend**: Kotlin + Spring Boot 4.0.6. Implementación estricta de Arquitectura Hexagonal (Gold Standard) y Disciplina TDD.
- **IA**: Python + LangChain + PGVector (Memoria Semántica por Tenant).
- **Frontend**: Svelte 5 (Webapp) y Jetpack Compose (Mobile).
- **Persistencia**: PostgreSQL con aislamiento de esquemas y UUID v4 como estándar único de identidad y seudonimización.
- **Seguridad**: JWT (Stateless) y cumplimiento riguroso con la Ley Chilena 21.719.

---

## Despegue Rápido (Backend)

La infraestructura de SIGA está completamente dockerizada para garantizar un entorno de desarrollo consistente.

```bash
# Levantar infraestructura completa (Base de Datos + Microservicios)
docker-compose up -d
```

**Estado de Servicios**: Una vez arriba, la API Gateway orquestará las peticiones hacia los microservicios de Auth, Billing e Inventory bajo el estándar UUID.

---

## Ecosistema Frontend (En Desarrollo)

SIGA contempla múltiples interfaces organizadas bajo el directorio `apps/` para una clara separación de concerns:

- **Webapp (Administración)**: Localizada en `apps/webapp`. Basada en Svelte 5.
- **Página de Inicio (Landing)**: Localizada en `apps/landing`.
- **Portal de Clientes**: Localizada en `apps/customer-portal`.
- **Portal de Administración**: Localizada en `apps/admin-portal`.
- **Mobile App**: Localizada en `apps/mobile`. Basada en Jetpack Compose.

*Nota: Los frontends son consumidores de los microservicios ubicados en `services/`.*

---

## Gobernanza, Privacidad y Cumplimiento (Ley 21.719)

SIGA no es solo código; es una plataforma diseñada para ser legalmente inexpugnable bajo la normativa chilena de protección de datos:

- **Privacidad por Diseño (Art. 14 quáter)**: La arquitectura está blindada para que el proveedor del servicio (SIGA Admin) **NO** tenga acceso a datos financieros sensibles, montos de facturación ni detalles granulares de los clientes de las Pymes.
- **Zero-Knowledge Architecture**: Aplicamos el principio de "Ceguera al Dato, Atención al Flujo". Gestionamos la infraestructura y la disponibilidad, pero respetamos la soberanía absoluta de los datos de cada negocio.
- **Seudonimización (Art. 2, letra l)**: Uso mandatorio de **UUID v4** en todas las capas para evitar la trazabilidad no autorizada de personas naturales.
- **SDD (Spec-Driven Development)**: Cada cambio técnico nace de una especificación, asegurando que la seguridad y la privacidad sean requisitos funcionales, no añadidos posteriores.
- **Bilingüismo Técnico**: Documentación y contratos de API en espejo (ES/EN) para asegurar transparencia y escalabilidad.

## Licencia

Este proyecto es propiedad privada de **Héctor Aguila**. Todos los derechos están reservados. El código se proporciona exclusivamente para fines de revisión técnica y cumplimiento de auditoría bajo la Ley 21.719. Consulte el archivo [LICENSE](LICENSE) para más detalles.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`
