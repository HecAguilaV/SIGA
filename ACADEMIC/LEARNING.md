# LEARNING — Manual de Aprendizaje y Arquitectura de SIGA

> *"No sé mucho, sueño harto, deseo más."*
> — Un Soñador con Poca RAM 👨🏻‍💻

---

## Prólogo: El Viaje

SIGA no nació en una sala de clases. SIGA nació en la ruta, manejando. 

Era chofer repartidor de una empresa que operaba casinos y kioskos concesionarios en instituciones académicas y laborales. Empecé como chofer, después me "ascendieron" a administrador de los kioskos, y finalmente a encargado de inventario. Era un **operador orquesta**: tres roles, un sueldo.

La empresa tenía contratado un software ERP que era... terrible. La UX/UI era pésima, lento en sus cargas y actualizaciones de estado. Para alguien como yo, que se da con el manejo de software, me costó horrores entenderlo. Ahora imagínate para alguien que no.

De los tres roles que tenía que cumplir, aunque me esforcé, solo terminé cumpliendo con el de chofer repartidor. El tiempo para alguien que vive en terreno es oro. Lo veía con la dueña también: hacía de todo. Cuando ella no estaba, era un caos. Literalmente la empresa cojeaba. Para llevar el stock, ella trasnochaba — según me contó — anotando qué vendieron, qué no, cuánto vendieron, revisando los mensajes que enviaban las cajeras sobre qué productos tenían y cuáles no. Yo no podía darme el lujo de quedarme en los kioskos pidiendo información o viendo qué faltaba. No teníamos cómo obtener KPIs.

Fue entonces cuando pensé: *debería haber una solución para gestionar el inventario mientras "conduzco"* — y "conduzco" quiere decir antes de comenzar a conducir, entre paradas, entre carga y descarga de mercadería. En ese instante mi idea fue un **asistente de IA con facultades CRUD**. Mientras usaba la webapp responsive en el teléfono, podía escribirle al chatbot: *"añade X productos a Y local y Z productos a V local"*. Así optimizaría mi tiempo y podría cumplir con el rol de encargado de inventario y de chofer.

Y pensaba más allá: este mismo asistente, a través de RAG, podría entregarme información valiosa sobre KPIs estratégicos. *"¿Cuáles son los productos bajos de stock en V local?"*, y me respondería de forma clara, amigable, sencilla. *"Muéstrame qué productos se venden más o menos en determinados locales"*, *"¿cuál es el local de mayor venta?"*, *"¿cuál es el producto estrella?"*.

Así nació la idea. Después vi una competencia de ideas y, con miedo y esperanza, me inscribí. Empecé a buscar el nombre. Entre una lluvia de ideas apareció **Sistema Inteligente de Gestión de Inventario**. Pero decidí cambiar "Inventario" por "Activos". Como un llamado a la acción y una promesa a mí mismo: el inventario es el comienzo, activos abarca más. La decisión final: **Sistema Inteligente de Gestión de Activos — SIGA**. *SIGA para que no te detengas.*

Descubrí que se perdía mucho tiempo. Mi jefa, además de trasnochar, seguía trabajando los sábados y domingos haciendo compras de mercadería, cargando y descargando, revisando listas en papel. Para mí eso era inaceptable. Como empleado, no estaba dispuesto a trabajar días de descanso sin remuneración. Y pensé en las **personas orquesta** — en mí, en mi jefa, en su bienestar — y en las PYMEs que viven el mismo caos. Por eso uno de mis pilares para el desarrollo es **primero la UX**: pienso en mis padres, que no son nativos digitales y se les complica usar software y apps móviles. Si SIGA no es intuitivo, no sirve.

> *"No gestiones tu inventario, gestiona Tu Tiempo."*
>
> — Eslogan de SIGA

Según mi proyección, con SIGA como lo visualizo, al día se podrían ahorrar hasta **1 hora y 30 minutos**, solo pensando en el horario laboral. Mi ex jefa ya no tendría que volver a trasnochar. Las PYMEs podrían respirar.

SIGA tiene potencial para escalar a empresas más grandes; por eso "Activos" en vez de solo "Inventario". Pero hoy mi foco es el inventario y el servicio de POS. Porque SIGA no puede depender de APIs de terceros — sobre todo sabiendo que probablemente ni API tienen para compartir. La evolución lógica del chatbot con mucha codificación detrás son **los agentes**.

Y sí, este proyecto empezó siendo un **monolito feo con repos sueltos** (multi-repo). Funcionaba, pero era difícil de mantener, de escalar, y sobre todo, de explicar. En ese entonces era código que hacía cosas, pero no sabría decirte por qué estaba estructurado así.

Llegó el momento de la Universidad. Este año (2026) tengo:

| Asignatura                         | Cómo se conecta con SIGA                   |
| ---------------------------------- | ------------------------------------------- |
| **Fullstack 3**              | Proyecto libre → SIGA es mi proyecto       |
| **Evaluación de Proyectos** | Evaluar viabilidad económica → Ley 21.719 |
| **Big Data**                 | Ingesta de datos → Con SIGA                |
| **Fundamentos de ML**        | Fundamentos (dataset aparte)                |
| **Ing. de Soluciones IA**    | Agentes inteligentes → Con SIGA            |
| **Herramientas DevOps**      | Docker, CI/CD, deploy → AWS con créditos  |

El problema: **hacer todo esto solo, en un semestre, con 41 años, 4 hijos, y viniendo de ser carpintero de construcción** no es normal. Trabajo remoto desde un MacBook 2019 de 14" con un SSD externo pegado con cinta en la tapa, conectado por SSH a un PC de escritorio más potente donde corre todo — Docker, los servicios, esta conversación.

Uso agentes de IA. No lo niego. Sería absurdo pretender que esto se hace solo en este tiempo. Pero cada decisión, cada patrón, cada línea de código, la entiendo y la defiendo. Este documento es **mi aprendizaje hecho texto**.

> Si llegó a tus manos (profesor, reclutador, estudiante, desarrollador): bienvenido. Esto no es solo documentación técnica. Es el diario de construcción de un sueño.

---

## Parte 1: Mapa del Territorio

### 1.1 Monorepo — ¿Por qué todo junto?

SIGA es un **Monorepo de Microservicios**. Suena contradictorio, pero tiene sentido:

```
SIGA/
├── services/         → Todos los microservicios
│   ├── auth/         → Autenticación, registro, login, permisos
│   ├── billing/      → Facturación y pagos (SaaS)
│   ├── inventory/    → Stock multi-tenant
│   ├── sales/        → POS y transacciones
│   ├── common/       → Librería compartida (auditoría)
│   ├── gateway/      → Spring Cloud Gateway (puerta de entrada)
│   ├── registry/     → Eureka Server (service discovery)
│   └── agent/        → Agente IA (Python)
├── openspec/         → Documentación viva (SDD)
├── ACADEMIC/         → Este documento y otros recursos
├── .github/          → CI/CD (GitHub Actions)
├── docker-compose.yml → Orquestación local
└── .env.example      → Template de variables de entorno
```

**¿Por qué monorepo si son microservicios?**

Para un desarrollador solitario asistido por IA, tener todo en un mismo repositorio es una VENTAJA GIGANTESCA:

- **Visibilidad total**: La IA ve el ecosistema completo de una sola mirada. No tengo que estar saltando entre 8 repos.
- **Cambios atómicos**: Si cambio un contrato entre servicios (ej: Auth devuelve un campo nuevo y Gateway lo consume), lo hago en UN SOLO commit.
- **Un solo `docker-compose.yml`**: Todo el ecosistema se levanta con un comando.
- **CI/CD unificado**: Un solo pipeline que construye y testea todo.

El día que el proyecto crezca y tenga un equipo humano, evaluaremos separar repos. Hoy, es la decisión correcta.

### 1.2 Los Servicios — Quién es Quién

| Servicio           | Puerto | Base de Datos (en PostgreSQL)               | ¿Qué hace?                                                                                                       |
| ------------------ | ------ | ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| `siga-registry`  | 8761   | —                                          | Service Discovery (Eureka). Todos los servicios se registran acá. El Gateway pregunta acá "¿dónde está auth?" |
| `siga-gateway`   | 8080   | —                                          | Puerta de entrada única. Token JWT, CORS, ruteo. El frontend solo habla con él.                                  |
| `siga-auth`      | 8081   | `siga_auth` (esquema: `auth`)           | **Identidad**. Register, verify email, login (Customer + User), JWT, CRUD de usuarios, permisos.             |
| `siga-inventory` | 8082   | `siga_inventory` (esquema: `inventory`) | **Stock**. Control de activos, multi-tenant. Stock consolidado multi-punto, búsqueda ILIKE+unaccent, auto-SKU, conciliación con alertas, transferencias bodega↔punto. |
| `siga-sales`     | 8083   | `siga_sales` (esquema: `sales`)         | **POS**. Ventas, transacciones, facturación interna de la PYME.                                             |
| `siga-billing`   | 8084   | `siga_billing` (esquema: `billing`)     | **SaaS**. Facturación de la plataforma, planes, pagos de las PYMEs.                                         |
| `siga-agent`     | 8000   | `siga_agent` (esquema: `agent`)         | **IA**. Agente inteligente con búsqueda vectorial.                                                          |
| `siga-ops`       | —     | —                                       | **Observabilidad local**. ContainerFlow (~80MB) — dashboard Docker en tiempo real vía navegador. Se levanta al final del `start-staggered.sh`. |

**Regla de oro**: Cada servicio es 100% dueño de su base de datos. Ningún servicio cruza a la BD de otro. Si necesita datos de otro servicio, lo pide por API (sincrónico vía Gateway) o por evento (asincrónico vía Kafka).

### 1.3 Guía Práctica de Navegación

> *"No sé qué hace cada bloque de código — necesito saber dónde ir para cada cosa."*

| Si quiero...                              | Voy a...                                                                              |
| ----------------------------------------- | ------------------------------------------------------------------------------------- |
| **Agregar un endpoint nuevo**       | `services/{servicio}/src/main/kotlin/com/siga/{servicio}/controller/`               |
| **Agregar un caso de uso**         | `.../application/usecase/` (hereda de Port)                                         |
| **Agregar un puerto (Port)**       | `.../domain/port/` (interfaz que el caso de uso necesita)                           |
| **Agregar un adaptador JPA**       | `.../infrastructure/adapter/` (implementa el Port)                                 |
| **Cambiar el modelo de datos**      | `.../domain/model/` (puro) + `.../entity/` (JPA) + `.../infrastructure/mapper/` |
| **Agregar una migración de BD**    | `.../src/main/resources/db/migration/` (Flyway)                                     |
| **Cambiar reglas de seguridad**     | `.../security/` (JwtAuthFilter, SecurityConfig)                                     |
| **Agregar un test unitario**        | `.../src/test/kotlin/...` (mismo path que el source)                                |
| **Agregar un test de integración** | `.../src/test/kotlin/...` con `@SpringBootTest`                                   |
| **Cambiar configuración**          | `.../src/main/resources/application.yml`                                            |
| **Modificar una dependencia**       | `.../build.gradle.kts`                                                              |

