# Arquitectura de Referencia — SIGA Microservicios

Documento técnico que fundamenta las decisiones arquitectónicas del proyecto SIGA
en su migración desde un monolito modular hacia una arquitectura de microservicios.

---

## 1. Fundamentación del Lenguaje: Kotlin sobre JVM

### Compatibilidad con el Ecosistema Java

Kotlin compila a bytecode JVM estándar. El resultado binario es indistinguible del
generado por el compilador de Java. Esto garantiza compatibilidad total con las
bibliotecas, frameworks y herramientas del ecosistema Java (Spring Boot, Hibernate,
JUnit, Gradle, Maven).

### Ventajas Técnicas Medibles

| Aspecto | Java | Kotlin | Impacto |
|---------|------|--------|---------|
| Null Safety | Anotaciones opcionales (`@Nullable`) sin enforcement en compilación | Sistema de tipos nullable (`String?`) con verificación en compilación | Eliminación de `NullPointerException` como categoría de error |
| Verbosidad | DTO típico requiere 30-40 líneas (constructor, getters, setters, equals, hashCode, toString) | `data class` genera lo mismo en 3-5 líneas | Reducción de 60-70% en código boilerplate |
| Concurrencia | `CompletableFuture`, callbacks anidados | Coroutines (`suspend fun`) con flujo secuencial legible | Código asíncrono más mantenible |
| Soporte Oficial | Lenguaje base de la JVM | Lenguaje oficial de Android (Google, 2019). Soporte first-class en Spring Framework 5+ | Respaldo institucional de Google y JetBrains |

### Consideraciones Específicas para JPA

Las entidades JPA en SIGA NO utilizan `data class` de Kotlin. Se implementan como
clases abiertas (`open class`) para garantizar compatibilidad con los proxies de
Hibernate y el mecanismo de lazy loading. Esta decisión demuestra un entendimiento
de las limitaciones de la integración Kotlin-JPA y evita errores sutiles en producción.

### Interoperabilidad

Todo código Kotlin puede llamar código Java y viceversa. Las dependencias del proyecto
(`spring-boot-starter-*`, `postgresql`, `java-jwt`) son bibliotecas Java que Kotlin
consume sin adaptadores ni wrappers adicionales.

---

## 2. Patrones de Arquitectura

### 2.1 Microservicios (Patrón Principal)

Cada dominio de negocio se encapsula en un servicio independiente con su propio ciclo
de despliegue. La comunicación entre servicios se realiza exclusivamente a través de
interfaces de red (REST), eliminando el acoplamiento directo entre módulos.

### 2.2 Strangler Fig (Estrategia de Migración)

La migración del monolito se ejecuta de forma incremental. El API Gateway rutea
las peticiones hacia el nuevo microservicio correspondiente a medida que cada dominio
se extrae. El monolito original permanece operativo para los dominios aún no migrados,
garantizando continuidad del servicio durante la transición.

### 2.3 Arquitectura Orientada a Eventos (Preparación)

La arquitectura actual está diseñada para evolucionar hacia un modelo event-driven
sin reestructuración. Los servicios emiten eventos de dominio bien definidos que,
al incorporar un broker de mensajería (Apache Kafka), habilitarán comunicación asíncrona
y desacoplamiento temporal entre servicios.

### 2.4 CQRS — Puente hacia Big Data

CQRS (Command Query Responsibility Segregation) separa las operaciones de escritura
(comandos) de las de lectura (consultas). Esta separación permite que los datos
transaccionales (OLTP) fluyan hacia un sistema analítico (OLAP) sin afectar
el rendimiento operacional.

Pipeline proyectado:
```
SIGA (OLTP)
  └── Venta registrada
        ├── PostgreSQL (operacional, tiempo real)
        └── Pub/Sub → Dataflow → BigQuery (analítico, batch/streaming)
                                    └── Vertex AI (ML: predicción de demanda,
                                        detección de anomalías, análisis de tendencias)
```

---

## 3. Patrones de Comunicación

### 3.1 Comunicación Síncrona (Implementación Actual)

