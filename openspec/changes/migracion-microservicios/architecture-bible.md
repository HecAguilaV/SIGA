# Biblia Arquitectónica SIGA — Defensa Técnica Completa

Este documento es la referencia definitiva para la defensa de la arquitectura de SIGA.
Cubre las decisiones técnicas, los patrones de comunicación, la infraestructura
y la preparación para escalabilidad futura (Big Data).

---

## 1. ¿Por qué Kotlin y NO Java?

### El argumento académico (para tu profesor)

Kotlin no reemplaza a Java — **lo extiende**. Kotlin compila a bytecode JVM idéntico.
TODO lo que Java puede hacer, Kotlin lo hace con menos código y más seguridad.

### Los argumentos técnicos irrefutables

| Aspecto | Java | Kotlin | Veredicto |
|---------|------|--------|-----------|
| Null Safety | `@Nullable` (anotación, no se aplica en runtime) | `String?` (el compilador te IMPIDE acceder sin verificar) | Kotlin elimina NullPointerException en compilación |
| Verbosidad | 40-60 líneas para un DTO | 5 líneas con `data class` | Kotlin produce el mismo bytecode con 70% menos código |
| Coroutines | `CompletableFuture` (complejo) | `suspend fun` (natural) | Kotlin maneja async sin callback hell |
| Spring Boot | Soporte completo | Soporte first-class desde Spring 5+ | Ambos son ciudadanos de primera clase |
| Google | N/A | Lenguaje oficial de Android desde 2019 | Google eligió Kotlin sobre Java |
| JetBrains | IntelliJ está hecho en Java | JetBrains CREÓ Kotlin para reemplazar Java en sus propios productos | El creador del IDE más popular del mundo prefiere Kotlin |

### El argumento killer para tu profesor

> "Profesor, Kotlin compila a bytecode JVM. Si usted descompila mi `.class`, verá
> exactamente el mismo código que Java generaría. La diferencia es que Kotlin me
> da null-safety en tiempo de compilación, lo cual REDUCE bugs en producción.
> Spring Boot 3.x tiene soporte nativo para Kotlin. No estoy abandonando el
> ecosistema Java — estoy usando una sintaxis más segura sobre la misma JVM."

### Evidencia concreta en SIGA

```kotlin
// Kotlin (5 líneas)
data class ProductoDTO(
    val id: Int,
    val nombre: String,
    val precio: BigDecimal?  // El ? te OBLIGA a manejar el null
)

// Java equivalente (30+ líneas)
public class ProductoDTO {
    private int id;
    private String nombre;
    private BigDecimal precio;
    // + constructor + getters + setters + equals + hashCode + toString
}
```

**NOTA IMPORTANTE**: Para entidades JPA, NO usamos `data class` (por problemas con
lazy loading y proxies de Hibernate). Usamos clases normales de Kotlin con `open`.
Esto demuestra que conocemos las limitaciones y no aplicamos patrones ciegamente.

---

## 2. Patrones de Arquitectura

### 2.1 Arquitectura de Microservicios (Patrón Principal)

SIGA usa microservicios como patrón base. Cada servicio es independiente,
tiene su propio ciclo de vida y puede desplegarse por separado.

### 2.2 Arquitectura Orientada a Eventos (Event-Driven) — Futuro

SIGA está PREPARADA para evolucionar a event-driven, pero NO lo implementamos hoy.
¿Por qué? Porque añadir Apache Kafka o RabbitMQ ahora sería over-engineering.

**Cuándo activarla**: Cuando SIGA tenga más de 100 clientes concurrentes y necesitemos
desacoplar las ventas del inventario (ej: el stock se actualiza por eventos, no por
llamadas directas).

### 2.3 Serverless — NO aplica

Serverless (AWS Lambda, Google Cloud Functions) elimina los servidores.
SIGA no es candidata porque:
- Necesitamos conexiones persistentes a PostgreSQL (serverless tiene cold starts)
- Nuestro asistente IA requiere contexto de sesión
- Docker Compose nos da control total del entorno