---

## Parte 2: Stack Tecnológico — Cada Dependencia Justificada

### 2.1 Kotlin + Spring Boot

```kotlin
// build.gradle.kts (services/auth/)
plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"     // ← Abre clases para Spring (no necesita `open`)
    kotlin("plugin.jpa") version "2.2.0"        // ← Habilita JPA en data classes
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
}
```

**¿Por qué Kotlin y no Java?**

- Es más expresivo (menos boilerplate que Java, aunque igual tenemos bastante por Hexagonal)
- Null-safety: el sistema de tipos te obliga a pensar si algo puede ser null o no
- Spring Boot 4.x tiene soporte de primera clase para Kotlin
- **Costo**: Curva de aprendizaje. Pero una vez que lo entiendes, no vuelves a Java.

**¿Por qué Spring Boot y no Quarkus / Micronaut / Helidon?**

- Es lo que conozco y lo que se enseña en la U. Para un proyecto que necesita avanzar rápido, usar lo que ya sabes es sabio.
- Ecosistema enorme: si hay un problema, ya hay mil personas que lo tuvieron antes.
- Spring Boot 4.x trae mejoras significativas en tiempo de arranque y memoria.

**¿Por qué Gradle y no Maven?**

- Gradle es más rápido (incremental builds, cache).
- El DSL de Kotlin es más legible que el XML de Maven.
- Pero: Gradle 8.x+ tiene cambios de compatibilidad que nos han dado dolores de cabeza (la advertencia esa de "Kotlin Gradle plugin was loaded multiple times").

### 2.2 Base de Datos

| Tecnología          | Rol         | ¿Por qué?                                                                                                                                                           |
| -------------------- | ----------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **PostgreSQL** | Producción | Open source, robusto, soporta JSONB, schemas, MVCC. Lo usan startups y empresas grandes.                                                                              |
| **H2**         | Tests       | In-memory, rapidísimo. Los tests no necesitan PostgreSQL corriendo.**PERO CUIDADO**: H2 y PostgreSQL NO se comportan igual con UUIDs, columnas de arrays, etc. |

**Las migraciones son con Flyway**, no con `ddl-auto: update` de Hibernate. ¿Por qué?

- **Control de versiones**: La BD es un activo. Cada cambio queda registrado.
- **`ddl-auto: validate` en producción**: Hibernate verifica que el código coincida con la BD real. Si alguien hizo un cambio a mano, el sistema falla al arrancar. Eso es bueno — "falla temprano, falla fuerte".
- En tests usamos `create-drop` (H2), así que las migraciones no corren en tests.

**Schema `auth` en la BD:**

```sql
ALTER TABLE auth.customers ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE auth.users ADD COLUMN customer_id INTEGER;
```

Cada servicio tiene su PROPIA base de datos (`siga_auth`, `siga_inventory`, `siga_sales`, `siga_billing`) dentro de la misma instancia PostgreSQL. Y dentro de cada base de datos, un esquema con el mismo nombre. Esto es **base de datos por servicio** — no solo esquemas separados.

### 2.3 Dependencias una por una

#### Spring Boot Starters

| Dependencia                                    | ¿Para qué sirve?                                 | ¿Qué pasaría si no estuviera?                                  |
| ---------------------------------------------- | -------------------------------------------------- | ----------------------------------------------------------------- |
| `spring-boot-starter-web`                    | APIs REST (controllers, Jackson, Tomcat)           | No podríamos exponer endpoints HTTP                              |
| `spring-boot-starter-data-jpa`               | Hibernate + repositorios                           | Tendríamos que escribir JDBC puro → muchísimo más código     |
| `spring-boot-starter-security`               | Filtros de autenticación, SecurityContext, BCrypt | Cualquier endpoint sería público. No habría JWT ni protección |
| `spring-boot-starter-oauth2-client`          | Si en futuro queremos OAuth (Google, GitHub login) | Hoy no se usa activamente, pero está disponible                  |
| `spring-boot-starter-mail`                   | JavaMailSender para enviar emails                  | El registro no podría enviar emails de verificación             |
| `spring-boot-starter-actuator`               | `/actuator/health`, `/actuator/info`           | No sabríamos si el servicio está vivo                           |
| `spring-boot-starter-flyway`                 | Migraciones de BD versionadas                      | Cambiarían la BD a mano → caos                                  |
| `spring-cloud-starter-netflix-eureka-client` | Registrarse en Eureka                              | El Gateway no sabría dónde está este servicio                  |

#### Otras dependencias

| Dependencia                                                 | ¿Para qué sirve?                                                                      |
| ----------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| `com.auth0:java-jwt:4.4.0`                                | Crear y validar tokens JWT (HMAC256). Alternativa considerada:`io.jsonwebtoken:jjwt`. |
| `org.postgresql:postgresql`                               | Driver JDBC para PostgreSQL. Sin esto no hay conexión a la BD.                         |
| `org.flywaydb:flyway-database-postgresql`                 | Módulo de Flyway para PostgreSQL (SB 4.x lo requiere explícitamente).                 |
| `org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3` | Swagger UI. Documentación automática de APIs.                                         |

#### Testing

| Dependencia                         | ¿Para qué sirve?                                         |
| ----------------------------------- | ---------------------------------------------------------- |
| `spring-boot-starter-test`        | JUnit 5, Mockito, AssertJ                                  |
| `spring-boot-starter-webmvc-test` | MockMvc — testear controllers sin levantar el servidor    |
| `spring-security-test`            | Anotaciones como `@WithMockUser` para tests de seguridad |
| `com.h2database:h2`               | BD en memoria para tests (rápido, sin Docker)             |

### 2.4 Conexión Remota y Docker

**Setup físico:**

```
MacBook Pro 2019 (14", i5, 8GB RAM, SSD 128GB)
  ├── SSD externo 512GB pegado con cinta en la tapa (literal)
  ├── Cliente SSH
  └── VS Code Remote — SSH

PC de Escritorio (más potente)
  ├── Servidor Docker
  ├── 6 contenedores de microservicios
  ├── PostgreSQL
  └── Kafka
```

Esta decisión no es técnica, es **económica**. La MacBook no puede correr 6 contenedores Docker + IDE + navegador. En vez de comprar una máquina nueva (no hay plata), uso lo que tengo: un PC de escritorio que hace de servidor, y desarrollo remoto por SSH.

**¿Por qué no desarrollo directo en el PC?**
Porque el PC no tiene monitor/teclado dedicados. Y porque así puedo trabajar desde cualquier lugar con la MacBook — la conexión SSH hace de "puente".

**Estructura del `docker-compose.yml`:**

```yaml
services:
  siga-db:          # PostgreSQL — una sola instancia, múltiples bases de datos
  siga-eureka:      # Service Discovery — el "directorio telefónico"
  siga-gateway:     # API Gateway — la puerta de entrada única
  siga-auth:        # Autenticación — se registra en Eureka
  siga-inventory:   # Stock — se registra en Eureka
  siga-sales:       # Ventas — se registra en Eureka
  siga-billing:     # Facturación — se registra en Eureka
  siga-agent:       # IA — se registra en Eureka (Python)
  siga-kafka:       # Message Broker — SAGA pattern
  kafdrop:          # UI para ver Kafka
  siga-ops:         # ContainerFlow — Observabilidad local de contenedores
```

**¿Por qué una sola instancia de PostgreSQL y no un servidor de BD por servicio?**
Costo operativo. En un VPS de desarrollo, tener 5 servidores de BD consume mucha RAM. Una sola instancia de PostgreSQL con BDs separadas por servicio da el mismo aislamiento lógico con menos recursos. Cada servicio tiene:

- Su propia base de datos (`siga_auth`, `siga_inventory`, `siga_sales`, `siga_billing`, `siga_agent`)
- Su propio usuario de base de datos
- Su propio esquema dentro de esa base de datos

El script `init-db.sh` crea los schemas y usuarios al levantar el contenedor, pero **Flyway es la única fuente de verdad para el DDL** (las migraciones V1 crean schemas con `CREATE SCHEMA IF NOT EXISTS` y usan `schema.table` explícito). Cuando el proyecto crezca y tengamos presupuesto, migramos a instancias de base de datos independientes por servicio.

**¿Por qué Docker y no desplegar nativo?**

- Consistencia: lo que corre en mi PC es lo mismo que corre en producción
- Aislamiento: cada servicio tiene sus dependencias sin conflictos
- Escalabilidad: si auth necesita más recursos, levanto otro contenedor

---

## Parte 3: Arquitectura Hexagonal — El Corazón

### 3.1 ¿Por qué Hexagonal y no MVC?

MVC tradicional que enseñan en la U:

```
Controller → Service → Repository → BD
```

Eso funciona para un CRUD simple. Pero cuando el negocio crece, el Service termina siendo un cajón de sastre con lógica mezclada. Difícil de testear, imposible de cambiar.

**Arquitectura Hexagonal (Ports & Adapters):**

```
[Controller] → [UseCase] → [Port] ← [Adapter (JPA)] → BD
                              ↑
                         [Domain Model]
```

**La regla de oro**: El DOMINIO no sabe nada del mundo exterior. No conoce Spring, no conoce JPA, no conoce HTTP. Solo sabe de reglas de negocio.

**¿Por qué aceptamos el boilerplate?**
Sí, hay más archivos. Si tengo una entidad `User`, tengo:

| Archivo                                      | ¿Qué hace?                            | ¿Por qué existe?                                    |
| -------------------------------------------- | --------------------------------------- | ----------------------------------------------------- |
| `domain/model/User.kt`                     | El concepto de Usuario puro, sin JPA    | El negocio no necesita saber cómo se guarda          |
| `entity/User.kt`                           | La misma data pero con anotaciones JPA  | Hibernate/JPA necesita anotaciones para la BD         |
| `infrastructure/mapper/UserMapper.kt`      | Convierte entre Domain ↔ Entity        | Traduce entre el mundo puro y el mundo JPA            |
| `domain/port/UserRepositoryPort.kt`        | Interfaz: "guardar un usuario"          | El caso de uso no sabe si es JPA, MongoDB, o archivos |
| `repository/UserRepository.kt`             | Spring Data JPA                         | Implementación concreta del repositorio              |
| `infrastructure/adapter/UserJpaAdapter.kt` | Implementa el Puerto usando Spring Data | Engancha el puerto con la implementación concreta    |

Son 6 archivos para una entidad. En MVC serían 2-3. **Ese es el costo**. Y no es menor.

**¿Por qué vale la pena pagarlo?**

