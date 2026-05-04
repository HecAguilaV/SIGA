# Estado de la Arquitectura - SIGA

**Última Actualización:** 2026-05-03
**Estado:** En Transición (Refactorización a Hexagonal)

## 1. Modelo de Despliegue
El sistema ha completado su transición de Monolito Modular a una arquitectura de **Microservicios Independientes**.

### Componentes Core
*   **siga-auth**: Gestión de identidad y permisos (SSO).
*   **siga-billing**: Gestión de suscripciones y pagos del SaaS (Dominio Billing).
*   **siga-inventory**: Control de stock y activos.
*   **siga-sales**: POS y facturación interna de la Pyme (Dominio Sales).
*   **siga-agent**: Inteligencia Artificial y búsqueda vectorial (pgvector).

### Frontends
*   **landing**: Portal público de captación.
*   **customer-portal**: Interfaz de gestión de cuenta y pagos para el dueño de la pyme.
*   **admin-console**: (Backoffice) Herramienta interna para la administración de SIGA (Planes, métricas, soporte).
*   **webapp**: Panel administrativo de la pyme.
*   **mobile**: Aplicación de terreno.

## 2. Estrategia de Persistencia
Se aplica el principio de **Database per Service**. Cada microservicio es dueño absoluto de su base de datos.

| Microservicio | Base de Datos | Esquema Principal |
|---------------|---------------|-------------------|
| siga-auth     | siga_auth     | auth              |
| siga-billing  | siga_billing  | billing           |
| siga-inventory| siga_inventory| inventory         |
| siga-sales    | siga_sales    | sales             |
| siga-agent    | siga_agent    | agent             |

**Nota sobre Sales**: El microservicio de Sales gestiona sus propias facturas de venta bajo el esquema `sales`, independiente de la facturación del SaaS.

## 3. Infraestructura
*   **Service Discovery**: Netflix Eureka.
*   **API Gateway**: Spring Cloud Gateway.
*   **Event Broker**: **Apache Kafka** (Local) / **GCP Pub/Sub** (Cloud).
*   **Contenerización**: Docker Compose con inicialización automatizada (`init-db.sh`).

## 4. Patrones de Comunicación
*   **Sincrónico**: REST API vía Gateway para operaciones de lectura y comandos críticos.
*   **Asincrónico (SAGA)**: Coreografía de eventos vía Kafka para transacciones distribuidas (ej: Venta -> Stock).
*   **Analítico**: Streaming de eventos hacia BigQuery/Vertex AI para ingesta Big Data.

## 5. Patrón Interno (SDR - Software Design Reality)
**ADVERTENCIA**: El estado actual del código presenta un **Alto Acoplamiento** (Coupled Layered Architecture).
*   **Dominio Anémico**: Las entidades de negocio están acopladas a JPA y se usan como DTOs.
*   **Lógica en Adaptadores**: La lógica de negocio vive en los Controllers y Consumers de Kafka.

## 6. Hoja de Ruta de Calidad (Transición Hexagonal)
Se ha tomado la decisión estratégica de migrar hacia **Arquitectura Hexagonal (Ports & Adapters)** para garantizar:
1.  **Independencia del Framework**: Spring Boot debe ser un detalle de infraestructura.
2.  **Testeabilidad**: Lógica de negocio testeable sin contexto de Spring.
3.  **Escalabilidad Lambda**: Modelos de dominio puros listos para procesamiento de Big Data.

*Ver [ARCHITECTURE_RECOVERY_PLAN.md](ARCHITECTURE_RECOVERY_PLAN.md) para detalles de implementación.*