### 2.4 Patrón Strangler Fig (Migración Progresiva)

Este SÍ lo usamos. Es la estrategia para migrar del monolito a microservicios
sin detener la producción. El Gateway rutea gradualmente endpoints al nuevo
servicio mientras el monolito sigue funcionando para lo que aún no se migra.

---

## 3. Patrones de Comunicación entre Microservicios

### 3.1 Comunicación Síncrona (Lo que usamos HOY)

#### REST (HTTP/JSON) — Patrón principal de SIGA
```
siga-gateway ──HTTP──▶ siga-auth ──HTTP──▶ siga-inventario
```
- **Cuándo**: Cuando el cliente necesita una respuesta inmediata
- **Ejemplo**: El cajero escanea un producto → necesita el precio AHORA
- **Herramienta**: Spring Boot `RestTemplate` o `WebClient`

#### gRPC (Protocol Buffers) — Opcional/Futuro
- Más rápido que REST (binario vs texto)
- Ideal para comunicación interna entre servicios (no expuesto al frontend)
- **Cuándo activarlo**: Si la latencia entre servicios se vuelve un problema
- Spring Boot tiene soporte via `grpc-spring-boot-starter`

### 3.2 Comunicación Asíncrona (Preparación Futura)

#### Mensajería con Apache Kafka / RabbitMQ
```
siga-ventas ──evento──▶ [Cola] ──▶ siga-inventario (actualiza stock)
                              ──▶ siga-billing (registra la transacción)
```
- **Cuándo**: Cuando NO necesitas respuesta inmediata
- **Ejemplo**: Una venta se registra → el stock se actualiza en background
- **Herramienta futura**: Spring Cloud Stream + Kafka

### 3.3 SIGA Hoy: Comunicación Híbrida

| Flujo | Tipo | Razón |
|-------|------|-------|
| Frontend → Gateway → Auth | Síncrono (REST) | El usuario necesita su token AHORA |
| Frontend → Gateway → Inventario | Síncrono (REST) | Consulta de stock en tiempo real |
| Frontend → Gateway → Ventas | Síncrono (REST) | Registro de venta requiere confirmación |
| Ventas → Inventario (descuento stock) | Síncrono (REST) | Integridad: no vender sin stock |
| Agente → Fallback | Síncrono (REST) | Si Gemini falla, respuesta inmediata de respaldo |
| *Futuro*: Ventas → Kafka → Analytics | Asíncrono | Big Data no necesita datos en tiempo real |

---

## 4. Stack Tecnológico Definitivo (con versiones para Docker)

### 4.1 Backend

| Tecnología | Versión | Justificación |
|------------|---------|---------------|
| Kotlin | 1.9.22 | Ya definida en build.gradle.kts |
| Spring Boot | 3.2.0 | Última LTS estable |
| Spring Cloud | 2023.0.x (Leyton) | Compatible con Spring Boot 3.2 |
| Spring Cloud Netflix (Eureka) | 4.1.x | Service Discovery requerido |
| Spring Cloud Gateway | 4.1.x | API Gateway reactivo |
| Spring Security + OAuth2 | (incluido en Boot 3.2) | Auth con Google/Apple |
| PostgreSQL Driver | 42.7.1 | Ya definido |
| java-jwt (Auth0) | 4.4.0 | Emisión/validación JWT |
| springdoc-openapi | 2.3.0 | Swagger UI automático |
| JDK | 21 (LTS) | Última LTS de Java |

### 4.2 Frontend

| Tecnología | Versión | Uso |
|------------|---------|-----|
| SvelteKit | 2.x + Svelte 5 | Webapp (cajeros/operadores) |
| React | 18.x + Vite | Web Comercial (dueños de negocio) |
| Bulma | 1.x | CSS Framework (webapp) |
| Bootstrap | 5.x | CSS Framework (comercial) |