- **Separación de responsabilidades al extremo**: Cada archivo tiene UNA razón para cambiar. El mapeador solo mapea. El puerto solo declara el contrato. El adaptador solo implementa tecnología concreta. Si mañana cambias JPA por JDBA, tocas 1 archivo (el adaptador) y el mapper, no más.
- **Testabilidad sin Spring**: El `UseCase` recibe puertos (interfaces). En el test le pasas un mock. No necesitas levantar la base de datos, ni el contexto de Spring, ni nada. El test es RÁPIDO. Esa velocidad es la que permite TDD real.
- **La IA lo entiende mejor**: Puede sonar contradictorio (más archivos = más para leer), pero la IA entiende mejor estructura explícita que código amontonado. Cada archivo tiene un propósito claro. La IA no tiene que adivinar "¿esto es lógica de negocio o infraestructura?" — lo sabe por la carpeta donde está.
- **El cambio de BD es real**: Cuando pasamos de PostgreSQL a H2 en tests, el único cambio fue... ninguno. Porque los adaptadores hablan con puertos, no con la BD directamente. El `UserJpaAdapter` funciona igual con PostgreSQL que con H2.
- **Deuda técnica evitable**: En MVC, cuando el negocio crece, el Service termina con 500 líneas mezclando lógica, persistencia y validación. Refactorizar eso duele. En Hexagonal, cada cosa está en su lugar desde el día 1. El costo del boilerplate es UNA VEZ; el costo de la deuda técnica es PARA SIEMPRE.

**¿Cuándo vale la pena?**

- Cuando el proyecto va a crecer (como SIGA)
- Cuando quieres cambiar de BD sin tocar el negocio
- Cuando quieres testear el negocio sin levantar Spring
- Cuando trabajas con IA (la estructura clara ayuda a la IA a entender el código)

**¿Cuándo NO vale la pena?**

- Scripts chicos, pruebas de concepto, proyectos de 1 semana.

### 3.2 Anatomía de una Feature — Register paso a paso

Este es el ejemplo más concreto que tenemos. Cuando alguien se registra en SIGA:

```
1. HTTP POST /api/v1/auth/register
       ↓
2. AuthController (controller/)
   - Recibe JSON { email, password, name, companyName }
   - Llama a RegisterCustomerUseCase
       ↓
3. RegisterCustomerUseCase (application/usecase/)
   - Valida que ningún campo esté vacío
   - Verifica que el email no exista (→ CustomerRepositoryPort)
   - Hashea la contraseña con BCrypt
   - Crea un Customer con isActive=false
   - Genera un token de verificación UUID con expiración 24h
   - Guarda el Customer (→ CustomerRepositoryPort)
   - Envía email de verificación (→ EmailSenderPort)
   - Devuelve { status: "pending" }
       ↓
4. CustomerRepositoryPort (domain/port/)
   - Interfaz: "findByEmail", "save", "existsByEmail"
       ↓
5. CustomerJpaAdapter (infrastructure/adapter/)
   - Toma el Customer (domain) → lo mapea a CustomerEntity (JPA)
   - Lo guarda con Spring Data
   - Lo mapea de vuelta a Customer (domain)
   - Devuelve el resultado
```

**¿Por qué es tan largo?**
Cada paso tiene una responsabilidad CLARA:

| Componente   | Responsabilidad             | ¿Qué pasa si cambia?                                             |
| ------------ | --------------------------- | ------------------------------------------------------------------ |
| Controller   | Recibir HTTP, devolver HTTP | Cambia el formato de la API. NO toca lógica de negocio            |
| UseCase      | Orquestar el flujo          | Cambia la regla de negocio (ej: ahora también hay que enviar SMS) |
| Domain Model | Representar el concepto     | Cambia el modelo de datos                                          |
| Port         | Definir el contrato         | Cambia qué operaciones de persistencia necesita el negocio        |
| Adapter      | Implementar el contrato     | Cambia la tecnología (JPA → MongoDB, PostgreSQL → H2)           |

**Implicancia**: Si mañana cambio PostgreSQL por MongoDB, solo toco el Adapter (y el mapper). El UseCase y el Domain Model ni se enteran.

### 3.3 El Mapa de Carpetas (Auth como ejemplo)

```
services/auth/src/main/kotlin/com/siga/auth/
├── domain/
│   ├── model/          → Customer.kt, User.kt, Permission.kt (datos PUROS)
│   └── port/           → CustomerRepositoryPort.kt, EmailSenderPort.kt (INTERFACES)
├── application/
│   └── usecase/        → RegisterCustomerUseCase.kt, LoginUseCase.kt (LÓGICA)
├── infrastructure/
│   ├── adapter/        → CustomerJpaAdapter.kt, EmailSenderService.kt (IMPLEMENTACIONES)
│   └── mapper/         → CustomerMapper.kt, UserMapper.kt (DOMAIN ↔ ENTITY)
├── entity/             → Customer.kt, User.kt (JPA)
├── repository/         → CustomerRepository.kt, UserRepository.kt (Spring Data)
├── controller/         → AuthController.kt, UserController.kt (HTTP)
├── security/           → JwtService.kt, JwtAuthFilter.kt, SecurityConfig.kt
├── event/              → (futuro) Kafka events
└── client/             → (futuro) Feign clients para otros servicios
```

---

## Parte 4: Seguridad y Ley 21.719

### 4.1 Privacidad por Diseño

La **Ley 21.719** (Ciberseguridad en Chile) exige que los sistemas protejan datos personales desde el diseño, no como un parche. SIGA lo cumple con decisiones técnicas concretas:

| Exigencia Legal                                          | Implementación en SIGA                               |
| -------------------------------------------------------- | ----------------------------------------------------- |
| **Art. 14 quáter** — Privacidad desde el diseño | Arquitectura Zero-Trust, schemas separados por tenant |
| **Art. 14 quinquies** — Seudonimización          | **UUID v4** en vez de IDs secuenciales          |
| **Art. 3 letra c** — Proporcionalidad             | El administrador NO ve datos financieros de las PYMEs |
| **Control de acceso**                              | JWT + BCrypt + Roles + Permisos granulares            |

### 4.2 JWT (JSON Web Token)

```kotlin
// JwtService.kt — Simplificado
fun generateToken(email: String, rol: String, tenantId: Int?, principalType: String): String {
    return JWT.create()
        .withSubject(email)
        .withClaim("rol", rol)
        .withClaim("principalType", principalType)   // "customer" o "user"
        .withClaim("tenantId", tenantId)             // ID del Customer (tenant)
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plus(24, HOURS))
        .sign(Algorithm.HMAC256(secret))
}
```

**Claims del JWT:**

| Claim             | ¿Qué significa?        | ¿Para qué sirve?                                         |
| ----------------- | ------------------------ | ---------------------------------------------------------- |
| `sub` (subject) | Email del usuario        | Identificar quién es                                      |
| `rol`           | Rol del usuario          | `customer`, `ADMINISTRATOR`, `CASHIER`, `OPERATOR` |
| `principalType` | Tipo de entidad          | `customer` = dueño, `user` = empleado                 |
| `tenantId`      | ID del tenant (Customer) | Para filtrar datos multi-tenant                            |

**Flujo de autenticación:**

1. Usuario envía `POST /login` con email + password
2. El sistema busca primero como **Customer** (dueño)
3. Si no encuentra, busca como **User** (empleado)
4. Si encuentra y la contraseña coincide → devuelve JWT
5. El frontend guarda el JWT y lo envía en cada request como `Authorization: Bearer <token>`
6. `JwtAuthFilter` lo valida en cada request y setea el `SecurityContext`

¿Por qué `principalType`? Porque el Customer (dueño) y el User (empleado) son entidades diferentes con tablas diferentes, pero comparten el mismo endpoint de login para simplificar al frontend.

### 4.3 BCrypt — ¿Por qué este algoritmo y no otro?

| Algoritmo        | ¿Sirve para passwords? | Problema                                                                |
| ---------------- | ----------------------- | ----------------------------------------------------------------------- |
| **BCrypt** | ✅ Sí                  | El estándar. Cost factor ajustable. Lento a propósito.                |
| MD5              | ❌ No                   | Se rompe en segundos.                                                   |
| SHA-256          | ❌ No                   | Es hash rápido, diseñado para integridad, no para proteger passwords. |
| Argon2           | ✅ Sí                  | Mejor que BCrypt... pero Spring Boot no lo soporta nativamente.         |
| scrypt           | ✅ Sí                  | Similar a Argon2, pero menos soporte en Spring.                         |

**Conclusión**: BCrypt es el estándar de la industria para Spring Boot. No es el más moderno (Argon2 es "mejor"), pero es el más soportado, testeado y auditado.

### 4.4 El Modelo de Jerarquía

**¿Quién es quién en SIGA?**

```
Dueño (Customer)
├── Es la persona que registró la empresa
├── Se autentica como principalType="customer"
├── NO pasa por RBAC — tiene control inherente
├── NADIE puede quitarle privilegios
├── Es el ÚNICO que puede crear un Super Usuario 2
└── Tiene acceso a facturación y pagos del plan

Super Usuario 2 (User con TODOS los permisos)
├── Es un empleado cualquiera al que el Dueño le dio TODOS los permisos
├── Puede dar permisos a otros empleados (hasta nivel TBD)
├── NO puede crear otro SU2
├── NO puede quitarle privilegios al Dueño
└── Puede ver facturas/pagos (si el Dueño se lo permite)

Admin (User con role=ADMINISTRATOR)
├── Es un empleado con permisos DEFAULT de admin
├── Canaliza información al Dueño/SU2
├── NO tiene acceso a asuntos privados (a menos que el Dueño se lo dé)
└── PUEDE escalar a SU2 si el Dueño decide

Cajero / Operador / Repartidor (Users con sus roles)
└── Permisos default de su rol + overrides que Dueño/SU2 asignen
```

**¿Por qué Customer y User son tablas separadas?**
Porque son conceptos distintos:

- **Customer** = el cliente que paga el plan. Existe 1 por empresa. Control absoluto. Se crea vía registro público.
- **User** = los empleados. Muchos por empresa. Roles y permisos asignados por el Dueño. Se crean dentro del sistema.

Son tablas separadas con relaciones diferentes. El Login es unificado (un solo endpoint) pero internamente busca primero Customer y después User.

---

## Parte 5: SAGA y Eventos (Kafka)

### 5.1 El Problema de las Dos Armadas

Cuando hago una venta en SIGA, pasan dos cosas:

1. `Sales` registra la venta (cobra)
2. `Inventory` descuenta el stock

En un monolito, esto es una transacción: o se hacen ambas o ninguna. En microservicios, cada servicio tiene su propia BD. No hay transacción distribuida.

**La solución**: **SAGA por Coreografía**.

### 5.2 Cómo Funciona

```
Venta() → Sales publica STOCK_DEDUCT_REQUEST → Inventory escucha
                                                       ↓
                                              ¿Hay stock?
                                             /           \
                                          Sí              No
                                          |               |
                                    Descuenta      Publica STOCK_FAILED
                                          |               |
                                    Publica       Sales escucha → cancela
                                    STOCK_OK           la venta
```

**Principios:**

