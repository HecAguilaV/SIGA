# Manifiesto Core SIGA

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../../en/architecture/CORE_BUSINESS.md)*

## 1. Visión y Propósito
SIGA (Sistema de Inteligencia y Gestión de Activos) es una solución tecnológica diseñada específicamente para PYMES que operan con uno o varios locales. A diferencia de un sistema administrativo tradicional, SIGA es un **Ecosistema Inteligente** donde la IA es un operador activo.

## 2. Pilares Fundamentales
1.  **Gestión de Activos como Centro:** El inventario es el corazón de SIGA. Todo el sistema gira en torno al control preciso de existencias por local.
2.  **Agentes de IA Operativos:** Los agentes no solo responden preguntas; ejecutan acciones (CRUD) sobre el inventario y realizan análisis de datos, siempre heredando y respetando los privilegios del usuario humano.
3.  **Módulo Ventas (POS) con Propósito:** El módulo de ventas no es un fin en sí mismo, sino la herramienta necesaria para garantizar el descuento de stock en tiempo real sin depender de integraciones externas.
4. **Arquitectura de Microservicios Soberanos:** Implementación SaaS real con una base de datos independiente por servicio (`siga_auth`, `siga_inventory`, `siga_sales`, `siga_billing`, `siga_agent`). El aislamiento es total a nivel de infraestructura.

## 3. Frontends y Puntos de Entrada
*   **dashboard:** Centro operativo para la gestión diaria de la pyme (Inventario, Ventas, Reportes, Agente IA).
*   **customer-portal:** Puerta de entrada para el dueño de la pyme (SaaS). Gestión de suscripciones, pagos y acceso SSO al dashboard.
*   **admin-portal:** Panel de administración interna de la plataforma SIGA.
*   **landing:** Sitio público de presentación del producto.
*   **pos:** Terminal de punto de venta para operaciones en tienda.
*   **mobile:** Herramienta de ejecución rápida para el personal de terreno *(etapa futura)*.

## 4. Roles y Flexibilidad (Gobernanza de Permisos)
En las PYMES, las fronteras de los roles son difusas y dinámicas. SIGA implementa un modelo de **Permisos Granulares e Inheredables**:
*   **Herencia IA:** Los Agentes de IA operan bajo el paraguas de seguridad del usuario. Nunca podrán ejecutar una acción (CRUD o Análisis) para la cual el usuario humano no tenga permiso explícito.
*   **Granularidad Dinámica:** El sistema permite añadir o quitar privilegios específicos a medida que la confianza en un empleado evoluciona, permitiendo una gestión de accesos orgánica y no estática.

## 5. Modelo de Negocio (SaaS)
El sistema se factura a través del **customer-portal** bajo un modelo de suscripción gestionado por el microservicio `siga-billing`. Los planes se diferencian por la capacidad de la IA:
*   **Plan Base:** IA de Análisis (Lectura y sugerencias).
*   **Plan Avanzado:** IA Operativa (Capacidad CRUD y ejecución de acciones de inventario).

---
*Este documento constituye la fuente de verdad para el desarrollo de la arquitectura y la lógica de negocio.*
