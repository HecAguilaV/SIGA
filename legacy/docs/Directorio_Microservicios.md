# Directorio de Servicios y Microservicios - SIGA

Este documento detalla la función de cada componente en el directorio `/services`, clasificándolos según su rol en la arquitectura.

---

## 1. Microservicios de Negocio (Backend Core)
Estos son servicios autónomos, con su propio ciclo de vida y base de datos (aislamiento por esquema).

| Directorio | Tecnología | Esquema DB | Funcionalidad |
| :--- | :--- | :--- | :--- |
| `auth` | Kotlin / Spring Boot | `siga_auth` | Gestión de identidades, tokens JWT y gobernanza de permisos granulares. |
| `inventario` | Kotlin / Spring Boot | `siga_inventario` | **Corazón de SIGA**. Gestión de activos, stock por local, categorías y alertas. |
| `ventas` | Kotlin / Spring Boot | `siga_ventas` | Módulo POS. Registra transacciones para garantizar el descuento de stock preciso. |
| `agente` | Python / FastAPI | `siga_agente` | Motor de IA. Análisis de datos y ejecución de acciones CRUD mediante lenguaje natural. |
| `backend` | Kotlin / Spring Boot | `siga_comercial` | Monolito de transición. Maneja el portal comercial, suscripciones y lógica SaaS. |

---

## 2. Infraestructura y Soporte
Servicios que no contienen lógica de negocio directa, sino que permiten que los microservicios funcionen como un ecosistema.

*   **`registry` (Eureka Server):** El directorio de teléfonos del sistema. Cada microservicio se registra aquí para que otros puedan encontrarlo sin conocer su IP.
*   **`gateway` (API Gateway):** La puerta de entrada. Enruta las peticiones del exterior al microservicio correcto y maneja la seguridad inicial.
*   **`common` (Shared Library):** **NO es un microservicio**. Es una librería de código compartido (DTOs comunes, utilidades de auditoría) que los otros servicios de Kotlin importan.

---

## 3. Interfaces de Usuario (Frontend)
No son servicios de backend, sino aplicaciones que consume el usuario final.

*   **`webapp` (SvelteKit):** El dashboard administrativo principal (Premium UI).
*   **`comercial` (React/Vite):** El portal B2B para que las PYMES se registren y compren planes.
*   **`mobile` (Kotlin Multiplatform):** Aplicación nativa para operarios en terreno.

---

## 4. El Módulo de Fallback (Propuesta)
Respecto a tu consulta sobre el servicio de **Fallback**:

### ¿Dónde debería vivir?
Existen dos opciones prolijas:
1.  **Dentro del `gateway`:** Como un filtro que detecta si el `siga-agente` falla y redirige la petición a un servicio de respaldo.
2.  **Como un servicio independiente (`siga-fallback`):** Un microservicio ligero en Kotlin/Java o incluso un módulo de Node.js.

### ¿Necesita un Esquema propio?
**No.** Siguiendo tu visión core:
> *"Para que el chat nunca devuelva un error sin respuesta, solo un mensaje agradable pero con resultados reales."*

Este servicio **no necesita almacenar datos propios**. Su función es ser un "Orquestador de Resiliencia". Cuando la IA falla, el Fallback toma la pregunta, la traduce a una **Query SQL/PL-SQL predefinida** (usando los esquemas de `inventario` o `ventas`) y devuelve los datos crudos formateados elegantemente. 

**Usa la data real ya existente para cubrir el vacío de la IA.**

---
*Documentación generada para la alineación del equipo de desarrollo de SIGA.*