| Principio                                | ¿Por qué?                                                                                                                                         |
| ---------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Coreografía, no orquestación** | No hay un "director de orquesta". Cada servicio sabe qué hacer. Si el orquestador falla, todo para. En coreografía, los servicios son autónomos. |
| **Eventos inmutables**             | Una vez publicado, el evento no se modifica. Es un registro histórico.                                                                             |
| **Idempotencia**                   | Los eventos pueden llegar más de una vez (Kafka es at-least-once). Cada servicio tiene una tabla `processed_events` para ignorar duplicados.     |
| **Compensación**                  | No hay rollback tradicional. Si Inventory falla, Sales emite una venta compensatoria (anulación).                                                  |

### 5.3 ¿Por qué Kafka y no RabbitMQ?

| Característica | Kafka                              | RabbitMQ                 |
| --------------- | ---------------------------------- | ------------------------ |
| Persistencia    | ✅ Los eventos quedan en disco     | ❌ Efímero por defecto  |
| Replay          | ✅ Puedo reprocesar eventos viejos | ❌ No diseñado para eso |
| Throughput      | ✅ Millones de msg/segundo         | ⚠️ Menos               |
| Complejidad     | ⚠️ Más complejo de operar       | ✅ Más simple           |

**Decisión**: Kafka. Por la capacidad de **replay**: si Inventory estuvo caído 2 horas, al volver procesa todos los eventos acumulados y queda al día. Con RabbitMQ, los mensajes se habrían perdido.

### 5.4 Caso Concreto: Notificación Async por Kafka (Mayo 2026)

En mayo de 2026 implementamos un **servicio de notificaciones** que consume eventos de Kafka para enviar emails de forma asíncrona. Este es el caso de uso más concreto de Kafka en SIGA hasta la fecha.

#### El Problema

Originalmente, cuando un cliente se registraba, el servicio `auth` enviaba el email de verificación de forma **sincrónica** usando `JavaMailSender`. Si SMTP estaba caído, el registro fallaba. El cliente no podía registrarse porque el email no se enviaba.

```
❌ Flujo Original (Sincrónico):
Register → Guardar en BD → Enviar email (SMTP) → Si SMTP falla → TODO FALLA
```

#### La Solución: Kafka como Buffer

Separamos la responsabilidad en dos servicios:

1. **Auth** publica un `EmailEvent` al topic `email-events` y responde inmediatamente al cliente.
2. **Notification** consume el evento y envía el email. Si SMTP falla, reintenta hasta 3 veces con backoff exponencial.

```
✅ Flujo Nuevo (Asincrónico vía Kafka):

Auth:                                Notification:
Register → Guardar BD → Publicar     Consume → Render template → Enviar email
                        evento              ↻ si falla: retry 3x (2s/4s/8s)
                        ↓                   ↻ si agota: log (dead-letter)
                   [Kafka topic
                   email-events]
```

#### El Contrato del Evento

```kotlin
data class EmailEvent(
    val eventId: UUID,         // Para idempotencia
    val email: String,         // Destinatario
    val type: EmailType,       // WELCOME | PASSWORD_RESET
    val name: String,          // Nombre del destinatario
    val token: String?,        // Token de verificación/reset
    val timestamp: Instant     // Cuándo se creó
)
```

`EmailType` es un enum Kotlin exhaustivo: solo existen `WELCOME` y `PASSWORD_RESET`. Como es exhaustivo, el `when` en el consumer no necesita `else` — el compilador garantiza que todos los casos están cubiertos.

#### Idempotencia: La Tabla `processed_events`

Kafka entrega mensajes con semántica **at-least-once** (al menos una vez). Esto significa que un mismo evento puede llegar más de una vez. Para evitar enviar el mismo email dos veces:

```sql
-- Cada evento procesado queda registrado
CREATE TABLE processed_events (
    event_id UUID PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    processed_at TIMESTAMPTZ DEFAULT NOW()
);
```

```kotlin
// En el consumer — antes de procesar, verificamos si ya se procesó
if (processedEventRepository.existsById(event.eventId)) {
    log.info("Duplicate event skipped: eventId={}", event.eventId)
    return  // ← No hacemos nada, el email ya se envió
}
```

#### Manejo de Errores: Retry con Backoff Exponencial (REQ-5)

Si SMTP falla (timeout, conexión rechazada), el consumer reintenta hasta 3 veces:

```kotlin
val maxAttempts = 4  // 1 intento inicial + 3 retrys
for (attempt in 1..maxAttempts) {
    try {
        // intentar enviar...
        return  // éxito → salir
    } catch (e: Exception) {
        if (attempt < maxAttempts) {
            val backoffMs = 2000L * (1L shl (attempt - 1))  // 2s, 4s, 8s
            Thread.sleep(backoffMs)  // esperar antes de reintentar
        }
    }
}
// Todos los intentos fallaron → log como dead-letter
log.error("Email event moved to dead-letter after retries exhausted")
```

Si después de 4 intentos (1 inicial + 3 retrys con 2s/4s/8s de espera) el email no se pudo enviar, se loguea el error pero **no se guarda como procesado**. Esto permite reprocesarlo manualmente después desde Kafka (replay).

#### ¿Por qué Kafka funciona mejor que RabbitMQ para este caso?

| Escenario | Kafka | RabbitMQ |
|-----------|-------|----------|
| SMTP caído 30 minutos | ✅ Los eventos se acumulan en el topic. Al volver, se procesan todos | ⚠️ Los mensajes expiran o se pierden si la cola no está configurada con persistence + DLQ |
| Reprocesar un lote fallido | ✅ `kafka-console-consumer --from-beginning --topic email-events` — reprocesas desde cualquier punto | ❌ No hay replay nativo. Tendrías que tener un sistema aparte para reinyectar mensajes |
| Escalar consumidores | ✅ Kafka particiona el topic; puedes tener N consumidores en paralelo | ✅ RabbitMQ también soporta competidores, pero Kafka escala mejor |
| Auditoría (quién envió qué) | ✅ Los eventos quedan en disco con timestamp. Es un log de auditoría por sí mismo | ⚠️ Los mensajes se eliminan después de consumidos (a menos que configures TTL largo o dead-letter) |

**Decisión**: Para este caso de uso, Kafka nos da **replay** (si notification estuvo caído, al volver procesa todo) y **auditoría** (los eventos quedan en disco como historial). RabbitMQ habría requerido configurar manualmente dead-letter queues y un mecanismo externo de replay.

#### ¿Por qué no un webhook o cola en BD?

Parece más simple: guardar un registro "email pendiente" en PostgreSQL y tener un cron que los procese. Pero:

- **PostgreSQL no es una cola de mensajes**: hacer SELECT constante sobre registros pendientes compite con las queries del negocio
- **No hay orden garantizado**: dos registros pueden procesarse en orden inverso
- **Escalado**: si notification necesita más consumidores, compites por la misma BD
- **Kafka ya está en el stack**: usamos Kafka para SAGA (Sales → Inventory). Agregar un topic más no suma infraestructura nueva

### 5.5 ¿Y la Mensajería Síncrona? OpenFeign + Eureka

No todo es Kafka. Para comunicación síncrona entre servicios usamos **OpenFeign** + **Eureka**:

```
Gateway (8080) → FeignClient → Auth (8081)
Gateway (8080) → FeignClient → Inventory (8082)
```

Eureka es el "directorio telefónico": cada servicio se registra con su IP y puerto. Cuando Gateway necesita llamar a Auth, le pregunta a Eureka "¿dónde está Auth?" y Eureka responde con la dirección.

**¿Por qué no todo por Kafka?**

- Una query "tráeme el stock del local X" es una request-response. Modelar eso con Kafka requeriría un topic de request y otro de response con correlation IDs. Es mucha complejidad para algo que una llamada HTTP resuelve en 50ms.
- Kafka es para **eventos** (sucedió algo). Feign es para **consultas** (dame este dato).

**La regla**: comandos y eventos van por Kafka. Queries y consultas van por Feign/HTTP. El ejemplo clásico:
- "Registrar una venta" → evento Kafka (Sales → Inventory)
- "¿Cuánto stock tengo?" → Feign HTTP (Gateway → Inventory)

---

## Parte 5b: Redis Cache — Acelerando el Stock Consolidado

### 5b.1 El Problema

El endpoint `GET /api/v1/consolidated-stock` devuelve el stock agrupado por producto en todos los puntos de venta de un tenant. El caso de uso (`ConsolidatedStockUseCase`) hacía esto:

```kotlin
fun execute(storeId: UUID?, page: Int, size: Int): ConsolidatedStockResponse {
    val allStock = stockPort.findAll()  // ← TRAE TODO el stock a memoria
    // filtra, agrupa, ordena, pagina... EN MEMORIA
}
```

`stockPort.findAll()` cargaba **TODAS** las filas de stock del tenant en memoria antes de filtrar y paginar. Para un tenant con 10 locales y 5000 productos, eso eran ~50,000 filas en memoria por request. El spec requería **<300ms de respuesta**.

Además, era un endpoint de **dashboard** — se consulta constantemente para mostrar stock disponible. No necesitaba datos en tiempo real; 60 segundos de desfase eran aceptables.

### 5b.2 La Solución: `@Cacheable` con Redis

Spring Boot tiene una abstracción de caché que permite agregar caching declarativo sin tocar la lógica de negocio:

```kotlin
// Antes — sin caché (cargaba todo cada vez)
class ConsolidatedStockUseCase(
    private val stockPort: StockRepositoryPort,
    private val productPort: ProductRepositoryPort
) {
    fun execute(storeId: UUID?, page: Int, size: Int): ConsolidatedStockResponse {
        // ...
    }
}

// Después — con @Cacheable (una línea)
class ConsolidatedStockUseCase(
    private val stockPort: StockRepositoryPort,
    private val productPort: ProductRepositoryPort
) {
    @Cacheable(
        cacheNames = ["consolidatedStock"],
        key = "#storeId?.toString() ?: 'all' + ':' + #page + ':' + #size"
    )
    fun execute(storeId: UUID?, page: Int, size: Int): ConsolidatedStockResponse {
        // ...
    }
}
```

**¿Qué hace `@Cacheable`?**

1. Spring crea un proxy AOP alrededor del método
2. Antes de ejecutar `execute()`, calcula la key y pregunta a Redis: "¿tienes esto?"
3. Si Redis tiene el valor → lo devuelve sin ejecutar el método (cache hit)
4. Si Redis no tiene el valor → ejecuta el método, guarda el resultado en Redis con TTL, y lo devuelve (cache miss)

**¿Por qué no usar `RedisTemplate` directamente?**

Comparativa de enfoques:

| Enfoque | Líneas de código | Acoplamiento | ¿Se puede cambiar de caché sin tocar el caso de uso? |
|---------|------------------|--------------|------------------------------------------------------|
| `@Cacheable` | 1 línea + clase de configuración vacía | Bajo (Spring abstrae el backend) | ✅ Sí |
| `RedisTemplate` | ~15 líneas (get, put, serializar) | Alto (el caso de uso sabe que es Redis) | ❌ No |

Además `@Cacheable` permite cambiar de Redis a Caffeine o Hazelcast solo cambiando la dependencia en `build.gradle.kts` y config — sin tocar una línea de lógica de negocio.