| Protocolo | Uso en SIGA | Justificación |
|-----------|-------------|---------------|
| REST (HTTP/JSON) | Toda la comunicación entre servicios | Estándar de la industria, compatible con Swagger/OpenAPI, depurable con Postman |
| gRPC (Futuro) | Comunicación interna de alta frecuencia | Protocolo binario con menor latencia, adecuado para llamadas internas masivas |

### 3.2 Comunicación Asíncrona (Proyección)

| Tecnología | Caso de Uso Proyectado | Beneficio |
|------------|----------------------|-----------|
| Apache Kafka / Pub/Sub | Eventos de ventas hacia analytics | Desacoplamiento temporal, tolerancia a fallos |
| Spring Cloud Stream | Abstracción del broker | Cambio de broker sin modificar código de negocio |

### 3.3 Flujos de Comunicación Actuales

| Origen | Destino | Tipo | Descripción |
|--------|---------|------|-------------|
| Cliente (Web/Mobile) | `siga-gateway` | REST | Punto de entrada único |
| `siga-gateway` | `siga-auth` | REST | Validación de credenciales y emisión de JWT |
| `siga-gateway` | `siga-inventario` | REST | Consultas de catálogo y stock |
| `siga-gateway` | `siga-ventas` | REST | Registro de transacciones |
| `siga-ventas` | `siga-inventario` | REST | Verificación y descuento de stock |
| `siga-agente` | Gemini API | REST | Procesamiento de lenguaje natural |
| `siga-agente` | `siga-fallback` | REST | Respuestas de contingencia ante timeout o fallo de Gemini |

---

## 4. Stack Tecnológico (Versiones Pinneadas)

### 4.1 Backend

| Componente | Versión | Notas |
|------------|---------|-------|
| JDK | 21 (LTS) | Última versión de soporte extendido |
| Kotlin | 1.9.22 | Versión estable con soporte Spring Boot 3.x |
| Spring Boot | 3.2.0 | Release actual con soporte LTS |
| Spring Cloud | 2023.0.x (Leyton) | Release train compatible con Boot 3.2 |
| Spring Cloud Netflix Eureka | 4.1.x | Service Discovery |
| Spring Cloud Gateway | 4.1.x | API Gateway reactivo |
| PostgreSQL Driver | 42.7.1 | Driver JDBC para PostgreSQL |
| Auth0 java-jwt | 4.4.0 | Generación y validación de tokens JWT |
| springdoc-openapi | 2.3.0 | Documentación automática Swagger/OpenAPI |
| Resilience4j | 2.2.x | Circuit Breaker, retry, rate limiter |

### 4.2 Frontend

| Componente | Versión | Aplicación |
|------------|---------|------------|
| Svelte 5 + SvelteKit | 2.x | Webapp (operadores y cajeros) |
| React 18 + Vite | 5.x | Web Comercial (administradores de negocio) |
| Bulma | 1.x | Sistema de estilos para Webapp |
| Bootstrap | 5.x | Sistema de estilos para Comercial |

### 4.3 Infraestructura

| Componente | Imagen Docker | Puerto | Función |
|------------|---------------|--------|---------|
| PostgreSQL | `postgres:15-alpine` | 5432 | Base de datos relacional |
| pgAdmin | `dpage/pgadmin4` | 8080 | Administración visual de BD |
| Prometheus | `prom/prometheus:v2.51.0` | 9090 | Recolección de métricas |
| Grafana | `grafana/grafana:10.4.0` | 3000 | Visualización de métricas |
| Elasticsearch | `elasticsearch:8.13.0` | 9200 | Almacenamiento de logs |
| Logstash | `logstash:8.13.0` | 5044 | Procesamiento de logs |
| Kibana | `kibana:8.13.0` | 5601 | Visualización de logs |
| Zipkin | `openzipkin/zipkin:3.3` | 9411 | Distributed tracing |

---

## 5. Observabilidad

### 5.1 Documentación de API (Swagger + Postman)

Cada microservicio expone automáticamente su documentación OpenAPI en `/swagger-ui.html`
mediante `springdoc-openapi`. Las colecciones de Postman se generan exportando la
especificación OpenAPI desde cada servicio.

### 5.2 Log Aggregation (ELK Stack)

