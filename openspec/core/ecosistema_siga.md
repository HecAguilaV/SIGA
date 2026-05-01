# Visión del Ecosistema SIGA

## 1. El Propósito
SIGA nace para solucionar el desfase de inventario en terreno. No es un ERP de escritorio; es una herramienta de movilidad para el "Guerrero Multi-rol".

## 2. Componentes del Sistema

###  SIGA-SERVICES (El Ecosistema)
- **Tecnología:** Kotlin + Spring Boot / Python (Agent).
- **Base de Datos:** PostgreSQL Independientes (PostGIS y PGVector).
- **Patrón:** Microservicios con soberanía de datos y comunicación vía Gateway.
- **IA:** Agentes operativos con búsqueda vectorial descentralizada.

###  FRONTENDS (La Interfaz)
- **Tecnología:** Svelte 5 / React / Kotlin Multiplatform.
- **customer-portal:** Gestión de suscripción y entrada al ecosistema vía SSO.
- **webapp:** Administración operativa de la pyme.
- **mobile:** Herramienta de terreno para el Guerrero Multi-rol.

## 3. Principios Arquitectónicos
- **SOLID:** Aplicado especialmente en la Separación de Responsabilidades.
- **Seguridad:** JWT con Refresh Token Interceptor.
- **Escalabilidad:** Diseñado para ser migrado a microservicios satélites si la carga lo requiere.