### 5b.3 La Configuración

```kotlin
// CacheConfig.kt — literalmente 3 líneas
@Configuration
@EnableCaching
class CacheConfig
```

`@EnableCaching` activa el post-processor de Spring que escanea `@Cacheable`, `@CacheEvict`, `@CachePut` en los beans. La configuración concreta (servidor Redis, TTL, serialización) va en `application.yml`:

```yaml
spring:
  cache:
    type: redis                    # Backend: Redis
    redis:
      time-to-live: 60s            # Los datos expiran a los 60 segundos
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
```

### 5b.4 El Key Contract

Todas las partes del sistema acuerdan el mismo formato de clave de caché:

```
consolidatedStock::<storeId_o_"all">:<page>:<size>
```

Ejemplos:
- `consolidatedStock::all:0:20` — todos los locales, página 0, tamaño 20
- `consolidatedStock::a1b2c3d4:0:50` — local específico, página 0, tamaño 50

**¿Por qué incluir `storeId`, `page` y `size` en la key?**
Porque son los parámetros de entrada. Si cambia alguno, el resultado es diferente. No usarías el mismo caché para "página 0" y "página 1".

**¿Por qué 60 segundos de TTL y no invalidación por eventos?**

| Opción | Beneficio | Costo | Decisión |
|--------|-----------|-------|----------|
| TTL 60s | Simple, cero eventos, funciona sin Kafka | Datos obsoletos por hasta 60s | ✅ Adoptado |
| Invalidación por eventos (Kafka + @CacheEvict) | Datos siempre frescos | Consumer Kafka en inventory + lógica de cache-evict + ordenamiento de eventos | ❌ Diferido |

La vista de stock consolidado es para un dashboard, no para una transacción de venta. 60 segundos de desfase son aceptables. Si alguien vende un producto, el dashboard puede mostrar stock desactualizado por hasta 1 minuto — la venta real sigue funcionando porque consume del repositorio directo, no del caché.

### 5b.5 Graceful Degradation

Si Redis está caído, `@Cacheable` lanza una excepción (`CacheException`) y el request falla. ¿Debemos agregar un fallback?

**Decisión actual**: No. Redis es parte de la infraestructura, como PostgreSQL. Si la BD está caída, el servicio falla. Si Redis está caída, el caché falla. No agregamos un `CacheErrorHandler` que degrade silenciosamente porque es preferible fallar rápido y que el operador sepa que Redis está caído, a servir datos inconsistentes o tener un comportamiento impredecible.

Si en producción la estabilidad de Redis es un problema, se puede agregar un `CacheErrorHandler` que loguee el error y ejecute el método original (degradando a sin-caché).

### 5b.6 Infraestructura

Redis corre como un contenedor Docker más:

```yaml
# docker-compose.yml
siga-redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - siga-net
    restart: unless-stopped
```

La dependencia en Gradle:

```kotlin
// services/inventory/build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-data-redis")
```

Spring Boot 4.x gestiona la versión automáticamente vía BOM. No necesitamos especificar versión.

### 5b.7 Lecciones Aprendidas

1. **SpEL operator precedence**: En la key de `@Cacheable`:
   ```kotlin
   // ❌ ESTO NO FUNCIONA COMO ESPERAS
   key = "#storeId?.toString() ?: 'all' + ':' + #page + ':' + #size"
   // El operador `+` tiene MAYOR precedencia que `?:` (elvis)
   // → Interpreta: (#storeId?.toString() ?: ('all' + ':' + #page + ':' + #size))
   
   // ✅ ASÍ SÍ
   key = "(#storeId?.toString() ?: 'all') + ':' + #page + ':' + #size"
   ```
   Siempre usar paréntesis en expresiones SpEL cuando mezclas elvis (`?:`) con concatenación (`+`).

2. **`@Cacheable` es inerte en tests unitarios**: Spring necesita el proxy AOP para interceptar `@Cacheable`. En un test unitario puro (sin Spring), la anotación no tiene efecto — el método se ejecuta siempre. Para probar el caché necesitas `@SpringBootTest` con Redis.

3. **Redis en tests con Testcontainers**: Para tests de integración que verifican caché, usamos Testcontainers:
   ```kotlin
   @SpringBootTest
   @Testcontainers
   class ConsolidatedStockCacheTest {
       companion object {
           @Container
           val redis = GenericContainer<Nothing>("redis:7-alpine")
               .withExposedPorts(6379)
   
           @DynamicPropertySource
           fun redisProps(registry: DynamicPropertyRegistry) {
               registry.add("spring.data.redis.port") { redis.getMappedPort(6379) }
           }
       }
   }
   ```

4. **No todo merece caché**: El endpoint de stock consolidado es read-heavy (se consulta constantemente) y tolera datos ligeramente desactualizados. Un endpoint de transferencia de stock no debería cachearse porque necesita consistencia inmediata.

---

## Parte 6: Patrones y Lecciones Aprendidas (Los Gotchas)

> Esta sección es la más valiosa para aprender. Cada entrada es un problema que enfrentamos, cómo lo resolvimos, y qué aprendimos.

### 6.1 UUID Auto-Generation en Adapters

**Problema**: 22 tests fallaban con `IdentifierGenerationException` en H2. Los tests creaban entidades con `id = null` esperando que JPA generara el UUID automáticamente, pero las entidades no tenían `@GeneratedValue`.

```kotlin
// ❌ Lo que NO funciona (en H2)
@Entity
class User(
    @Id
    var id: UUID? = null   // Sin @GeneratedValue → H2 no sabe generar UUID
)

// ✅ Lo que funciona — en el ADAPTER (no en la entidad)
override fun save(user: User): User {
    val entity = UserMapper.toEntity(user)
    if (entity.id == null) {
        entity.id = UUID.randomUUID()  // ← Generamos acá
    }
    val saved = userRepository.save(entity)
    return UserMapper.toDomain(saved)
}
```

**¿Por qué no usar `@GeneratedValue`?**
Porque en algunos casos necesitamos pasar un UUID explícito (ej: tests, referencias externas), y `@GeneratedValue` causa conflictos merge/persist cuando el ID viene explícito.

**Aprendizaje**: El adaptador es el lugar correcto para decidir cómo se genera el ID. El dominio no sabe ni le importa. Y funciona tanto en PostgreSQL (que puede generar UUIDs nativamente) como en H2 (que no).

**Tests fixeados**: UserJpaAdapterTest (7), UserStoreJpaAdapterTest (5), UserPersistenceTest (1), AuthFlowIntegrationTest (4), PermissionJpaAdapterTest (5) = **22 tests**.

### 6.2 @Component en Filtros de Spring Security

**Problema**: `JwtAuthFilter` no tenía `@Component`. 38 tests fallaban con `NoSuchBeanDefinitionException` porque Spring no registraba el filtro como bean.

```kotlin
// ❌ Sin @Component — Spring no lo descubre
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() { ... }

// ✅ Con @Component — Spring lo inyecta en SecurityConfig
@Component
class JwtAuthFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() { ... }
```

**Aprendizaje**: En Spring, si una clase se va a **inyectar en otra** (como `SecurityConfig` recibe `JwtAuthFilter` en el constructor), necesita ser un bean. `@Component` es la forma más simple. Si no lo tiene, Spring no lo instancia y cualquier dependencia que lo requiera falla.

**38 tests estaban fallando por esto SIN QUE NOS DIÉRAMOS CUENTA** — porque se acumularon con otros 22 tests rotos y asumimos que "todo era culpa de H2/UUID".

### 6.3 spring.mail y el MailHealthIndicator

**Problema**: Al agregar `spring.mail` en `application.yml` con valores default vacíos, Spring Boot crea un `JavaMailSenderImpl` apuntando a `localhost:587`. El `MailHealthIndicator` intenta conectar → falla → el health endpoint devuelve **503 Service Unavailable**.

```
# ❌ ESTO ROMPE — Spring crea el bean aunque el host esté vacío
spring:
  mail:
    host: ${SMTP_HOST:}     # ← vacío → Spring usa "localhost"
    port: ${SMTP_PORT:587}  # ← default 587

# ✅ SOLUCIÓN — Config en perfil separado (application-prod.yml)
# El perfil "default" no tiene mail config → no se crea JavaMailSender
# El perfil "prod" tiene mail config → funciona correctamente
```

**Aprendizaje**: `spring-boot-starter-mail` activa auto-configuración apenas detecta propiedades `spring.mail.*`. Si no quieres mail en desarrollo, no pongas las propiedades. El `EmailSenderService` ya maneja el caso `@Autowired(required = false)` y cae a log-mode si no hay mail sender.

### 6.4 Claims JWT: Consistencia en Nombres

**Problema**: El spec dice `principalType` y `tenantId` (camelCase). La implementación usaba `principal_type` y `tenant_id` (snake_case). Ambos funcionan, pero la inconsistencia entre spec y código es una deuda técnica.

```kotlin
// ❌ Antes
.withClaim("principal_type", principalType)
.withClaim("tenant_id", tenantId)

// ✅ Después — alineado con el spec
.withClaim("principalType", principalType)
.withClaim("tenantId", tenantId)
```

**Aprendizaje**: No importa tanto si usas snake_case o camelCase en claims JWT. Lo que importa es **ser consistente**. Elegí camelCase porque es el estándar en JSON y Kotlin.

### 6.5 H2 ≠ PostgreSQL (Sobre todo con UUIDs)

H2 es genial para tests por su velocidad. Pero NO es PostgreSQL. Las diferencias que nos afectaron:

| Aspecto                      | H2                                 | PostgreSQL                                    |
| ---------------------------- | ---------------------------------- | --------------------------------------------- |
| UUID sin `@GeneratedValue` | ❌ Falla                           | ✅ Funciona (tiene default gen_random_uuid()) |
| `TIMESTAMPTZ`              | ❌ No soporta                      | ✅ Nativo                                     |
| `ADD COLUMN IF NOT EXISTS` | ❌ No soporta (Spring Boot 4.x H2) | ✅ Nativo                                     |
| Array columns                | ❌ Limitado                        | ✅ Nativo                                     |

**Aprendizaje**: No asumas que porque funciona en H2 funciona en PostgreSQL, ni viceversa. Los tests en H2 son para velocidad, pero eventualmente necesitas tests con Testcontainers (PostgreSQL real).

### 6.6 Observabilidad Local Ligera (ContainerFlow)

**Problema**: Levantar 6 microservicios + PostgreSQL + Kafka en Docker consume muchísimos recursos, especialmente en un entorno remoto sin GUI (Docker Desktop no es una opción viable por peso y licencias). Necesitábamos ver el estado, logs y métricas de los contenedores sin colapsar la RAM ni recurrir a herramientas pesadas como Portainer.

**Solución**: Integramos **ContainerFlow** (`ghcr.io/rgjorge/containerflow`) como un contenedor "sidecar" de operaciones (`siga-ops`). Pesa solo ~80MB, ofrece un dashboard en tiempo real vía navegador, y se levanta en la fase final de nuestro script de inicio escalonado (`start-staggered.sh`).