```
Microservicio → Logback (JSON) → Logstash → Elasticsearch → Kibana
```

Spring Boot utiliza Logback por defecto. Se configura un appender JSON que envía los
logs estructurados a Logstash para su indexación en Elasticsearch y visualización
en Kibana.

### 5.3 Distributed Tracing (Micrometer + Zipkin)

Cada petición recibe un identificador único (`traceId`) que se propaga automáticamente
entre microservicios. Spring Boot 3.x integra Micrometer Tracing (sucesor de Spring
Cloud Sleuth) con exportación nativa a Zipkin.

### 5.4 Métricas (Prometheus + Grafana)

Spring Boot Actuator expone métricas en formato Prometheus (`/actuator/prometheus`).
Prometheus las recolecta periódicamente y Grafana las presenta en dashboards
configurables (latencia, throughput, uso de memoria por servicio).

### 5.5 Health Checks (Spring Boot Actuator)

Cada servicio expone `/actuator/health` con indicadores de salud (conexión a BD,
espacio en disco, estado de Eureka). El Service Registry utiliza estos endpoints
para determinar la disponibilidad de cada instancia.

---

## 6. Manejo de Excepciones y Logging

### 6.1 Global Exception Handler

Implementación centralizada mediante `@RestControllerAdvice` que intercepta todas
las excepciones no capturadas y devuelve respuestas estandarizadas al cliente.

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Recurso no encontrado: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, "Not Found", ex.message, Instant.now()))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Error interno", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "Internal Server Error",
                "Error interno del servidor", Instant.now()))
    }
}
```

Este patrón es idéntico al utilizado en Java con Spring MVC. La anotación
`@RestControllerAdvice` funciona de forma transparente en ambos lenguajes.

### 6.2 Excepciones de Dominio

```kotlin
class ResourceNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class BusinessRuleViolationException(message: String) : RuntimeException(message)
class InsufficientStockException(productoId: Int, solicitado: Int, disponible: Int) :
    RuntimeException("Stock insuficiente para producto $productoId: solicitado=$solicitado, disponible=$disponible")
```

---

## 7. Resiliencia: siga-fallback

### Concepto

`siga-fallback` es un microservicio de soporte que garantiza la continuidad del
servicio cuando la API externa de Gemini AI no está disponible (timeout, error 5xx,
cuota agotada).

### Mecanismo

`siga-agente` utiliza el patrón Circuit Breaker (Resilience4j) para detectar fallos
consecutivos en la comunicación con Gemini. Cuando el circuito se abre, las peticiones
se redirigen automáticamente a `siga-fallback`, que responde con:

1. Respuestas pre-definidas para consultas frecuentes
2. Consultas SQL directas sobre los servicios internos (inventario, ventas)
3. Mensaje informativo al usuario indicando modo de operación reducida

### Diferencia con Docker restart policy

`siga-fallback` resuelve fallos de lógica de negocio (la IA no responde).
La directiva `restart: on-failure` de Docker resuelve fallos de infraestructura
(el contenedor se detiene). Son capas de resiliencia complementarias.

---

## 8. Estrategia de Base de Datos (Database-per-Service)

### Aislamiento Lógico (Schema-per-Service)

Para el entorno de producción (o clúster principal), SIGA implementa el patrón **Database-per-Service** utilizando la variante de **Aislamiento Lógico**. En lugar de provisionar 4 servidores físicos separados (lo cual genera un costo financiero y operativo de DevOps innecesario para esta escala), se utiliza **un único servidor de base de datos PostgreSQL** que contiene múltiples esquemas completamente aislados.

| Esquema (Bounded Context) | Microservicio Dueño | Responsabilidad de Dominio |
|---------------------------|----------------------|----------------------------|
| `siga_auth`               | `siga-auth`          | Usuarios, roles, permisos. |
| `siga_inventario`         | `siga-inventario`    | Productos, categorías, stock local. |
| `siga_ventas`             | `siga-ventas`        | Transacciones, tickets, trazabilidad de ventas. |
| `siga_comercial`         | `siga-comercial`    | Suscripciones SaaS, usuarios comerciales, facturas. |

### La Regla de Oro del Desacoplamiento

El código fuente de los microservicios y los usuarios de base de datos están configurados para tener **acceso exclusivo a su propio esquema** (mediante contraseñas y `search_path`).
- `siga-ventas` **no tiene permisos** para consultar la tabla de stock en `siga_inventario`.
- Si `siga-ventas` requiere disminuir stock tras una venta, debe realizar una petición por red (API REST o Eventos) a `siga-inventario`.
- Esto garantiza que el acoplamiento a nivel de datos está físicamente bloqueado.

### Pruebas Locales (El polimorfismo de H2)

La prueba definitiva de que los servicios están verdaderamente desacoplados se demuestra en el entorno de pruebas (`application-test.yml` o perfiles locales). Para el desarrollo local y pipelines CI/CD, la conexión a PostgreSQL se reemplaza por bases de datos H2 en memoria completamente independientes:

- `siga-auth` instancia: `jdbc:h2:mem:auth_db`
- `siga-inventario` instancia: `jdbc:h2:mem:inventario_db`

Este comportamiento demuestra que la lógica de negocio es 100% agnóstica a la topología física. Los servicios no dependen estructuralmente de un PostgreSQL compartido; la consolidación física ocurre solo en producción para optimizar costos de infraestructura.

---

## 9. Preparación para Big Data

### Arquitectura de Datos Analíticos (OLAP)

SIGA genera datos transaccionales (ventas, movimientos de stock, eventos de usuario)
que constituyen una fuente de datos valiosa para análisis predictivo y de negocio.

### Pipeline Propuesto (GCP)

```
┌─────────────────────────┐
│   SIGA (OLTP)           │
│   PostgreSQL             │
└────────┬────────────────┘
         │ Change Data Capture / Eventos
         ▼