### 4.3 Infraestructura Docker

```yaml
# Versiones pinneadas para reproducibilidad total
services:
  siga-db:
    image: postgres:15-alpine           # Alpine = imagen liviana

  siga-eureka:
    image: eclipse-temurin:21-jre-alpine # JRE 21 minimalista
    # build: ./services/registry

  siga-gateway:
    image: eclipse-temurin:21-jre-alpine
    # build: ./services/gateway

  # ... mismo base image para todos los servicios Spring Boot

  # ----- MONITORIZACIÓN -----

  # Prometheus: recolecta métricas de todos los servicios
  prometheus:
    image: prom/prometheus:v2.51.0

  # Grafana: dashboards visuales de salud del sistema
  grafana:
    image: grafana/grafana:10.4.0

  # ----- LOG AGGREGATION (ELK Stack) -----

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.13.0

  logstash:
    image: docker.elastic.co/logstash/logstash:8.13.0

  kibana:
    image: docker.elastic.co/kibana/kibana:8.13.0

  # ----- DISTRIBUTED TRACING -----

  zipkin:
    image: openzipkin/zipkin:3.3
```

---

## 5. Observabilidad y Monitorización

### 5.1 Swagger / OpenAPI (Documentación de API)

Ya tenemos `springdoc-openapi` en las dependencias. Cada microservicio expondrá
automáticamente su documentación en `/swagger-ui.html`.

**Para Postman**: Exportaremos las colecciones desde Swagger → Postman Collection.
Esto permite al profesor probar los endpoints sin levantar el frontend.

### 5.2 Health Checks (Spring Boot Actuator)

Cada servicio expone `/actuator/health` con estado de salud.
Eureka usa estos endpoints para saber si un servicio está vivo.

### 5.3 Log Aggregation (ELK Stack)

```
Servicio → Logback → Logstash → Elasticsearch → Kibana (Dashboard visual)
```

- **Elasticsearch**: Motor de búsqueda donde se almacenan los logs
- **Logstash**: Recolector que formatea y envía los logs a Elasticsearch
- **Kibana**: Interfaz web para buscar y visualizar logs

Spring Boot ya usa Logback por defecto. Solo necesitamos un appender
que envíe los logs a Logstash en formato JSON.

### 5.4 Distributed Tracing (Zipkin + Micrometer)

Cuando una petición cruza 3 servicios (Gateway → Auth → Ventas),
¿cómo sabes dónde se demoró o falló? 

**Tracing distribuido**: Cada petición recibe un `traceId` único.
Zipkin recolecta los tiempos de cada servicio y te muestra un "mapa de calor"
de dónde está el cuello de botella.

Spring Boot 3.x usa Micrometer Tracing (reemplaza a Spring Cloud Sleuth).

### 5.5 Métricas (Prometheus + Grafana)

- **Prometheus**: Recolecta métricas (CPU, memoria, requests/segundo) de cada servicio
- **Grafana**: Dashboards bonitos con gráficas en tiempo real
- Spring Boot Actuator + Micrometer exporta métricas en formato Prometheus automáticamente

---

## 6. Manejo de Errores y Logging Real

### 6.1 Global Exception Handler (Kotlin)

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("Recurso no encontrado: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(
                status = 404,
                error = "Not Found",
                message = ex.message ?: "Recurso no encontrado",
                timestamp = Instant.now()
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Error inesperado", ex)  // Stack trace completo en logs
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(
                status = 500,
                error = "Internal Server Error",
                message = "Error interno del servidor",  // NO exponemos detalles al cliente
                timestamp = Instant.now()
            ))
    }
}
```

Esto es IDÉNTICO a como se hace en Java, pero con menos verbosidad.
`@RestControllerAdvice` funciona igual en Kotlin y en Java.

### 6.2 Custom Exceptions

```kotlin
class ResourceNotFoundException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class BusinessRuleViolationException(message: String) : RuntimeException(message)
```

---

## 7. siga-fallback vs Docker Fallback

### Tu siga-fallback (Microservicio de Resiliencia)
Es un servicio que responde cuando `siga-agente` no puede conectarse a Gemini AI.
Devuelve respuestas predefinidas y/o ejecuta consultas SQL directas.

### Docker Fallback (Concepto diferente)
En Docker, "fallback" se refiere a la política de reinicio de un contenedor:
```yaml
services:
  siga-agente:
    restart: on-failure       # Si el contenedor falla, Docker lo reinicia
    deploy:
      restart_policy:
        condition: on-failure
        max_attempts: 3       # Máximo 3 reintentos