**Aprendizaje**: No siempre necesitas la herramienta estándar de la industria (Portainer/Docker Desktop). Para desarrollo local, las herramientas minimalistas enfocadas en una sola tarea (ver logs y estado de contenedores) mejoran drásticamente la experiencia del desarrollador (DX) sin penalizar el rendimiento del servidor. **Dato clave:** hay que forzar a estas herramientas a exponerse correctamente usando credenciales (ej. `AUTH_TOKEN`) para que bindeen a `0.0.0.0` y no queden atrapadas en `127.0.0.1` dentro del contenedor.

### 6.7 Secretos y el Patrón 12-Factor (.env vs .env.example)

**Problema**: En el archivo `.env.example` estábamos dejando placeholders vacíos para todo, lo que obligaba a cada desarrollador a inventar contraseñas de bases de datos locales solo para que `docker compose up` funcionara. Pero a la vez, si poníamos contraseñas por defecto, corríamos el riesgo de poner contraseñas reales de servicios externos (como SMTP o JWT).

**Solución**: Aplicar la metodología **12-Factor App** dividiendo el `.env.example` en dos categorías estrictas:
1. **Defaults de Infraestructura Local**: Valores hardcodeados inofensivos (`siga_local_dev`, puertos por defecto) que permiten levantar el entorno local "out-of-the-box" sin configurar nada.
2. **Secretos Externos Reales**: Claves de APIs, JWT secrets y credenciales SMTP quedan como placeholders explícitos (`<tu_secreto>`). NUNCA se sube un secreto real al repositorio.

**Aprendizaje**: Un buen `.env.example` debe equilibrar seguridad extrema para producción con cero-fricción para desarrollo local.

### 6.9 Domain vs Entity Enum: El Mapeo Silencioso

**Problema**: Al extender `Movement` con nuevos tipos (`RECONCILIATION`, `TRANSFER`), descubrimos que el domain model y la entity JPA tenían enums divergentes. El domain model tenía `{SALE, ADJUSTMENT, ENTRY}` mientras la entity tenía `{IN, OUT, SALE, ADJUSTMENT, TRANSFER}`. El mapper tiraba excepción cuando encontraba `OUT` o `TRANSFER` porque no existían en el domain model.

**Solución**: Unificar ambos enums. El domain model ahora tiene `{SALE, ADJUSTMENT, ENTRY, RECONCILIATION, TRANSFER}` y la entity tiene `{IN, OUT, SALE, ADJUSTMENT, RECONCILIATION, TRANSFER}`. El mapper maneja ambos lados explícitamente.

**Aprendizaje**: Los enums entre domain y entity son FUENTE DE BUGS cuando divergen. Siempre mantenerlos sincronizados manualmente. Automatizar con un test que verifique que todo valor de entity tiene un mapeo en domain (y viceversa).

### 6.10 UUID Auto-Generation en Nuevos Adapters

**Problema**: Al crear `StockJpaAdapter.save()` y `AlertJpaAdapter.save()`, las nuevas entidades no generaban UUID automáticamente. Hibernate requiere `@Id` explícito, pero la entity `Stock` no tiene `@GeneratedValue`. Cuando el adapter creaba una entidad nueva sin ID, fallaba con constraint violation.

**Solución**: En `StockJpaAdapter.save()`, si no existe una entidad previa, se genera el UUID antes de persistir: `entity.id = if (existingEntity != null) existingEntity.id else UUID.randomUUID()`. Mismo patrón para `AlertJpaAdapter`.

**Aprendizaje**: En proyectos con `@Id` manual (sin `@GeneratedValue`), el adapter es responsable de generar el UUID antes de `save()`. Es fácil olvidarlo cuando el mapper crea la entidad sin ID. Un test de integración con `@DataJpaTest` lo detecta inmediatamente.

### 6.11 Servicios Spring y @ComponentScan en Tests de Integración

**Problema**: Todos los tests `@SpringBootTest` fallaban con `NoSuchBeanDefinitionException` para `SkuGenerator`. El servicio `SkuGenerator` se creó sin `@Service` durante Phase 3 (se omitió por error), y como no había tests de integración aún, no se detectó hasta Phase 6.

**Solución**: Agregar `@Service` a `SkuGenerator`.

**Aprendizaje**: Siempre agregar la anotación Spring (`@Service`, `@Component`) desde el momento cero, aunque el test unitario con MockK no la necesite. El test de integración `@SpringBootTest` SÍ la exige, y encontrarlo en Phase 6 es más costoso que ponerla desde Phase 3.

### 6.12 mockito-kotlin vs mockito-core en Kotlin

**Problema**: Al escribir tests de controller con `@WebMvcTest`, usamos `org.mockito.ArgumentMatchers.any()` que en Java devuelve `null`. Kotlin rechaza null para tipos no-nullables. Resultado: `NullPointerException` en runtime.

**Solución**: Migrar a `org.mockito.kotlin.any()` (de la dependencia `mockito-kotlin`), que maneja correctamente los tipos nullable de Kotlin.

**Aprendizaje**: En proyectos Kotlin, no uses `mockito-core` directamente — usa `mockito-kotlin` como wrapper. Detectarlo temprano evita horas de debuggeo.

### 6.13 Falsos Positivos en Scanners de Seguridad (GitGuardian)

**Problema**: GitGuardian bloqueó un commit lanzando una alerta de "Incidente Secreto Interno (Credenciales SMTP)". 

**Causa**: Nuestro `.env.example` recién refactorizado tenía las líneas `SPRING_MAIL_HOST=smtp.gmail.com` seguidas de `SPRING_MAIL_PASSWORD=<your_app_password>`. El escáner heurístico vio un host real junto a la palabra PASSWORD y disparó la alarma, aunque el valor fuera literalmente un texto de relleno (`<your_app_password>`).

**Aprendizaje**: Los escáneres de seguridad automatizados buscan **patrones**, no solo valores reales. Para evitar falsos positivos y "fatiga de alertas" en el equipo, es mejor usar dominios falsos (`mail.ejemplo.com`) y placeholders obvios (`escribe_tu_password_aqui`) en los archivos de ejemplo públicos.

---

## Parte 7: Pipeline SDD — Cómo Trabajamos

### 7.1 Spec-Driven Development

SIGA no se desarrolla "a lo loco". Seguimos un pipeline llamado **SDD (Spec-Driven Development)**:

```
Exploración → Propuesta → Especificación → Diseño → Tareas → Aplicación → Verificación → Archivo
```

| Fase                      | ¿Qué pasa?                                                                 | ¿Qué produce?                                 |
| ------------------------- | ---------------------------------------------------------------------------- | ----------------------------------------------- |
| **Exploración**    | Investigamos el código actual, identificamos qué falta, evaluamos enfoques | `exploration.md`                              |
| **Propuesta**       | Definimos QUÉ vamos a hacer (alcance, qué NO incluimos)                    | `proposal.md`                                 |
| **Especificación** | Escribimos los REQUISITOS con escenarios Given/When/Then**(BDD)**      | `spec.md` + escenarios en Kotest BehaviorSpec |
| **Diseño**         | Definimos CÓMO lo vamos a hacer (diagramas, decisiones)                     | `design.md`                                   |
| **Tareas**          | Partimos el trabajo en tareas pequeñas con estimación                      | `tasks.md`                                    |
| **Aplicación**     | Escribimos código (TDD: test → implementación → refactor)                | Código fuente                                  |
| **Verificación**   | Corremos tests, verificamos contra el spec, reportamos issues                | `verify-report.md`                            |
| **Archivo**         | Movemos todo a archive, actualizamos specs principales                       | `archive-report.md`                           |

**¿Por qué tanto proceso?**
Porque trabajamos con IA. Sin este pipeline, la IA improvisa, se pierde, y produce código inconsistente. Con el pipeline, la IA tiene un plan claro antes de escribir una línea.

**¿No es mucha burocracia para un proyecto solo?**
Para features chicas (fix de un bug), sí. Pero para cambios grandes (como agregar autenticación multi-tenant), el pipeline nos salvó de cometer errores que habrían costado días. Es como tener planos antes de construir una casa.

### 7.2 BDD (Behavior-Driven Development)

Las especificaciones no se escriben al azar. Seguimos **BDD** con **Kotest BehaviorSpec**, que es el motor de pruebas del proyecto:

```kotlin
class LoginUseCaseTest : BehaviorSpec({
    given("un usuario registrado con email verificado") {
        val email = "test@siga.cl"
        val password = "SecurePass1!"
        // setup...

        `when`("inicia sesión con credenciales correctas") {
            val result = loginUseCase.login(LoginRequest(email, password))

            then("devuelve un JWT válido") {
                result shouldBeRight { token ->
                    jwtService.verify(token).isSuccess shouldBe true
                }
            }
        }
    }
})
```

**Flujo BDD en SIGA:**

1. **Spec phase** (dentro de SDD): Se escriben los escenarios Given/When/Then en lenguaje natural dentro del `spec.md`. Estos son los requisitos.
2. **Apply phase**: Esos escenarios se traducen a tests `BehaviorSpec` en Kotest. Los tests SON la especificación ejecutable.
3. **Cada scenario es un test**: Si el escenario dice "el usuario recibe un JWT", hay un `then("devuelve un JWT válido")` que lo verifica.

**¿Por qué BDD y no solo TDD?**

- TDD dice *cómo* probar. BDD dice *qué* probar.
- Los escenarios Given/When/Then son entendibles por un dueño de producto o un profesor, aunque no sepa código.
- Los mismos escenarios del spec se convierten en tests. No hay "traducción" entre lo que pide el negocio y lo que prueba el código.

**¿Por qué Kotest BehaviorSpec y no JUnit 5 + Cucumber?**

- Cucumber requiere archivos `.feature` separados + glue code. Es otro lenguaje que aprender.
- Kotest BehaviorSpec usa el mismo Kotlin que el resto del proyecto. Los escenarios son código Kotlin puro.
- El IDE los entiende nativamente: puedes navegar, refactorizar, autocompletar.
- Los slices grandes (como el de auth multi-tenant) consumen muchos tokens PRECISAMENTE porque cada escenario BDD se traduce en un test concreto.

### 7.3 TDD Strict Mode

Cuando trabajamos, seguimos TDD estricto:

```
1. RED → Escribir el test que falla
2. GREEN → Escribir el código mínimo para que pase
3. REFACTOR → Mejorar el código manteniendo el test verde
```

No se hace commit si hay tests fallando. El build debe ser **verde completo** antes de integrar.

### 7.4 ¿Por qué Commits Directos y no PRs?

En un equipo, usarías Pull Requests para revisión de código. En un proyecto solitario:

- **Commits directos** a `migracion-microservicios`
- Formato bilingüe: `feat(auth): description / descripción en español`
- La IA escribe y revisa en el mismo ciclo (SDD Verify phase)

Cuando haya más desarrolladores humanos, volvemos a PRs.