┌─────────────────────────┐
│   Cloud Pub/Sub         │  ← Ingesta de eventos en tiempo real
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   Dataflow (Apache Beam)│  ← Transformación y enriquecimiento
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   BigQuery              │  ← Data Warehouse analítico
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│   Vertex AI             │  ← Machine Learning
│   - Predicción de demanda│
│   - Detección de anomalías│
│   - Segmentación de clientes│
└─────────────────────────┘
```

### Vertex AI en el Contexto de SIGA

| Producto | Uso en SIGA | Capa |
|----------|-------------|------|
| Vertex AI Gemini API | Asistente conversacional (`siga-agente`) | Aplicación (OLTP) |
| Vertex AI AutoML / Pipelines | Predicción de demanda, anomalías | Analítica (OLAP) |
| BigQuery ML | Modelos directamente sobre datos de ventas | Analítica (OLAP) |

Son dos usos completamente diferentes del mismo ecosistema. El asistente conversacional
opera en tiempo real sobre datos transaccionales. Los modelos analíticos operan en
batch sobre datos históricos almacenados en BigQuery.

### Generación de Datos Sintéticos

Para la evaluación de Big Data, SIGA puede generar datos sintéticos programáticamente
utilizando las entidades existentes. Un script de inyección creará registros realistas
de ventas, movimientos de stock y comportamiento de usuarios a escala (millones de
registros) para alimentar el pipeline analítico.

---

## 10. Catálogo de Microservicios

| # | Servicio | Puerto | Tipo | Tecnología Principal |
|---|----------|--------|------|---------------------|
| 1 | `siga-eureka` | 8761 | Infraestructura | Spring Cloud Netflix Eureka Server |
| 2 | `siga-gateway` | 8080 | Infraestructura | Spring Cloud Gateway + Eureka Client |
| 3 | `siga-auth` | 8081 | Negocio | Spring Boot + Security + OAuth2 Client |
| 4 | `siga-inventario` | 8082 | Negocio | Spring Boot + JPA |
| 5 | `siga-ventas` | 8083 | Negocio | Spring Boot + JPA |
| 6 | `siga-comercial` | 8084 | Negocio | Spring Boot + JPA |
| 7 | `siga-agente` | 8085 | Negocio | Spring Boot + WebFlux + Gemini API |
| 8 | `siga-fallback` | 8086 | Soporte | Spring Boot + Resilience4j |