```

Son conceptos completamente diferentes. Tu `siga-fallback` es lógica de negocio.
El fallback de Docker es infraestructura de recuperación.

---

## 8. Preparación para Big Data

### Arquitectura lista para escalar

La clave para que SIGA esté "Big Data Ready" es separar los flujos:
- **OLTP** (Online Transaction Processing): Lo que hacemos hoy — ventas, stock, auth
- **OLAP** (Online Analytical Processing): Lo que haremos mañana — analytics, reportes masivos

### Cómo lo logramos sin reescribir nada

```
HOY (OLTP):
  siga-ventas → PostgreSQL (siga_saas)  ← Transaccional, rápido

MAÑANA (OLAP - Big Data):
  siga-ventas → Kafka → Apache Spark / BigQuery ← Analítico, masivo
                   ↓
              Data Lake (S3/GCS)
```

El truco: Cuando añadamos Kafka (comunicación asíncrona), cada venta emitirá
un EVENTO que viaja a dos destinos:
1. PostgreSQL (para la operación diaria)
2. Data Lake (para análisis masivo)

Esto se llama **CQRS** (Command Query Responsibility Segregation) y es el
puente natural entre microservicios y Big Data.

### ¿Qué necesitamos HOY para que funcione MAÑANA?
- Nada extra. Solo necesitamos que nuestros servicios emitan eventos bien
  estructurados. El día que conectemos Kafka, los eventos ya estarán definidos.

---

## 9. Stack Completo: Spring Boot + Spring Cloud

**Sí, usamos ambos.** Aquí está la diferencia:

| Framework | Qué hace | Se usa en |
|-----------|----------|-----------|
| Spring Boot | Framework base (web, JPA, security) | TODOS los servicios |
| Spring Cloud Netflix Eureka | Service Discovery | `siga-eureka` (server) + todos los demás (clients) |
| Spring Cloud Gateway | API Gateway reactivo | `siga-gateway` |
| Spring Cloud CircuitBreaker (Resilience4j) | Resiliencia (fallback patterns) | `siga-agente` → `siga-fallback` |
| Spring Cloud Config | Configuración centralizada (futuro) | Opcional |
| Micrometer Tracing | Distributed tracing | Todos los servicios |

---

## 10. Catálogo Final de Microservicios SIGA

| # | Servicio | Puerto | Tipo | Tecnología |
|---|----------|--------|------|------------|
| 1 | `siga-eureka` | 8761 | Infraestructura | Spring Cloud Netflix Eureka Server |
| 2 | `siga-gateway` | 8080 | Infraestructura | Spring Cloud Gateway + Eureka Client |
| 3 | `siga-auth` | 8081 | Negocio | Spring Boot + Security + OAuth2 |
| 4 | `siga-inventario` | 8082 | Negocio | Spring Boot + JPA |
| 5 | `siga-ventas` | 8083 | Negocio | Spring Boot + JPA |
| 6 | `siga-billing` | 8084 | Negocio | Spring Boot + JPA |
| 7 | `siga-agente` | 8085 | Negocio | Spring Boot + WebFlux + Gemini AI SDK |
| 8 | `siga-fallback` | 8086 | Soporte | Spring Boot + Resilience4j |
