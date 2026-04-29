# Proposal: Arquitectura de Microservicios SIGA v2.1 - Alineación Core

## Intent
Normalizar la arquitectura de microservicios tras la migración a multi-tenant, eliminando el acoplamiento con el esquema legado `siga_saas` y estableciendo a los Agentes de IA como ciudadanos de primera clase con gobernanza de permisos heredable.

## Scope

### In Scope
- **Normalización de Esquemas**: Migración total de entidades JPA a los 5 esquemas dedicados (`siga_auth`, `siga_inventario`, `siga_ventas`, `siga_comercial`, `siga_agente`).
- **Agentes de IA Operativos**: Implementación de lógica CRUD en el Agente heredando privilegios del usuario humano.
- **Resiliencia (Fallback)**: Creación de un servicio de respaldo que ejecute consultas SQL/PL-SQL directas cuando el motor de IA falle.
- **Gobernanza de Permisos**: Modelo granular y dinámico que permita la evolución de roles en las PYMES.

### Out of Scope
- Migración de la lógica de facturación electrónica (se mantiene en `backend` legacy temporalmente).
- Rediseño de la UI Mobile (solo alineación de APIs).

## Capabilities
- `siga-auth`: Gestión de identidades y permisos granulares heredables.
- `siga-inventario`: Gestión inteligente de activos con alertas de stock.
- `siga-ventas`: POS integrado para control de stock local en tiempo real.
- `siga-agente`: Ejecución de acciones CRUD mediante procesamiento de lenguaje natural.
- `siga-fallback`: Orquestador de resiliencia para fallos del Agente IA.

## Affected Areas
- `services/auth/` (Permisos heredables)
- `services/inventario/` (Normalización de tablas)
- `services/ventas/` (Normalización de transacciones)
- `services/fallback/` [NUEVO]
- `docs/CORE_BUSINESS.md` (Fuente de verdad)

## Risks & Mitigations
- **Error en la Inferencia de IA**: Mitigado mediante el nuevo Servicio de Fallback.
- **Complejidad en Permisos**: Mitigado mediante un sistema de herencia dinámica (Usuario -> Agente).