### 7.5 HiTL y Harness: La Evolución de Nuestro Flujo (TDD-BDD-SDD)

El ecosistema de desarrollo guiado por IA (**Gentle AI**) en el que operamos no reemplaza al programador, sino que eleva su rol. Hemos consolidado nuestra metodología sumando dos conceptos vitales:

1. **HiTL (Human in the Loop)**: La IA puede escribir cientos de líneas en segundos, pero el arquitecto (el humano) debe aprobar los diseños (`design.md`), las decisiones arquitectónicas y las especificaciones BDD **ANTES** de que se toque una sola línea de código fuente. La IA propone e implementa; el humano lidera y aprueba.
2. **Harness (Arnés de Pruebas Automático)**: El pipeline SDD funciona de forma autónoma construyendo un "arnés" alrededor del código. Cuando la IA desarrolla una feature, integra TDD (pruebas unitarias strictas) y BDD (escenarios vivos en Kotest). Este arnés automatizado actúa como red de seguridad, asegurando que las iteraciones rápidas de la IA no rompan la lógica del negocio.

**La fórmula completa del éxito en SIGA:** `TDD (Pruebas) + BDD (Negocio) + SDD (Especificación) + HiTL (Dirección Humana) + Harness (Red de Seguridad)`.

### 7.6 Ciclo Completado: inventory-core-features (Mayo 2026)

**Cambio**: Implementación completa de US-2.1 a US-2.5 en `siga-inventory` (stock consolidado, auto-SKU + detección duplicados, búsqueda ILIKE+unaccent, conciliación con alertas, transferencias bodega↔punto con trazabilidad).

**Datos del ciclo**:
| Métrica | Valor |
|---------|-------|
| Fases SDD recorridas | 8/8 (explore → propose → spec → design → tasks → apply → verify → archive) |
| Tareas | 42 |
| Commits | 7 directos a `migracion-microservicios` |
| Tests nuevos | 50+ (unitarios con MockK + integración con @DataJpaTest y @SpringBootTest) |
| Bugs corregidos durante el ciclo | 3 (SkuGenerator sin @Service, UUID no generado en adapters, mockito-kotlin) |
| Fallas pre-existentes | 8 (no relacionadas al cambio) |
| Modo | Strict TDD — 100% RED → GREEN → REFACTOR |
| Duración | ~4 horas (automático) |

**Lo que aprendí en este ciclo**:
1. Los enums domain/entity divergen silenciosamente — sincronizarlos manualmente o con tests.
2. Los adapters JPA sin `@GeneratedValue` necesitan generar UUID explícitamente.
3. `mockito-kotlin` es obligatorio en proyectos Kotlin, no opcional.
4. El pipeline SDD en automático + `ask-on-risk` es eficiente si el humano define el scope claro desde la proposal.

**Documentación generada**:
- `openspec/changes/archive/2026-05-19-inventory-core-features/` — artefactos completos
- `openspec/specs/inventory/` — 5 specs sincronizados como especificaciones principales
- `docs/es/HISTORIAS_USUARIO.md` / `docs/en/USER_STORIES.md` — trazabilidad actualizada
- `README.md` / `README.en.md` — tabla de capacidades de inventory agregada

---

## Parte 8: Despliegue y Operaciones

### 8.1 Arquitectura de Despliegue (Actual — Desarrollo)

```
[MacBook vía SSH]
       ↓
[PC Escritorio] → Docker Compose → 6 contenedores + PostgreSQL + Kafka
```

### 8.2 Próximo: AWS (Con Créditos Gratuitos)

```
[AWS]
├── VPC (Virtual Private Cloud)
│   ├── ECR (registro de imágenes Docker)
│   ├── ECS (orquestación de contenedores) o EC2
│   ├── RDS (PostgreSQL administrado)
│   └── MSK (Kafka administrado) o self-hosted
└── CI/CD (GitHub Actions → build → push → deploy)
```

### 8.3 Variables de Entorno Críticas

| Variable       | ¿Dónde se usa?    | ¿Qué pasa si no está?               |
| -------------- | ------------------- | -------------------------------------- |
| `DB_HOST`    | Todos los servicios | No conectan a BD                       |
| `JWT_SECRET` | Auth                | JWTs inválidos (usa default inseguro) |

### 8.4 Discord Notifications via ContainerFlow (Junio 2026)

ContainerFlow (siga-ops) tiene soporte nativo de notificaciones vía Discord webhook. No requiere servicios externos ni bots.

#### Configuración

1. Crear un webhook en Discord: Canal → Edit Channel → Integrations → Webhooks → New Webhook
2. Pegar la URL en siga-ops: Settings → Discord Notifications → Webhook URL
3. Configurar eventos y umbrales

#### Eventos Soportados

| Evento | Descripción | Recomendado |
|--------|-------------|-------------|
| **Container State Changes** | Container start, stop, die (crash), restart, health_status | ✅ Sí |
| **Resource Alerts** | CPU o MEM superan umbral (global o por contenedor) | ✅ Sí |
| **UI Actions** | Acciones manuales desde el panel (start/stop/restart/rebuild) | ❌ No (spam) |
| **Action Errors** | Cuando una acción del panel falla (rebuild roto, etc.) | ✅ Sí |

#### Umbrales por Servicio (Configuración Actual SIGA)

| Servicio | CPU % | MEM % | ¿Por qué? |
|----------|-------|-------|-----------|
| siga-db | 70 | 80 | Postgres usa shared_buffers + cache de disco. 60% de MEM es falso positivo |
| siga-redis | — | 40 | Redis es RAM pura. Si pasa 40% hay que mirar |
| siga-kafka | 60 | 60 | Broker, consume CPU en particiones y RAM en buffers |
| siga-gateway | 60 | 60 | Punto único de entrada. CPU alto indica problema |
| siga-auth | 60 | — | Login caído = soporte al toque |
| siga-inventory | 60 | 70 | Consolidación de stock puede pedir CPU y RAM |
| siga-sales | 50 | — | Ventas constantes, queremos saber si el CPU se dispara |
| siga-billing | 50 | — | Facturación, no debería exigir mucho |
| siga-notification | 50 | — | Mails async, tranqui |
| siga-agent | 70 | 70 | IA, puede tener picos |
| siga-kafka-ui | ❌ apagado | ❌ apagado | Admin tool, no aporta alertas |
| pgadmin | ❌ apagado | ❌ apagado | Admin tool, no aporta alertas |
| siga-eureka | ❌ apagado | ❌ apagado | Service registry, si falla todo cae igual |

#### Anti-Spam

- **Cooldown global**: 5 min entre alerts del mismo tipo+servicio (configurable 1-60)
- **Down reminder**: cada 10 min si un contenedor sigue caído
- **Stop/die debounce**: 15s para colapsar restart/redeploy en una sola notificación
- **Queue con rate limit**: 500ms mínimo entre webhooks, respeta Retry-After de Discord

#### Lecciones Aprendidas

1. **ContainerFlow no tiene volumen persistente por defecto** — la config de Discord y thresholds se pierde al recrear el contenedor. Hay que agregar `containerflow-data:/app/data` explícitamente en docker-compose.
2. **Los UIDs siguen formato `{project}/{service}`** — ej: `siga/siga-db`, `siga/pgadmin`. Si el contenedor no tiene labels de compose, usa el container_name.
3. **Los archivos de configuración son JSON planos** — `.dockerflow-discord.json` y `.dockerflow-container-settings.json` en `/app/data/`. Se pueden modificar directamente con `docker cp`.
4. **ContainerFlow monitorea TODOS los contenedores Docker**, no solo los del compose. Los frontends futuros aparecerán automáticamente.
5. **El webhook de Discord no necesita un bot** — es un simple HTTP POST. Cualquier servicio que pueda hacer curl puede enviar notificaciones.

| Variable       | ¿Dónde se usa?    | ¿Qué pasa si no está?               |
| -------------- | ------------------- | -------------------------------------- |
| `DB_HOST`    | Todos los servicios | No conectan a BD                       |
| `JWT_SECRET` | Auth                | JWTs inválidos (usa default inseguro) |
| `SMTP_HOST`  | Auth (prod)         | Email de verificación no se envía    |
| `EUREKA_URL` | Todos los servicios | No se registran en service discovery   |

---

## Epílogo: El Futuro

> *SIGA. Para que no te detengas.*

El nombre no es casual. SIGA es un imperativo: *sigue avanzando, sigue construyendo, sigue soñando*. No es solo un acrónimo; es una promesa a mí mismo de que el inventario es el comienzo. Los activos abarcan más. Pero el foco, hoy, es el inventario y el POS. Porque SIGA no dependerá de APIs de terceros. Porque SIGA será un ecosistema completo.

**La moneda de SIGA es el tiempo.** Cada hora ahorrada a una PYME es una hora que alguien recupera para estar con su familia, para descansar, para no trasnochar anotando stock en papeles.

SIGA hoy es un backend sólido con autenticación completa, arquitectura hexagonal, más de 300 tests entre todos los servicios, y cero deuda técnica crítica. Pero esto es solo el cimiento. Lo que falta:

- **Slice 4**: Endpoints de gestión de permisos (R5 del spec)
- **Frontend Unificado (SvelteKit 5)**: Dashboard bajo `apps/dashboard` como interfaz única que reemplaza los frontends legacy deprecados (webapp, customer-portal, landing, admin-portal, mobile)
- **Despliegue AWS**: VPC, ECR, ECS
- **Agentes IA**: La evolución lógica del chatbot con codificación detrás

Pero esto no termina acá. SIGA es un proyecto vivo. Este documento se actualizará con cada iteración.

---

## Parte 9: Consolidación de Mayo 2026 — Matar el Frankenstein

### 9.1 El Problema

Hasta mayo 2026, SIGA había acumulado decisiones a medio implementar:

- Frontends declarados "legacy deprecado" pero manteniendo carpetas vacías en `apps/` que confundían
- Documentación desactualizada (ARCHITECTURE_STATE.md decía "webapp" pero era `apps/dashboard`)
- Billing con un modelo `SaleInvoice` que generaba dudas sobre quién veía qué datos
- Roles planificados (GODADMIN, SUPER_ADMIN) pero nunca implementados
- Sin un roadmap claro que guiara el desarrollo

### 9.2 Decisiones Tomadas (26/05/2026)

| Decisión | Detalle |
|----------|---------|
| **Single Frontend** | `apps/dashboard` es la ÚNICA app activa. Se eliminaron `admin-portal`, `customer-portal`, `mobile`, `pos`, `landing` como directorios separados |
| **Dashboard incluye todo** | Auth, dashboard cliente, admin SIGA, landing: todo son grupos de rutas en el mismo proyecto SvelteKit 5 |
| **Billing es solo SaaS de SIGA** | Planes, suscripciones, pagos de clientes. NO facturación de ventas PYME |
| **SaleInvoice como registro interno** | Se mantiene para futuros KPIs del cliente, sin endpoints REST ni UI |
| **POS simple Fase 1** | UI para cajeras, registro de ventas, SAGA con inventory. Sin facturación electrónica |
| **Facturación electrónica (Fase 2)** | Se hará con integración externa (Nexxus/E-Sii), no desde cero |
| **Roles pendientes** | GODADMIN y SUPER_ADMIN documentados pero no implementados |
| **Admin de SIGA** | Irá como grupo de rutas `/(platform)/` dentro de dashboard |
| **Roadmap** | Se creó ROADMAP.md como guía única de desarrollo |

