# Manifiesto Core SIGA

## 1. Visión y Propósito
SIGA (Sistema de Inteligencia y Gestión de Activos) es una solución tecnológica diseñada específicamente para PYMES que operan con uno o varios locales. A diferencia de un sistema administrativo tradicional, SIGA es un **Ecosistema Inteligente** donde la IA es un operador activo.

## 2. Pilares Fundamentales
1.  **Gestión de Activos como Centro:** El inventario es el corazón de SIGA. Todo el sistema gira en torno al control preciso de existencias por local.
2.  **Agentes de IA Operativos:** Los agentes no solo responden preguntas; ejecutan acciones (CRUD) sobre el inventario y realizan análisis de datos, siempre heredando y respetando los privilegios del usuario humano.
3.  **Módulo Ventas (POS) con Propósito:** El módulo de ventas no es un fin en sí mismo, sino la herramienta necesaria para garantizar el descuento de stock en tiempo real sin depender de integraciones externas.
4.  **Arquitectura Multi-Tenant Moderna:** Implementación SaaS real con una base de datos única y aislamiento mediante esquemas por servicio (`siga_auth`, `siga_inventario`, `siga_ventas`, etc.).

## 3. Roles y Flexibilidad (Gobernanza de Permisos)
En las PYMES, las fronteras de los roles son difusas y dinámicas. SIGA implementa un modelo de **Permisos Granulares e Inheredables**:
*   **Herencia IA:** Los Agentes de IA operan bajo el paraguas de seguridad del usuario. Nunca podrán ejecutar una acción (CRUD o Análisis) para la cual el usuario humano no tenga permiso explícito.
*   **Granularidad Dinámica:** El sistema permite añadir o quitar privilegios específicos a medida que la confianza en un empleado evoluciona, permitiendo una gestión de accesos orgánica y no estática.

## 4. Resiliencia y Sistema de Fallback
SIGA está diseñado para no fallar nunca de cara al usuario. Si el servicio de Agentes de IA presenta una caída o latencia excesiva, entra en juego el **Servicio de Fallback**:
*   **Lógica en Base de Datos:** Mediante procedimientos SQL/PL-SQL o servicios de respaldo, el sistema entregará resultados reales (queries tradicionales) envueltos en un mensaje amable.
*   **Continuidad de Negocio:** El chat o la interfaz inteligente siempre devolverá valor, asegurando que el usuario nunca vea un error técnico crudo.

## 5. Modelo de Negocio (SaaS)
El sistema se estructura en planes diferenciados por la capacidad de la IA:
*   **Plan Base:** IA de Análisis (Lectura y sugerencias).
*   **Plan Avanzado:** IA Operativa (Capacidad CRUD y ejecución de acciones de inventario).

---
*Este documento constituye la fuente de verdad para el desarrollo de la arquitectura y la lógica de negocio.*
