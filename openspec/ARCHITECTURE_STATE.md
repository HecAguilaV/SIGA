# Estado de la Arquitectura - SIGA

**Última Actualización:** 2026-05-11
**Estado:** Hexagonal Completo (100% microservicios migrados)

## 1. Modelo de Despliegue
El sistema ha completado su transición de Monolito Modular a una arquitectura de **Microservicios Independientes con Hexagonal Architecture**.

### Componentes Core
*   **siga-auth**: Gestión de identidad y permisos (SSO). ✅ Hexagonal
*   **siga-billing**: Gestión de suscripciones y pagos del SaaS (Dominio Billing). ✅ Hexagonal
*   **siga-inventory**: Control de stock y activos. ✅ Hexagonal
*   **siga-sales**: POS y facturación interna de la Pyme (Dominio Sales). ✅ Hexagonal
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

## 5. Patrón Interno (Hexagonal Architecture - Implementado)
✅ **Completado**: Todos los microservicios core (auth, billing, inventory, sales) siguen **Arquitectura Hexagonal (Ports & Adapters)**.
*   **Dominio Puro**: Modelos de negocio en `domain/model/` sin dependencias JPA/Spring.
*   **Puertos**: Interfaces en `domain/port/` que definen contratos de persistencia.
*   **Adaptadores**: Implementaciones JPA en `infrastructure/adapter/` con mappers Entity ↔ Domain.
*   **Casos de Uso**: Lógica de aplicación en `application/usecase/` con validación.
*   **Controladores**: Capa de entrada HTTP que inyecta casos de uso (no repositorios).

## 6. Calidad y Cobertura de Tests

### Cobertura por Servicio (Mayo 2026)
| Servicio | Tests Unitarios | Tests Adaptadores | Tests Integración HTTP | Total |
|----------|----------------|-------------------|----------------------|-------|
| auth     | 59             | 29                | 38                   | 126   |
| billing  | 7              | 22                | 4                    | 33    |
| inventory| 12             | 25                | 10                   | 47    |
| sales    | 30             | 55                | 18                   | 103   |

### Infraestructura de Tests
- **H2** para tests de adaptadores y persistencia (rápido, sin Docker)
- **MockMvc** para tests de integración HTTP
- **Embedded Kafka** para tests de eventos SAGA
- **Flyway**: Activado en producción (`ddl-auto: validate`), deshabilitado en tests (H2 + `create-drop`)
- **Auth**: Flujos completos de autenticación implementados: register, email verification, dual-principal login (Customer/User), JWT (generate+verify+filter), SecurityConfig (permitAll + JWT chain), tenant-scoped User CRUD — 126 tests, 0 failures. Jerarquía de tenants clara: Dueño ≠ User, Customer con control inherente, Users con permisos granulares.
- **Convención**: Tests en cada servicio replican el patrón hexagonal: adapter tests → use case tests → integration tests

### Patrón de Commits
- **Rama actual**: `migracion-microservicios`
- **Formato**: Conventional Commits en español e inglés (bilingüe)
- **No PRs**: Commits directos a la rama en uso