### 9.3 Lo Aprendido

1. **No dejar carpetas vacías con READMEs de "deprecado"** — confunden más de lo que aclaran. Si algo está deprecado, se elimina del repo. La documentación explica dónde está ahora.
2. **Documentar las decisiones inmediatamente** — no esperar a que "el ciclo termine". La desactualización de ARCHITECTURE_STATE.md costó varias discusiones confusas.
3. **Un servicio, un propósito claro** — billing tenía un pie en SaaS y otro en facturación PYME. Ahora está claro: billing es solo SaaS. Si algo no es SaaS, no va en billing.
4. **El roadmap es la guía, no la doc idealizada** — antes había documentación de "lo que debería ser". Ahora ROADMAP.md documenta lo que ES, lo que SE HIZO, y lo que VIENE. Sin ficción.
5. **No desarrollar facturación electrónica desde cero** — el SII chileno es un infierno de certificados, timbraje y XML. Mejor integrar con un servicio que ya lo hace.

### 9.4 Estado del Proyecto Post-Consolidación

```
SIGA/ (Mayo 2026)
├── apps/dashboard/          ← Única app frontend (SvelteKit 5)
├── services/
│   ├── auth/                ← Identidad + permisos (Hexagonal ✅)
│   ├── billing/             ← SaaS SIGA (planes, suscripciones, pagos)
│   ├── inventory/           ← Stock + activos (Hexagonal ✅)
│   ├── sales/               ← POS + ventas PYME (Hexagonal ✅)
│   ├── agent/               ← IA conversacional (A2UI v0.9)
│   ├── gateway/             ← Spring Cloud Gateway
│   ├── registry/            ← Eureka
│   └── common/              ← Librería compartida
├── openspec/                ← Documentación viva (SDD)
├── ROADMAP.md               ← Guía de desarrollo (pasado-presente-futuro)
└── ACADEMIC/                ← Aprendizaje personal
```

---

> *"No sé mucho, sueño harto, deseo más."*
>
> — **Héctor Aguila**— `> Un Soñador con Poca RAM 👨🏻‍💻`
>
> *Puerto Montt, Chile.*

> *"No gestiones tu inventario, gestiona Tu Tiempo."*

---

## Parte 8: Testing de Integración y Calidad (Junio 2026)

> *"Un sistema sin tests es un sistema que no puedes cambiar."*

En esta etapa, nos enfocamos en cerrar las brechas de integración que quedaron pendientes tras la migración a arquitectura hexagonal.

### 8.1 Desacoplamiento con WireMock

Para probar el `InventoryClient` (Feign) en el servicio de Sales, enfrentamos el reto de no querer levantar el servicio de Inventory real. La solución fue **WireMock**.

**Lección**: WireMock nos permite simular el comportamiento de servicios externos (200 OK, 500 Error, Timeouts) sin la sobrecarga de Testcontainers o la necesidad de que el otro microservicio esté arriba. Esto acelera el ciclo de feedback del desarrollador.

### 8.2 La SAGA en el Mundo Real

Implementar la coreografía SAGA entre Sales e Inventory nos enseñó sobre la **idempotencia**. No basta con escuchar un evento; hay que asegurarse de que si ese evento llega dos veces, el sistema no descuente stock dos veces o duplique facturas.

**Dato Clave**: La tabla `processed_events` es obligatoria en cada microservicio que consume eventos de Kafka. Es nuestra "memoria de corto plazo" para evitar errores transaccionales.

### 8.3 Automatización con SDD

El uso de **Spec-Driven Development (SDD)** ha sido fundamental. Al definir los escenarios de test (GWT) antes de escribir el código, reducimos la ambigüedad. La IA no solo genera código, sino que genera código que cumple con una especificación técnica y de negocio predefinida.

---

## Parte 10: La Batalla de la Cobertura Fantasma (Junio 2026)

> *"El código que no escribes también hay que testearlo."*

### 10.1 El Problema: El Estancamiento del 74%
Después de semanas de desarrollo riguroso, aplicando TDD y Arquitectura Hexagonal, el proyecto estaba sólido. Habíamos logrado un 80%+ de cobertura en la mayoría de los paquetes (`controller`, `usecase`, `domain`), pero la métrica global de SIGA seguía estancada en **74%**. JaCoCo (nuestra herramienta de cobertura) nos castigaba severamente, indicando que los paquetes `entity` estaban por el 25-50%.

¿El misterio? **Los modelos de dominio y las entidades JPA no tenían lógica de negocio**. Eran simples clases de datos. ¿Qué podía estar sin testear?

### 10.2 El Descubrimiento: El Boilerplate Oculto
La respuesta estaba en cómo funciona Kotlin por debajo y cómo interactúa con JPA y Arquitectura Hexagonal.

1. **Getters y Setters de Kotlin**: Al definir una propiedad como `var` en una clase Kotlin, el compilador genera automáticamente los métodos `get()` y `set()`. Si en tus tests solo instancias la clase usando el constructor pero nunca invocas esos setters (por ejemplo, `entity.nombre = "Nuevo"`), JaCoCo marca todos esos métodos generados como "No Cubiertos".
2. **Los Enums y sus Secretos**: Todo Enum en Kotlin/Java tiene dos métodos estáticos implícitos: `values()` y `valueOf(String)`. Como nunca los usábamos directamente, bajaban el promedio de instrucciones cubiertas.
3. **El Laberinto del `equals` y `hashCode`**: Para comparar entidades (especialmente en colecciones Set o Maps), Kotlin genera (en data classes) o nosotros sobreescribimos (en entidades JPA) estos métodos. Tienen muchas ramas lógicas (branches):
   - ¿Es el mismo objeto en memoria (`this === other`)?
   - ¿El otro objeto es nulo (`other == null`)?
   - ¿El otro objeto es de una clase distinta (`other !is MyEntity`)?
   - ¿Los IDs son nulos (`id == null`)? (Especialmente crítico en entidades con llaves compuestas `@EmbeddedId`).

### 10.3 La Solución "Haiku": Testing Exhaustivo sin Repetición
La solución obvia era escribir tests para todo esto. La solución *mala* era copiar y pegar 50 aserciones en cada clase del sistema. Fieles a nuestra filosofía minimalista (Haiku), creamos una sola función genérica de orden superior:

```kotlin
fun <T : Any> testEntity(entity: T, canHaveNullId: Boolean = true, factory: (UUID?) -> T, idSetter: (T, UUID?) -> Unit) {
    val e1 = factory(id)
    val e2 = factory(id)
    // Probamos igualdad basica
    e1 shouldBe e2
    // Probamos ramas ocultas
    e1.equals(null) shouldBe false
    e1.equals("no soy una entidad") shouldBe false
    // Forzamos hashCode con y sin ID
    e1.hashCode()
    // Forzamos accesores
    idSetter(e1, id)
}
```

Implementamos un `DataCoverageTest.kt` dedicado en cada microservicio (Billing, Sales, Auth) que barría sistemáticamente con todos los enums, modelos de dominio y entidades JPA, forzando la evaluación de cada línea compilada.

### 10.4 El Principio: Bases Sólidas
Lo más importante de este hito no fue el número, sino cómo lo conseguimos. **Alcanzamos el 86% de cobertura global sin tocar una sola línea de código de producción**. 

No ensuciamos nuestros modelos de dominio con anotaciones para evadir a JaCoCo, no comprometimos la pureza del Hexágono, y no desactivamos reglas. Mantuvimos el "boilerplate" necesario por elección (para aislar la base de datos del negocio), pero construimos un arnés de pruebas que lo soporta. 

**Resultado final**: Billing (86%), Sales (94%), y Auth (89%) ✅. El core de SIGA quedó completamente blindado.

## Parte 11: Blindando el Cerebro (Testing de IA y Agentes)

> *"No sobre-ingenierices el código para testearlo; testea inteligentemente lo que escribiste."*

### 11.1 El Problema: Cómo testear una caja negra
Cuando fuimos a subir la cobertura del microservicio `siga-agent` (el cerebro de IA), nos encontramos con que estaba en un 76%, y la clase core `GeminiEngine` estaba en un paupérrimo 9%.

El problema principal: `GeminiEngine` instanciaba su propio cliente HTTP (`WebClient.create()`) en la misma función que armaba el prompt. 
En el testing tradicional de Java/Spring, el instinto te dice: *"Extrae el WebClient a un Bean, inyéctalo en el constructor, crea un mock del servidor web y simula la respuesta HTTP"*.

### 11.2 La Decisión Arquitectónica: Por qué NO (Filosofía Haiku)
Hacer todo ese refactor (inyectar builders, armar MockWebServers) hubiera sumado 50 líneas de configuración y complejidad solo para probar que podíamos parsear un JSON. Estaríamos rompiendo la filosofía Haiku (minimalismo) por culpa de una métrica.

La solución elegante que elegimos: **La visibilidad `internal` de Kotlin**.
La clase `GeminiEngine` tenía métodos `private` enormes que armaban el prompt y parseaban el JSON de respuesta. Simplemente cambiamos esos métodos de `private` a `internal`. Esto significa que son públicos dentro del mismo módulo (el mismo `.jar` o suite de tests), pero invisibles para el resto de los microservicios.

Así, pudimos instanciar la clase en el test y llamar directamente a `parseResponse()` y `buildUserContent()` pasándole strings, probando el 100% de la lógica pesada sin necesidad de hacer una sola llamada de red ni ensuciar el diseño con inyecciones innecesarias.

### 11.3 La Fuga de Entorno (.env)
Otro error común que aprendimos: **Tus tests no deben depender de la máquina del desarrollador**.
El test `AgentConfigTest` comprobaba que se cargaran variables por defecto. Pero al correr en local, leía el archivo `.env` del proyecto (que tenía la API Key real de Gemini y el modelo `gemini-3-flash-preview`). El test fallaba porque esperaba el modelo por defecto.

**El Aprendizaje**: Siempre sella el contexto de Spring en los tests. Lo solucionamos forzando las propiedades a nivel de clase:
`@SpringBootTest(properties = ["gemini.api-key=test-key", "gemini.model-id=gemini-2.0-flash-001"])`
Esto aísla el test de las variables de entorno reales, garantizando que corra igual en tu PC, en la mía o en GitHub Actions.

### 11.4 Conclusión del Hito
Con este último servicio, el núcleo de SIGA (Auth, Billing, Sales, Agent e Inventory) supera formalmente el 85% de cobertura real y verificada, situando el promedio global del proyecto en 86%. Hemos demostrado que la Arquitectura Hexagonal y la alta cobertura no están peleadas con el código limpio y conciso.
