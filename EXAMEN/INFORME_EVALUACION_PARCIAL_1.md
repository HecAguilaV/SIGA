# Informe Final – Evaluación Parcial 1 (DSY1106)

## 1. Introducción y Contexto del Problema

SIGA (Sistema Inteligente de Gestión de Activos) nace como una solución para **PYMEs** que sufren de fricción cognitiva, pérdida de capital por falta de trazabilidad y ausencia de movilidad en sus procesos operativos. El proyecto original se estructuró en **cuatro repositorios**:

- `webapp` (frontend SvelteKit)
- `comercial` (frontend React)
- `backend` (Spring Boot + Kotlin)
- `app` (aplicación móvil Android)

Aunque el código estaba distribuido, **todos los módulos compartían un único backend monolítico** y una única base de datos. Este enfoque generaba un **punto único de falla**, dificultaba la escalabilidad y limitaba la capacidad de evolucionar de forma independiente cada dominio de negocio.

## 2. Estrategia de Migración y Estandarización Corporativa

La transformación del monolito se ejecuta mediante el **Patrón Strangler Fig (Higuera Estranguladora)**: el API Gateway enrutará inicialmente el tráfico al monolito existente, y gradualmente redirigirá dominio por dominio hacia los nuevos microservicios hasta desmantelar el sistema antiguo.

Junto a la segregación, se aplicó una **estandarización estricta del modelo de datos al español** (ej. migración de `created_at` a `fecha_creacion`). Esta decisión arquitectónica no es estética, sino estratégica: prepara el modelo relacional para una futura **ingesta masiva en procesos de Big Data**, garantizando que los pipelines de extracción y transformación operen sobre un esquema semánticamente limpio y unificado en toda la plataforma.

Las herramientas clave en esta fase son:

- **Kotlin sobre Spring Boot 3.2:** Su *Null-Safety* reduce en un 40% los defectos en producción al detectar posibles referencias nulas en tiempo de compilación.
- **ELK Stack (Elasticsearch, Logstash, Kibana) + Zipkin:** Permiten trazabilidad distribuida end-to-end para diagnosticar cuellos de botella entre servicios en milisegundos.
- **Resilience4j (Circuit Breaker):** Garantiza la tolerancia a fallos ante interrupciones de servicios externos (ej. caída de la API de IA), redirigiendo al microservicio `SIGA-Fallback`.
- **Database-per-Service (5 esquemas PostgreSQL):** Cada microservicio opera su propio esquema, aislando completamente los datos por dominio.

## 3. Arquitectura Externa e Interna

### 3.1 Arquitectura de Microservicios (El Mapa de la Ciudad)

El sistema se divide en servicios independientes con propiedad exclusiva sobre sus esquemas de base de datos:

| Microservicio | Esquema PostgreSQL | Responsabilidad Core |
|---------------|-------------------|----------------------|
| `auth` | `siga_auth` | Autenticación, JWT, RBAC y asignación de locales. |
| `inventario` | `siga_inventario` | Productos, stock, Kardex de movimientos y alertas. |
| `ventas` | `siga_ventas` | POS, turnos de caja, transacciones y detalle de ventas. |
| `backend` | `siga_comercial` | Portal Comercial y facturación SaaS. (Monolito aplicando Strangler Fig). |
| `agente` | `siga_agente` | Vector store (PGVector) e integraciones con LLMs. |

*Nota sobre Integridad Relacional:* Para no acoplar los servicios, las dependencias "cross-schema" (ej. un detalle de venta que necesita conocer un producto) se modelan mediante **referencias lógicas transversales** (IDs nativos) en lugar de dependencias físicas (`@ManyToOne` / *Foreign Keys*).

### 3.2 Visión de Arquitectura Interna (Hacia Clean Architecture)

Actualmente, los microservicios emplean una **Layered Architecture** tradicional impulsada por Spring Boot (Controller, Service, Entity). Si bien permite rapidez inicial, acopla el núcleo de negocio a detalles de infraestructura tecnológica (anotaciones JPA dentro del dominio de persistencia). 

La evolución arquitectónica trazada —para que SIGA opere a nivel Enterprise— es la transición hacia **Clean Architecture (Arquitectura Hexagonal)**. Esto logrará aislar los Modelos de Dominio en el centro, orquestarlos mediante Casos de Uso puros, y delegar la persistencia JPA y los controladores REST a ser simplemente "Detalles de Infraestructura" (Adaptadores de Entrada/Salida). El negocio dictará la arquitectura técnica, y no el framework.

## 4. Gobernanza de Datos y Cumplimiento Legal (Ley 21.719)

### Privacy by Design y Privacy by Default

La arquitectura de SIGA no relega la ciberseguridad a ser una auditoría final, sino que aplica inherentemente **Privacidad desde el Diseño (Privacy by Design)**, concepto jurídico cardinal de la nueva **Ley chilena 21.719** sobre Protección de Datos Personales.

Al aplicar el patrón arquitectónico de bases de datos segregadas por schemas, minimiza por defecto el radio de ataque ('blast radius'). Si un agente de amenaza lograra infiltrarse en el servicio de Inventario e intentara consultar tablas anexas o inyectar código, se vería imposibilitado operacionalmente: carece de los privilegios y contexto físico para unirse (`JOIN`) a las contraseñas operativas de `siga_auth` y acceder a transacciones SaaS ubicadas en `siga_comercial`.

En consecuencia, el equipo de desarrollo de SIGA demuestra **"Debida Diligencia"** proactiva ante la Agencia de Protección de Datos desde las capas operativas primarias, disminuyendo drásticamente la responsabilidad penal o las multas corporativas en caso extremo de vulneración, al seguir estrictamente el principio legal de **Privacidad por Defecto**.

## 5. Escalabilidad y Sostenibilidad Ambiental (Green Computing)

- **Escalamiento Asimétrico bajo Demanda:** En eventos peak o cargas no anticipadas, es viable aprovisionar diez réplicas horizontales dedicadas asimétricamente. Por ejemplo, `docker compose scale siga-ventas=10` no impacta el consumo de base del servidor comercial ni de identificaciones del `siga_auth`. Eureka Registry notifica y redistribuye instantáneamente según latencia y disponibilidad.
- **Sostenibilidad:** El rediseño hacia microservicios optimiza dramáticamente los recursos y desincentiva configuraciones sobredimensionadas on-premise o cloud de base permanente. Solo se gasta CPU/RAM exacto en las funciones del sistema en estrés dinámico. Al abatir la ineficiencia estructural, la organización disminuye activamente la huella de carbono operacional del datacenter.

## 6. Conclusiones

La transformación de SIGA hacia una arquitectura orientada a microservicios controlada, mitigada bajo el patrón Strangler Fig y orquestada con estándares de observabilidad y escalamiento asimétrico, consolida una solución Enterprise moderna.

Más que un proyecto transaccional de ventas, SIGA se alza como una estructura resiliente preparada para orquestar flujos cognitivos con agentes IA, pre-normalizada y limpia para integrarse a vastos procesos orientados a la ingesta **Big Data**, y férreamente construida —antes de escribir el primer Controlador HTTP— desde la resiliencia en ciberseguridad corporativa demandada por la Ley Nacional (**Privacy by Design**).
